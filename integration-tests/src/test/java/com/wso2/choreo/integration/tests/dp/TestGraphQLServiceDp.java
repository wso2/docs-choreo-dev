package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
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
import org.apache.commons.lang3.tuple.Pair;
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
    public void setup_GraphQLServiceDpIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
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
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setDeploymentStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"componentDeploy_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void promote_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                dp.getDeploymentStatusDTO(), dp.getEnvironments());
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                    statusDTO, dp.getEnvironments());
            ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/",
                    GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
        }
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                dp.getDeploymentStatusDTO(), dp.getEnvironments());
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
            Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                    statusDTO, dp.getEnvironments());
            ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/",
                    GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
        }
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        dp.updateEnvironments(ComponentUtils.getEnvironments(this, citrusClients, accessToken, dp.getChoreoComponent()));
    }


    @Test(dataProvider = "dps", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceDpIT"})
    @CitrusTest
    public void testLiveLogs_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
            ComponentUtils.verifyLogs(this, citrusClients, accessToken, dp.getChoreoComponent(), env, dp.getRegion());
        }
    }

    @Test(dataProvider = "dps", dependsOnMethods = {"testLiveLogs_GraphQLServiceDpIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceDpIT(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
            ComponentUtils.verifyGroupLogs(this, citrusClients, accessToken, dp.getChoreoComponent(), env, dp.getRegion());
        }
    }
}
