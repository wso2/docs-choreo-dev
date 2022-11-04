package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.wso2.choreo.integration.apis.GitHub;
import com.wso2.choreo.integration.apis.GraphQL;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ControlPlaneAPIs;
import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.createcomponentresponse.CreateComponent;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import com.wso2.choreo.integration.models.testconfigs.TestConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class TestClientJwTValidation extends TestNGCitrusSpringSupport {


    private static ChoreoComponent choreoComponent;
    private String orgHandle;
    private String projectId;
    private String repoName;
    private String accessToken;
    private CreateComponent response;
    private ChoreoOrganization org;


    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup() throws TokenRetrievalException, IOException, ProjectCreationException, InterruptedException, UnexpectedResponseException, ComponentCreationTimeoutException, ComponentCreationStatusCheckException {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        org = TestContext.getTestOrg();
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();


    }


    @Test
    @CitrusTest
    public void testCreateUserManagedComponentForJwt() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        response = GraphQL.createUserManagedComponent(repoName, componentName, projectId, accessToken);
    }

    @Test(dependsOnMethods = {"testCreateUserManagedComponentForJwt"})
    @CitrusTest
    public void testCreatedComponentStatusJwt() throws ComponentCreationTimeoutException, ComponentCreationStatusCheckException {
        ControlPlaneAPIs.waitForComponentCreationSuccess(accessToken, orgHandle, projectId, response.getId());
    }


    @Test(dependsOnMethods = {"testCreatedComponentStatusJwt"})
    @CitrusTest
    public void testInitialPRGenerationJwt() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 1);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"testInitialPRGenerationJwt"})
    @CitrusTest
    public void testPRMergeJwt() throws IOException, UnexpectedResponseException {
        GitHub.mergePR(repoName, "1");
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }


    @Test(dependsOnMethods = {"testPRMergeJwt"})
    @CitrusTest
    public void testMergeNewCodeJwt() throws IOException, UnexpectedResponseException {
        String encodedContent = FileUtil.readFile("src/test/resources/templates/encodedbal/service.bal");
        GitHub.mergeNewCode(repoName, "service.bal", "update code", encodedContent);
    }

    @Test(dependsOnMethods = {"testMergeNewCodeJwt"})
    @CitrusTest
    public void testComponentRetrievalJwt() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"testComponentRetrievalJwt"})
    @CitrusTest
    public void testAddDeploymentConfigurationJwt() throws Exception {
        Orgs.addConfiguration(choreoTestClient, this, choreoComponent, "dev");
    }


    @Test(dependsOnMethods = {"testAddDeploymentConfigurationJwt"})
    @CitrusTest
    public void testDeployJwt() throws Exception {
        GraphQL.deployComponent(choreoTestClient, this, choreoComponent);
    }

    @Test(dependsOnMethods = {"testDeployJwt"})
    @CitrusTest
    public void testDeploymentStatusByVersionJwt() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoTestClient, this, choreoComponent);
    }


    @Test(dependsOnMethods = {"testDeploymentStatusByVersionJwt"})
    @CitrusTest
    public void testComponentDevDeploymentStatusJwt() throws Exception {
        GraphQL.componentDeployment(choreoComponent, "dev", accessToken);
    }


    @Test(dependsOnMethods = {"testComponentDevDeploymentStatusJwt"})
    @CitrusTest
    public void testAPIInvocationJwt() throws NoLatestApiVersionFoundException, IOException, InvokeInformationNotFoundException, InterruptedException, ApiKeyNotFoundException, APIKeyGenerationCheckException {
        TestConfigs testConfigs = ComponentUtils.invokeEndpoint(choreoComponent, Constant.displayType.restAPI.name(), accessToken);
        String apiInvocationRequestURI = "/getJwt";

        http().
                client(testConfigs.getConfig(Constant.Environment.Development.name()).getInvokeUrl()).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT, "text/plain").
                header("API-Key", testConfigs.getConfig(Constant.Environment.Development.name()).getApiKey());

        http().client(testConfigs.getConfig(Constant.Environment.Development.name()).getInvokeUrl()).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
                validate(JsonMessageValidationContext.Builder.json());
    }


    @Test(dependsOnMethods = {"testAPIInvocationJwt"}, alwaysRun = true)
    @CitrusTest
    public void testDeleteComponent() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        ChoreoComponent[] components = GraphQL.getProjectComponents(projectId,accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals(components.length, 0);
    }

    @Test(dependsOnMethods = {"testDeleteComponent"}, alwaysRun = true)
    @CitrusTest
    public void testDeleteRepo() {
        Response response = GitHub.deleteGitHubRepo(repoName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT.value());
    }

}

