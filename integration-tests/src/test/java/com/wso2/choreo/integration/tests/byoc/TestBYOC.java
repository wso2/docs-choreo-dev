package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.ReleaseIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.byoc.ByocComponenet;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import com.wso2.choreo.integration.models.testconfigs.TestConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class TestBYOC extends TestNGCitrusSpringSupport {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private static ChoreoComponent choreoComponent;
    TestConfigs testConfigs;
    private String orgHandle;
    private String projectId;
    private String repoName;
    private String accessToken;
    private ChoreoOrganization org;
    private ByocComponenet component;
    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup() throws IOException, ProjectCreationException, InterruptedException, TokenRetrievalException {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
             org = TestContext.getTestOrg();
          ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();


    }


    @Test()
    @CitrusTest
    public void testCreateByocComponentBYOC() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        component = GraphQL.createBYOCComponent(componentName, projectId, repoName, DOCKER_FILE_PATH, accessToken);

    }

    @Test(dependsOnMethods = {"testCreateByocComponentBYOC"})
    @CitrusTest
    public void testComponentRetrievalBYOC() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, component.getHandle(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"testComponentRetrievalBYOC"})
    @CitrusTest
    public void testInitialPRGenerationBYOC() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(component.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }

    @Test(dependsOnMethods = {"testInitialPRGenerationBYOC"})
    @CitrusTest
    public void testDeployBYOC() throws Exception {
        Status status = GraphQL.deployComponent(choreoComponent, accessToken);
        Assert.assertTrue(status.isSuccess());
    }

    @Test(dependsOnMethods = {"testDeployBYOC"})
    @CitrusTest
    public void testDeploymentStatusByVersionBYOC() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"testDeploymentStatusByVersionBYOC"})
    @CitrusTest
    public void testComponentDevDeploymentStatusBYOC() throws Exception {
        GraphQL.componentDeployment(choreoComponent, "dev", "update code", accessToken);
    }

    @Test(dependsOnMethods = {"testComponentDevDeploymentStatusBYOC"})
    @CitrusTest
    public void testComponentPromotionToProd() throws ComponentDeploymentFailureException, NoLatestApiVersionFoundException, ReleaseIdNotFoundException, NoLatestAppEnvIdFoundException, IOException, ComponentDeploymentStatusCheckException, InterruptedException, ComponentDeploymentException, ComponentDeploymentTimeoutException {
        choreoComponent.promote(accessToken, Constant.DEV_ENVIRONMENT, Constant.PROD_ENVIRONMENT);
    }

    @Test(dependsOnMethods = {"testComponentPromotionToProd"})
    @CitrusTest
    public void testAPIInvocationInDevBYOC() throws InvokeInformationNotFoundException, NoLatestApiVersionFoundException, IOException, ApiKeyNotFoundException, APIKeyGenerationCheckException {
        testConfigs = ComponentUtils.invokeEndpoint(choreoComponent, Constant.displayType.restAPI.name(), accessToken);
        TestHelper.Movie[] movies = TestHelper.getMovies(testConfigs, Constant.Environment.Development);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"testAPIInvocationInDevBYOC"})
    @CitrusTest
    public void testAPIInvocationInPRodBYOC()  {
        TestHelper.Movie[] movies = TestHelper.getMovies(testConfigs, Constant.Environment.Production);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"testAPIInvocationInPRodBYOC"}, alwaysRun = true)
    @CitrusTest
    public void testDeleteComponent() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }
}
