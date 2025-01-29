/*
 * Copyright (c) 2023, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
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
import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCreateMiMultiRestEndpointServiceFromSubPath extends TestNGCitrusSpringSupport {

    public static final String COMPONENT_TYPE = Constant.AppType.MI_API_SERVICE.value;
    public static final String API_INVOCATION_REQUEST_URI = "/integration";
    public static final String REST_API_EXPECTED_RESPONSE = "{\"Hello\":\"Integration\"}";
    public static final String REST_API_CONTEXT = "/HelloWorld";
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
    private static ChoreoProject testProject;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ComponentDeploymentStatusDTO componentDeploymentStatusDTO, promotionStatusDTO;

    @BeforeClass
    public void setup_TestCreateMiMultiRestEndpointServiceFromSubPath()
            throws Exception {
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
        testProject = project;
    }

    @Test(dependsOnMethods = {"createProject_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void createComponent_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {

        // Creating component
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        final String repoName = "ipaas-multi-mi-project";
        final String repoBranch = "main";
        final String subPath = "mi-hello-multiple-api";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(COMPONENT_TYPE).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath(subPath).
                repositoryBranch(repoBranch).build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void componentRetrieval_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void generateEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
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
        GraphQL.generateEndpoints(this,choreoProjectsTestClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"generateEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void getEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 2);
    }

    @Test(dependsOnMethods = {"getEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void updateEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        final Endpoint endpoint = IntegrationComponentTestHelper.getEndpointForContext(endpoints, REST_API_CONTEXT);
        argMap.put("endpointId", endpoint.getId());
        argMap.put("displayName", endpoint.getDisplayName());
        argMap.put("apiContext", endpoint.getApiContext());
        argMap.put("apiDefinitionPath", endpoint.getApiDefinitionPath());
        argMap.put("visibility", Constant.EndpointVisibility.PUBLIC.value);
        Endpoint updatedEndpoint = GraphQL.updateEndpoint(this, choreoProjectsTestClient, accessToken, argMap);
        endpoints.set(0, updatedEndpoint);
    }

    @Test(dependsOnMethods = {"updateEndpointsDev_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void componentDeployment_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        // Deploy component
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
        testComponent);
        componentDeploymentStatusDTO = ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken, testComponent,
                environments);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void getEndpointsDevAfterDeploy_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        GraphQL.validateEndpointDeployment(this, choreoProjectsTestClient, accessToken, argMap);
        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 2);
    }

    @Test(dependsOnMethods = {"getEndpointsDevAfterDeploy_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void invokeAPIDev_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {

        final Endpoint endpoint = IntegrationComponentTestHelper.getEndpointForContext(endpoints, REST_API_CONTEXT);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, testComponent);
        final String devApiKey = testComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void promoteEndpointsProd_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("sourceReleaseId", testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("targetEnvironmentId", environments.get(1).getId());
        GraphQL.promoteEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"promoteEndpointsProd_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void componentPromotionToProd_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        // Retrieve the latest component.
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        testComponent = GraphQL.getComponentDetails(this, appServiceClient, projectId, componentHandler, accessToken);
        List<ComponentDeploymentStatusDTO> promotionStatuses = ComponentUtils.promoteComponent(this, citrusClients, 
            accessToken, testComponent, environments, 
            ComponentFlavour.MI, testProject);
        promotionStatusDTO = promotionStatuses.get(0);
    }

    @Test(dependsOnMethods = {"componentPromotionToProd_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void getEndpointsProdAfterDeploy_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", testComponent.getId());
        argMap.put("versionId", testComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", testComponent.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT));

        GraphQL.validateEndpointDeployment(this, choreoProjectsTestClient, accessToken, argMap);

        endpoints = GraphQL.getEndpoints(this, choreoProjectsTestClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 2);
    }

    @Test(dependsOnMethods = {"getEndpointsProdAfterDeploy_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void invokeAPIProd_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {

        final Endpoint endpoint = IntegrationComponentTestHelper.getEndpointForContext(endpoints, REST_API_CONTEXT);
        final String prodApiKey = testComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(1).getName()).replace("\"", "");
        final String invokeUrlProd = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void undeployComponentDev_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
            .componentType(COMPONENT_TYPE).releaseId(componentDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestCreateMiMultiRestEndpointServiceFromSubPath"})
    @CitrusTest
    public void undeployComponentProd_TestCreateMiMultiRestEndpointServiceFromSubPath() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
            .componentType(COMPONENT_TYPE).releaseId(promotionStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
