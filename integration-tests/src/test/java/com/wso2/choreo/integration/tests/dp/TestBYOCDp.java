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
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.tests.byoc.TestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestBYOCDp extends TestBase {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private String accessToken;
    private final List<DataProviderWrapper> dps = new ArrayList<>();
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestBYOCEUDp() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @DataProvider(name = "reg")
    public Object[][] regionData() {
        return DataProviderWrapper.convertToDataProvider(
                Arrays.asList(Configuration.getConfig(ConfigDefinition.REGIONS).split(",")));
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());

        Repository repo = Repository.builder().
                repoUrl("https://github.com/choreo-test-apps/byor-greetings-app2").
                oasFilePath("byoc-test/oas.yaml").
                dockerContext("byoc-test").
                dockerfilePath(DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);

        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void deployComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.BYOC);
        String devInvokeURL = statusDTO.getInvokeUrl();
        String apiId = statusDTO.getApiId();
        dp.setApiId(apiId);
        dp.setDevInvokeUrl(devInvokeURL);
    }

    @Test(dependsOnMethods = {"deployComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.BYOC);
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promoteComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIDev_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                dp.getApiId(), ComponentUtils.getKeyType(dp.getEnvironments().get(0)));
        String expectedResponse = TestHelper.getExpectedResponse();
        ComponentUtils.invokeApiGET(this, keyData.getApikey(), dp.getDevInvokeUrl(), "/movies",
                expectedResponse);
        dp.setDevKeyData(keyData);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIProd_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                dp.getApiId(), ComponentUtils.getKeyType(dp.getEnvironments().get(1)));
        String expectedResponse = TestHelper.getExpectedResponse();
        for (ComponentDeploymentStatusDTO statusDTO :dp.getPromoteStatusDTO()) {
            ComponentUtils.invokeApiGET(this, keyData.getApikey(), statusDTO.getInvokeUrl(), "/movies",
                    expectedResponse);
        }
        dp.setProdKeyData(keyData);
    }

}
