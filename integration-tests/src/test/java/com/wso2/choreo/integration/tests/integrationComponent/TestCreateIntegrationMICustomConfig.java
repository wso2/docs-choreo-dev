/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.integrationComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.DeploymentStatusByVersionFailureException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCreateIntegrationMICustomConfig extends TestNGCitrusSpringSupport {

    public static final String MI_API_SERVICE = Constant.AppType.MI_API_SERVICE.value;
    public static final String API_INVOCATION_REQUEST_URI = "/";
    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private String githubOrg;
    private List<Environment> environments;
    private List<Endpoint> endpoints;
    private static ChoreoComponent testComponent;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ComponentDeploymentStatusDTO componentDeploymentStatusDTO;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void setup() throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_TestCreateIntegrationMICustomConfig() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = { "createProject_TestCreateIntegrationMICustomConfig" })
    @CitrusTest
    public void createComponent_TestCreateIntegrationMICustomConfig() throws Exception {

        // Creating component
        String componentName = "MIConfig".concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-mi-config-test";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .apiName(componentName.toLowerCase())
                .orgId(Integer.parseInt(orgId))
                .orgHandler(orgHandle)
                .displayName(componentName)
                .componentType(MI_API_SERVICE)
                .projectId(projectId)
                .srcGitRepoUrl(srcGitHubURL)
                .repositorySubPath("")
                .repositoryBranch(repoBranch)
                .build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = { "createComponent_TestCreateIntegrationMICustomConfig" })
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationMICustomConfig() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationMICustomConfig"})
    @CitrusTest
    public void generateEndpointsDev_TestCreateIntegrationMICustomConfig() throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        dto.setComponentId(testComponent.getId());
        dto.setLatestVersionId(testComponent.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, projectId, testComponent.getId(), runId, 50);
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("commitHash", testComponent.getLatestCommitHash(testComponent.getCommitHistory(accessToken)));
        GraphQL.generateEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"generateEndpointsDev_TestCreateIntegrationMICustomConfig"})
    @CitrusTest
    public void getEndpointsDev_TestCreateIntegrationMICustomConfig() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = {"getEndpointsDev_TestCreateIntegrationMICustomConfig"})
    @CitrusTest
    public void updateEndpointsDev_TestCreateIntegrationMICustomConfig() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        final Endpoint endpoint = endpoints.get(0);
        argMap.put("endpointId", endpoint.getId());
        argMap.put("displayName", endpoint.getDisplayName());
        argMap.put("apiContext", endpoint.getApiContext());
        argMap.put("apiDefinitionPath", endpoint.getApiDefinitionPath());
        argMap.put("visibility", Constant.EndpointVisibility.PUBLIC.value);
        Endpoint updatedEndpoint = GraphQL.updateEndpoint(this, choreoProjectsTestClient, accessToken, argMap);
        endpoints.set(0, updatedEndpoint);
    }

    @Test(dependsOnMethods = { "updateEndpointsDev_TestCreateIntegrationMICustomConfig" })
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationMICustomConfig() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                testComponent);
        List<Commit> commitHistory = GraphQL.getCommitHistory(this, choreoProjectsTestClient, testComponent.getId(), accessToken,
                testComponent.getRepository().getBranchApp());

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        componentDeploymentStatusDTO = ComponentUtils.deployBuiltComponent(this, citrusClients, accessToken, testComponent, latestCommit,
                environments);
        try {
            ComponentUtils.validateComponentDeployment(this, citrusClients, accessToken, testComponent, latestCommit, environments);
        } catch (Exception e) {
            if (e.getCause() instanceof DeploymentStatusByVersionFailureException) {
                log.error("DeployStatusByVersion failure detected", e);
            } else {
                throw e;
            }
        }
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationMICustomConfig"})
    @CitrusTest
    public void getEndpointsDevAfterDeploy_TestCreateIntegrationMICustomConfig() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        GraphQL.validateEndpointDeployment(this, choreoProjectsTestClient, accessToken, argMap);
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = { "getEndpointsDevAfterDeploy_TestCreateIntegrationMICustomConfig" })
    @CitrusTest
    public void invokeAPIDev_TestCreateIntegrationMICustomConfig() throws Exception {

        Endpoint endpoint = endpoints.get(0);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, testComponent);
        final String devApiKey = testComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        String res = "{\"ServerName\":\"Choreo Micro Integrator 1.0.0\",\"GreetingMsg\":\"Greeting from WSO2 Micro " +
                "Integrator\"}";
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI, res);
    }

    @Test(dependsOnMethods = { "invokeAPIDev_TestCreateIntegrationMICustomConfig" })
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationMICustomConfig() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
            .componentType(MI_API_SERVICE).releaseId(componentDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

}
