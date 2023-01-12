package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class TestBYOCEUDp extends TestNGCitrusSpringSupport {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private static ChoreoComponent choreoComponent;
    private String projectId;
    private String accessToken;
    private String devInvokeURL;
    private String prodInvokeURL;
    String apiKey;

    @BeforeClass
    public void setup_TestBYOCEUDataPlane() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void creteProject_TestBYOCEUDataPlane() throws IOException {
        ChoreoProject project = GraphQL.createProject(Constant.region.EU, accessToken);
        projectId = project.getId();
        Assert.assertEquals(project.getRegion(), Constant.region.EU.name());
    }

    @Test(dependsOnMethods = {"creteProject_TestBYOCEUDataPlane"})
    @CitrusTest
    public void createByocComponent_TestBYOCEUDataPlane() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).projectId(projectId).dockerfilePath(DOCKER_FILE_PATH).build();
        choreoComponent = GraphQL.createBYOCComponent(dto, accessToken);
        Assert.assertEquals(choreoComponent.getName(), componentName);

    }

    @Test(dependsOnMethods = {"createByocComponent_TestBYOCEUDataPlane"})
    @CitrusTest
    public void componentRetrieval_TestBYOCEUDataPlane() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandle(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestBYOCEUDataPlane"})
    @CitrusTest
    public void initialPRGeneration_TestBYOCEUDataPlane() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(choreoComponent.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }

    @Test(dependsOnMethods = {"initialPRGeneration_TestBYOCEUDataPlane"})
    @CitrusTest
    public void deploy_TestBYOCEUDataPlane() throws Exception {
        Status status = GraphQL.deployComponent(choreoComponent, accessToken);
        Assert.assertTrue(status.isSuccess());
    }

    @Test(dependsOnMethods = {"deploy_TestBYOCEUDataPlane"})
    @CitrusTest
    public void deploymentStatusByVersion_TestBYOCEUDataPlane() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersion_TestBYOCEUDataPlane"})
    @CitrusTest
    public void componentDevDeploymentStatus_TestBYOCEUDataPlane() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, "dev", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_TestBYOCEUDataPlane"})
    @CitrusTest
    public void addPromoteConfiguration_TestBYOCEUDataPlane() throws Exception {
        Orgs.addConfiguration(choreoComponent, "prod", accessToken);
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_TestBYOCEUDataPlane"})
    @CitrusTest
    public void promote_TestBYOCEUDataPlane() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_TestBYOCEUDataPlane"})
    @CitrusTest
    public void componentProdDeploymentStatus_TestBYOCEUDataPlane() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_TestBYOCEUDataPlane"})
    @CitrusTest
    public void invokeAPIInDev_TestBYOCEUDataPlane() throws Exception {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        TestHelper.Movie[] movies = TestHelper.getMovies(devInvokeURL, apiKey);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"invokeAPIInDev_TestBYOCEUDataPlane"})
    @CitrusTest
    public void invokeAPIProd_TestBYOCEUDataPlane() {
        TestHelper.Movie[] movies = TestHelper.getMovies(prodInvokeURL, apiKey);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestBYOCEUDataPlane"}, alwaysRun = true)
    @CitrusTest
    public void deleteComponent_TestBYOC() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }
}
