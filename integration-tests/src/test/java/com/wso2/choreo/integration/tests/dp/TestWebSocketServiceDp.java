package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestWebSocketServiceDp extends TestBase {

    private String accessToken;
    private String apiId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_TestWebSocketServiceDp() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedComponentFor_TestWebSocketServiceDp(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        Repository repo = Repository.builder().
                repoUrl("https://github.com/wso2/choreo-samples").
                branch("main").
                subPath("websocket-chat-service-ballerina").build();

        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentWithPublicURLRequest(componentName, project, repo);

        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent.getId());
        
        ComponentUtils.waitForComponentInitialBuildComplete(this, citrusClients, accessToken, choreoComponent);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_TestWebSocketServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_TestWebSocketServiceDp(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO =ComponentUtils.deployAndValidateBuiltComponentWithFlavour(this, citrusClients, accessToken, dp.getChoreoComponent(),
        dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setDeploymentStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"componentDeploy_TestWebSocketServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void promote_TestWebSocketServiceDp(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD, dp.getChoreoProject());
        dp.setPromoteStatusDTO(statusDTO);
        int promotionStatusListSize =  statusDTO.size();
        ComponentDeploymentStatusDTO lastpromotionStatus = statusDTO.get(promotionStatusListSize-1);

        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken, dp.getChoreoComponent(),
                lastpromotionStatus);

        Endpoint endpoint = endpoints.get(0);
        apiId = endpoint.getApimId();
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_TestWebSocketServiceDp"}, dataProvider = "dps")
    @CitrusTest
    public void createConnectionInDev_TestWebSocketServiceDp(DataProviderWrapper dp) throws Exception {
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken, dp.getChoreoComponent(),
                dp.getDeploymentStatusDTO(), dp.getEnvironments());
        ComponentUtils.invokeWSApi(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/");
    }

    public void publishWebSocketAPItoDevPortal_TestWebSocketServiceDp(DataProviderWrapper dp) throws Exception {
        Response resp = ApiManager.changeLifeCycle(apiId, "Publish", accessToken);
        if (resp.getStatusCode() != HttpStatus.OK.value()) {
            throw new ValidationException("Failed to publish the API");
        }
    }
}
