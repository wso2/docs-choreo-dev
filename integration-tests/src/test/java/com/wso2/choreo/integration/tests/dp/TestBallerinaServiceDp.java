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
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestBallerinaServiceDp extends TestBase {

    private String accessToken;
    private String orgHandle;
    private String API_INVOCATION_REQUEST_URI;
    private String REST_API_EXPECTED_RESPONSE;

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
        API_INVOCATION_REQUEST_URI = "/books";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/ballerinaService/ballerinaServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-service-app1").
                branch("main").subPath("").build();

        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);
        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                choreoComponent);

        dp.setChoreoComponent(choreoComponent);
        dp.setEnvironments(environments);

        String componentId = choreoComponent.getId();

        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(componentId);
    }

    @Test(dependsOnMethods = {"createComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void deployComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentUtils.deployComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments(),
                ComponentFlavour.STANDARD);
    }

   @Test(dependsOnMethods = {"deployComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Endpoint endpoint = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                dp.getChoreoComponent(), Constant.DEV_ENVIRONMENT).get(0);
        String devApiKey = dp.getChoreoComponent().getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                dp.getEnvironments().get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments(),
                ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"promoteComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        Endpoint endpoint = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                dp.getChoreoComponent(), Constant.PROD_ENVIRONMENT).get(0);
        String prodApiKey = dp.getChoreoComponent().getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                dp.getEnvironments().get(1).getName()).replace("\"", "");
        String invokeUrlProd = endpoint.getPublicUrl();
        ComponentUtils.invokeApiGET(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        String devReleaseId = ComponentUtils.getComponentDeploymentStatus(this, citrusClients, accessToken,
                dp.getChoreoComponent(), Constant.DEV_ENVIRONMENT).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(dp.getChoreoComponent().getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        String prodReleaseId = ComponentUtils.getComponentDeploymentStatus(this, citrusClients, accessToken,
                dp.getChoreoComponent(), Constant.PROD_ENVIRONMENT).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(dp.getChoreoComponent().getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(prodReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
