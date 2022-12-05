package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
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
    private String projectId;
    private String repoName;
    private String accessToken;
    private ComponentCreationResponse response;

    private String devInvokeURL;
    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_TestClientJwTValidation() throws Exception {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();


    }


    @Test
    @CitrusTest
    public void createUserManagedComponentFor_TestClientJwTValidation() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").srcGitRepoUrl(GitHub.getGitHubRepoUrl(repoName)).projectId(projectId).displayType(Constant.displayType.restAPI.name()).build();
        response = GraphQL.createUserManagedComponent(dto, accessToken);
        Assert.assertNotNull(response.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_TestClientJwTValidation"})
    @CitrusTest
    public void createdComponentStatus_TestClientJwTValidation() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, response.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_TestClientJwTValidation"})
    @CitrusTest
    public void initialPRGeneration_TestClientJwTValidation() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 1);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"initialPRGeneration_TestClientJwTValidation"})
    @CitrusTest
    public void mergePR_TestClientJwTValidation() throws IOException, UnexpectedResponseException {
        GitHub.mergePR(repoName, "1");
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }


    @Test(dependsOnMethods = {"mergePR_TestClientJwTValidation"})
    @CitrusTest
    public void mergeNewCode_TestClientJwTValidation() throws IOException {
        String encodedContent = FileUtil.readFileEncodedContent("src/test/resources/templates/encodedbal/service.bal");
        GitHub.mergeNewCode(repoName, "service.bal", "update code", encodedContent);
    }

    @Test(dependsOnMethods = {"mergeNewCode_TestClientJwTValidation"})
    @CitrusTest
    public void componentRetrieval_TestClientJwTValidation() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestClientJwTValidation"})
    @CitrusTest
    public void addDeploymentConfiguration_TestClientJwTValidation() throws Exception {
        Orgs.addConfiguration(choreoComponent, "dev", accessToken);
    }


    @Test(dependsOnMethods = {"addDeploymentConfiguration_TestClientJwTValidation"})
    @CitrusTest
    public void deploy_TestClientJwTValidation() throws Exception {
        GraphQL.deployComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"deploy_TestClientJwTValidation"})
    @CitrusTest
    public void deploymentStatusByVersion_TestClientJwTValidation() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_TestClientJwTValidation"})
    @CitrusTest
    public void componentDevDeploymentStatus_TestClientJwTValidation() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentDevDeploymentStatus_TestClientJwTValidation"})
    @CitrusTest
    public void invokeAPI_TestClientJwTValidation() throws Exception {
        String apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
        String apiInvocationRequestURI = "/getJwt";

        http().
                client(devInvokeURL).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT, "text/plain").
                header("API-Key", apiKey);

        http().client(devInvokeURL).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
                validate(JsonMessageValidationContext.Builder.json());
    }


    @Test(dependsOnMethods = {"invokeAPI_TestClientJwTValidation"}, alwaysRun = true)
    @CitrusTest
    public void deleteComponent_TestClientJwTValidation() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        ChoreoComponent[] components = GraphQL.getProjectComponents(projectId, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals(components.length, 0);
    }

    @Test(dependsOnMethods = {"deleteComponent_TestClientJwTValidation"}, alwaysRun = true)
    @CitrusTest
    public void deleteRepo_TestClientJwTValidation() {
        Response response = GitHub.deleteGitHubRepo(repoName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT.value());
    }

}

