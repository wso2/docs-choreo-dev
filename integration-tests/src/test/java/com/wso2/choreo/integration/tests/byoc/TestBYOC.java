package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;

import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import com.wso2.choreo.integration.models.testconfigs.TestConfigs;
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
    private String projectId;
    private String accessToken;
    private ChoreoOrganization org;
    private String devInvokeURL;
    private String prodInvokeURL;
    String apiKey;

    @BeforeClass
    public void setup() throws IOException, ProjectCreationException, InterruptedException, TokenRetrievalException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        org = TestContext.getTestOrg();
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();
    }


    @Test()
    @CitrusTest
    public void testCreateByocComponentBYOC() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).projectId(projectId).dockerfilePath(DOCKER_FILE_PATH).build();
        choreoComponent = GraphQL.createBYOCComponent(dto, accessToken);
        Assert.assertEquals(choreoComponent.getName(), componentName);

    }

    @Test(dependsOnMethods = {"testCreateByocComponentBYOC"})
    @CitrusTest
    public void testComponentRetrievalBYOC() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandle(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"testComponentRetrievalBYOC"})
    @CitrusTest
    public void testInitialPRGenerationBYOC() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(choreoComponent.getId(), accessToken, 0);
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
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, "dev", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"testComponentDevDeploymentStatusBYOC"})
    @CitrusTest
    public void testAddPromoteConfigurationBYOC() throws Exception {
        Orgs.addConfiguration(choreoComponent, "prod", accessToken);
    }

    @Test(dependsOnMethods = {"testAddPromoteConfigurationBYOC"})
    @CitrusTest
    public void testPromoteBYOC() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"testPromoteBYOC"})
    @CitrusTest
    public void testComponentProdDeploymentStatusBYOC() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"testComponentProdDeploymentStatusBYOC"})
    @CitrusTest
    public void testAPIInvocationInDevBYOC() throws Exception {
        apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
        TestHelper.Movie[] movies = TestHelper.getMovies(devInvokeURL, apiKey);
        Assert.assertEquals(movies.length, 5);
        Assert.assertEquals(movies[0].id, 1);
        Assert.assertEquals(movies[0].ratings, 9.2);
        Assert.assertEquals(movies[0].name, "The Shawshank Redemption");
    }

    @Test(dependsOnMethods = {"testAPIInvocationInDevBYOC"})
    @CitrusTest
    public void testAPIInvocationInPRodBYOC() {
        TestHelper.Movie[] movies = TestHelper.getMovies(prodInvokeURL, apiKey);
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
