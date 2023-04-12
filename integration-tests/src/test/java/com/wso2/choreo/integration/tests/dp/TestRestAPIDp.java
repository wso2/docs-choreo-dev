package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class TestRestAPIDp extends TestBase {

    private String accessToken;
    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoTestClient;
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
    public void setup_TestRestAPIIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }


    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedRestAPI_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = GraphQL.createProject(dp.getRegion(), accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/rest-api").
                projectId(project.getId()).
                displayType(Constant.displayType.restAPI.name()).
                build();
        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedRestAPI_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        String devInvokeURL = statusDTO.getInvokeUrl();
        dp.setDevInvokeUrl(devInvokeURL);
    }


    @Test(dependsOnMethods = {"componentDeploy_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void promote_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        String prodInvokeURL = statusDTO.getInvokeUrl();
        String apiId = statusDTO.getApiId();
        dp.setApiId(apiId);
        dp.setProdInvokeUrl(prodInvokeURL);
    }


    @Test(dependsOnMethods = {"promote_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIInDev_TestBYOCEUDataPlane(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken, dp.getApiId());
        ComponentUtils.invokeApiGET(this, keyData.getApikey(), dp.getDevInvokeUrl(), "/isOdd?number=34", "false");
        dp.setKeyData(keyData);
    }

    @Test(dependsOnMethods = {"promote_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIInProd_TestBYOCEUDataPlane(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiGET(this, dp.getKeyData().getApikey(), dp.getProdInvokeUrl(), "/isOdd?number=34", "false");
    }

}
