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
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
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

public class TestCreateIntegrationRestComponentFromRoot extends TestNGCitrusSpringSupport {

    public static final String COMPONENT_TYPE = Constant.AppType.MI_API_SERVICE.value;
    public static final String API_INVOCATION_REQUEST_URI = "/";
    public static final String REST_API_EXPECTED_RESPONSE = "{\"message\": \"Hello Integration\"}";
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
    private HttpClient choreoProjectsTestClient;

    private ComponentDeploymentStatusDTO componentDeploymentStatusDTO, promotionStatusDTO;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestCreateIntegrationRestComponentFromRoot()
            throws Exception {
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"createProject_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void createComponent_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        // Creating component
        String componentName = "MIFromRoot".concat(String.valueOf(new Date().getTime()));
        final String repoName = "synaps-api-project-sample";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(COMPONENT_TYPE).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
                repositoryBranch(repoBranch).build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);

    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void generateEndpointsDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("commitHash", testComponent.getLatestCommitHash(testComponent.getCommitHistory(accessToken)));
        GraphQL.generateEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"generateEndpointsDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void getEndpointsDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = {"getEndpointsDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void updateEndpointsDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {
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

    @Test(dependsOnMethods = {"updateEndpointsDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        // Deploy component
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
        testComponent);
        componentDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, testComponent, 
            environments, ComponentFlavour.MI);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void getEndpointsDevAfterDeploy_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }


    @Test(dependsOnMethods = {"getEndpointsDevAfterDeploy_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void invokeAPIDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        Endpoint endpoint = endpoints.get(0);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, testComponent);
        final String devApiKey = testComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                        environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void promoteEndpointsProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("sourceReleaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("targetEnvironmentId", environments.get(1).getId());
        GraphQL.promoteEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"promoteEndpointsProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentPromotionToProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        testComponent = GraphQL.getComponentDetails(this, appServiceClient, projectId, componentHandler, accessToken);
        List<ComponentDeploymentStatusDTO> promotionStatuses = ComponentUtils.promoteComponent(this, citrusClients, 
            accessToken, testComponent, environments, ComponentFlavour.MI);
        promotionStatusDTO = promotionStatuses.get(0);
    }

    @Test(dependsOnMethods = {"componentPromotionToProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void getEndpointsProdAfterDeploy_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT));

        GraphQL.validateEndpointDeployment(this, choreoProjectsTestClient, accessToken, argMap);

        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = {"getEndpointsProdAfterDeploy_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void invokeAPIProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        final Endpoint endpoint = endpoints.get(0);
        final String prodApiKey = testComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                        environments.get(1).getName()).replace("\"", "");
        final String invokeUrlProd = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
            .componentType(COMPONENT_TYPE).releaseId(componentDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void undeployComponentProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
            .componentType(COMPONENT_TYPE).releaseId(promotionStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
