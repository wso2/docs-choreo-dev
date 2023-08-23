package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.tests.graphqlservice.GqlServiceTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestGraphQLServiceDp extends TestBase {

    private String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_GraphQLServiceEUdpIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().
                repoUrl("https://github.com/choreo-test-apps/gql-service").
                branch("main").
                subPath("").build();

        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);

        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        String devInvokeURL = statusDTO.getInvokeUrl();
        String apiId = statusDTO.getApiId();
        dp.setApiId(apiId);
        dp.setDevInvokeUrl(devInvokeURL);
    }

    @Test(dependsOnMethods = {"componentDeploy_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void promote_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                dp.getApiId(), ComponentUtils.getKeyType(dp.getEnvironments().get(0)));
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), dp.getDevInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
        dp.setDevKeyData(keyData);
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                dp.getApiId(), ComponentUtils.getKeyType(dp.getEnvironments().get(1)));
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            ComponentUtils.invokeApiPOST(this, keyData.getApikey(), statusDTO.getInvokeUrl(), "/",
                    GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
        }
        dp.setProdKeyData(keyData);
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiPOST(this, dp.getDevKeyData().getApikey(), dp.getDevInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            ComponentUtils.invokeApiPOST(this, dp.getProdKeyData().getApikey(), statusDTO.getInvokeUrl(), "/",
                    GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
        }
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        dp.updateEnvironments(ComponentUtils.getEnvironments(this, citrusClients, accessToken, dp.getChoreoComponent()));
    }


    @Test(dataProvider = "dps", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testLiveLogs_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
            ComponentUtils.verifyLogs(this, citrusClients, accessToken, dp.getChoreoComponent(), env, dp.getRegion());
        }
    }

    @Test(dataProvider = "dps", dependsOnMethods = {"testLiveLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
            ComponentUtils.verifyGroupLogs(this, citrusClients, accessToken, dp.getChoreoComponent(), env, dp.getRegion());
        }
    }
}
