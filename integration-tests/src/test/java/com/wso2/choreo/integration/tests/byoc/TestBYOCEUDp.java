package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.tests.dp.DataProviderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class TestBYOCEUDp extends TestNGCitrusSpringSupport {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private static ChoreoComponent choreoComponent;
    private String projectId;
    private String accessToken;
    private String devInvokeURL;
    private String prodInvokeURL;
    private String apiId;
    private KeyData keyData;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestBYOCEUDataPlane() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

    }

    @DataProvider(name = "reg")
    public Object[][] regionData() {
        return DataProviderWrapper.convertToDataProvider(Arrays.asList(Configuration.getConfig(ConfigDefinition.REGIONS).split(",")));
    }

    @Test(dataProvider = "reg")
    @CitrusTest
    public void creteProject_TestBYOCEUDataPlane(String region) throws IOException {
        ChoreoProject project = GraphQL.createProject(region, accessToken);
        projectId = project.getId();
        Assert.assertEquals(project.getRegion(), region);
    }

    @Test(dependsOnMethods = {"creteProject_TestBYOCEUDataPlane"})
    @CitrusTest
    public void createByocComponent_TestBYOCEUDataPlane() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).projectId(projectId)
                .srcGitRepoUrl("https://github.com/choreo-test-apps/byor-greetings-app2")
                .oasFilePath("byoc-test/oas.yaml")
                .dockerContext("byoc-test")
                .dockerfilePath(DOCKER_FILE_PATH).build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.BYOC);
        Assert.assertEquals(choreoComponent.getName(), componentName);

    }

    @Test(dependsOnMethods = {"createByocComponent_TestBYOCEUDataPlane"})
    @CitrusTest
    public void deploy_TestBYOCEUDataPlane() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, ComponentFlavour.BYOC);
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_TestBYOCEUDataPlane"})
    @CitrusTest
    public void promote_TestBYOCEUDataPlane() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, choreoComponent, ComponentFlavour.BYOC);
        prodInvokeURL = statusDTO.getInvokeUrl();
        apiId = statusDTO.getApiId();
    }


    @Test(dependsOnMethods = {"promote_TestBYOCEUDataPlane"})
    @CitrusTest
    public void invokeAPIInDev_TestBYOCEUDataPlane() throws Exception {
        keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken, apiId);
        String expectedResponse = TestHelper.getExpectedResponse();
        ComponentUtils.invokeApiGET(this, keyData.getApikey(), devInvokeURL, "/movies", expectedResponse);
    }

    @Test(dependsOnMethods = {"invokeAPIInDev_TestBYOCEUDataPlane"})
    @CitrusTest
    public void invokeAPIProd_TestBYOCEUDataPlane() throws Exception {
        String expectedResponse = TestHelper.getExpectedResponse();
        ComponentUtils.invokeApiGET(this, keyData.getApikey(), prodInvokeURL, "/movies", expectedResponse);
    }

}
