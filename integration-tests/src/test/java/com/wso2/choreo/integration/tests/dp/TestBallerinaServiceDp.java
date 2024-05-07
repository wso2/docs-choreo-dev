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
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
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
        Assert.assertNotNull(componentId);
    }

    @Test(dependsOnMethods = {"createComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void deployComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments(),
                ComponentFlavour.STANDARD);
        dp.setDeploymentStatusDTO(statusDTO);
    }

   @Test(dependsOnMethods = {"deployComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
       Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
               dp.getDeploymentStatusDTO(), dp.getEnvironments());
        ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteComponent_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments(),
                ComponentFlavour.STANDARD, dp.getChoreoProject());
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promoteComponent_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                    statusDTO, dp.getEnvironments());
            ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                    REST_API_EXPECTED_RESPONSE);
        }
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentDev_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(dp.getChoreoComponent().getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(dp.getDeploymentStatusDTO().getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestBallerinaServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void undeployComponentProd_TestBallerinaServiceDp(DataProviderWrapper dp) throws Exception {
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(dp.getChoreoComponent().getId()).orgHandler(orgHandle)
                    .componentType("ballerinaService").releaseId(statusDTO.getReleaseId()).build();
            GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
        }
    }
}
