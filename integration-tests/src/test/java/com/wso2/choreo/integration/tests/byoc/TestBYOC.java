package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;

import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class TestBYOC extends TestNGCitrusSpringSupport {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private static ChoreoComponent choreoComponent;
    private String projectId;
    private String accessToken;
    private String devInvokeURL;
    private String prodInvokeURL;
    String apiKey;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestBYOC() throws Exception {
       accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }


    @Test()
    @CitrusTest
    public void createByocComponent_TestBYOC() throws Exception {
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
    @Test(dependsOnMethods = {"createByocComponent_TestBYOC"})
    @CitrusTest
    public void deploy_TestBYOC() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent);
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_TestBYOC"})
    @CitrusTest
    public void addPromoteConfiguration_TestBYOC() throws Exception {
        Orgs.addConfiguration(choreoComponent, "prod", accessToken);
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_TestBYOC"})
    @CitrusTest
    public void promote_TestBYOC() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_TestBYOC"})
    @CitrusTest
    public void componentProdDeploymentStatus_TestBYOC() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_TestBYOC"})
    @CitrusTest
    public void invokeAPIInDev_TestBYOC() throws Exception {

        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        TestHelper.Movie[] movies = TestHelper.getMovies(devInvokeURL, apiKey);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"invokeAPIInDev_TestBYOC"})
    @CitrusTest
    public void invokeAPIProd_TestBYOC() {
        TestHelper.Movie[] movies = TestHelper.getMovies(prodInvokeURL, apiKey);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestBYOC"}, alwaysRun = true)
    @CitrusTest
    public void deleteComponent_TestBYOC() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }
}
