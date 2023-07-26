package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBallerinaServiceDp extends TestBase {

    private String accessToken;
    private String orgHandle;
    private ChoreoComponent choreoComponent;
    private String componentId;
    private List<Endpoint> endpoints;
    private List<Environment> environments;
    public static final String API_INVOCATION_REQUEST_URI = "/books";
    public static final String REST_API_EXPECTED_RESPONSE = "[]";
    private HttpClient appServiceClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;


    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @DataProvider(name = "env-provider")
    public Object[][] envProvider() {
        return DataProviderWrapper.convertToDataProvider(Arrays.asList(Constant.Environment.values()));
    }

    @BeforeClass
    public void setup_TestBallerinaServiceDp() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = GraphQL.createProject(dp.getRegion(), accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-service-app1").
                branch("main").subPath("").build();

        GraphqlDTO dto = ComponentUtils.createServiceComponentRequest(componentName, project, repo);
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                choreoComponent);
        componentId = choreoComponent.getId();

        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(componentId);
    }

    @Test(dependsOnMethods = {"createComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void generateEndpointsDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", choreoComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("commitHash", choreoComponent.getLatestCommitHash(choreoComponent.getCommitHistory(accessToken)));
        GraphQL.generateEndpoints(this, appServiceClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"generateEndpointsDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void getEndpointsDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", choreoComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, appServiceClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = {"getEndpointsDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void updateEndpointsDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", choreoComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        final Endpoint endpoint = endpoints.get(0);
        argMap.put("endpointId", endpoint.getId());
        argMap.put("displayName", endpoint.getDisplayName());
        argMap.put("apiContext", endpoint.getApiContext());
        argMap.put("apiDefinitionPath", endpoint.getApiDefinitionPath());
        argMap.put("visibility", Constant.EndpointVisibility.PUBLIC.value);
        Endpoint updatedEndpoint = GraphQL.updateEndpoint(this, appServiceClient, accessToken, argMap);
        endpoints.set(0, updatedEndpoint);
    }

    @Test(dependsOnMethods = {"updateEndpointsDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploymentDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentUtils.deployComponent(this, citrusClients, accessToken, choreoComponent, environments,
                ComponentFlavour.STANDARD);
        SleepUtil.sleep(30);
    }

    @Test(dependsOnMethods = {"componentDeploymentDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void getEndpointsDevAfterDeploy_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", choreoComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        endpoints = GraphQL.getEndpoints(this, appServiceClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

   @Test(dependsOnMethods = {"getEndpointsDevAfterDeploy_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Endpoint endpoint = endpoints.get(0);
        String devApiKey = choreoComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteEndpointsProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("sourceReleaseId", choreoComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT));
        argMap.put("targetEnvironmentId", environments.get(1).getId());
        GraphQL.promoteEndpoints(this, appServiceClient, accessToken, argMap);
    }

    @Test(dependsOnMethods = {"promoteEndpointsProd_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteComponentProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent, environments,
                ComponentFlavour.STANDARD);
        SleepUtil.sleep(30);
    }

    @Test(dependsOnMethods = {"promoteComponentProd_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void getEndpointsProdAfterDeploy_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", componentId);
        argMap.put("versionId", choreoComponent.getLatestApiVersion().getId());
        argMap.put("releaseId", choreoComponent.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT));
        GraphQL.validateEndpointDeployment(this, appServiceClient, accessToken, argMap);
        endpoints = GraphQL.getEndpoints(this, appServiceClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);
    }

    @Test(dependsOnMethods = {"getEndpointsProdAfterDeploy_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Endpoint endpoint = endpoints.get(0);
        String prodApiKey = choreoComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(1).getName()).replace("\"", "");
        String invokeUrlProd = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        String devReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        String prodReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.PROD_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(prodReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
