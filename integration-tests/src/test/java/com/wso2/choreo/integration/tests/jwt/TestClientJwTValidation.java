package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
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
import com.wso2.choreo.integration.common.exceptions.RequestExecutionException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.common.utils.GitUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
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

    private String orgId;
    private String orgHandle;
    private String projectId;
    private String orgUUID;
    private String repoName;
    private String accessToken;
    private static ChoreoComponent choreoComponent;
    private CreateComponent response;
    private ChoreoOrganization org;


    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup() throws TokenRetrievalException, IOException, ProjectCreationException, InterruptedException, RequestExecutionException, ComponentCreationTimeoutException, ComponentCreationStatusCheckException {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();


    }


    @Test
    @CitrusTest
    public void testCreateUserManagedComponentForJwt() throws IOException, RequestExecutionException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitUtil.initGitHubRepo(repoName, true, true, "nanoc");
        response = GraphQL.createBYORComponent(repoName, componentName, projectId, accessToken);
    }

    @Test(dependsOnMethods = {"testCreateUserManagedComponentForJwt"})
    @CitrusTest
    public void testCreatedComponentStatusJwt() throws ComponentCreationTimeoutException, ComponentCreationStatusCheckException {
        ControlPlaneAPIs.waitForComponentCreationSuccess(accessToken, orgHandle, projectId, response.getId());
    }


    @Test(dependsOnMethods = {"testCreatedComponentStatusJwt"})
    @CitrusTest
    public void testInitialPRGenerationJwt() throws IOException, RequestExecutionException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"testInitialPRGenerationJwt"})
    @CitrusTest
    public void testPRMergeJwt() throws IOException, RequestExecutionException {
        GitUtil.mergePR(repoName, "1");
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken);
        Assert.assertEquals(prs.length, 0);
    }


    @Test(dependsOnMethods = {"testPRMergeJwt"})
    @CitrusTest
    public void testMergeNewCodeJwt() throws IOException, RequestExecutionException {
        String encodedContent = FileUtil.readFile("src/test/resources/templates/encodedbal/service.bal");
        GitUtil.mergeNewCode(repoName, "service.bal", "update code", encodedContent);
    }

    @Test(dependsOnMethods = {"testMergeNewCodeJwt"})
    @CitrusTest
    public void testComponentRetrievalJwt() throws IOException, RequestExecutionException {
        choreoComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"testComponentRetrievalJwt"})
    @CitrusTest
    public void testAddDeploymentConfiguration() throws Exception {
        Orgs.addConfiguration(choreoTestClient, this, choreoComponent, "dev",null);
    }


    @Test(dependsOnMethods = {"testAddDeploymentConfiguration"})
    @CitrusTest
    public void testDeploy() throws Exception {
        GraphQL.deployComponent(choreoTestClient, this, choreoComponent);
    }

    @Test(dependsOnMethods = {"testDeploy"})
    @CitrusTest
    public void testDeploymentStatusByVersion() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoTestClient, this, choreoComponent);
    }


    @Test(dependsOnMethods = {"testDeploymentStatusByVersion"})
    @CitrusTest
    public void testComponentDevDeploymentStatus() throws Exception {
        GraphQL.componentDeployment(choreoTestClient, this, choreoComponent, "dev","update code");
    }


    @Test(dependsOnMethods = {"testComponentDevDeploymentStatus"})
    @CitrusTest
    public void testAPIInvocation() throws NoLatestApiVersionFoundException, IOException, InvokeInformationNotFoundException, InterruptedException, ApiKeyNotFoundException, APIKeyGenerationCheckException {
        TestConfigs testConfigs = ComponentUtils.invokeEndpoint(choreoComponent, Constant.displayType.restAPI.name(), accessToken);
        String apiInvocationRequestURI = "/getJwt";

        http().
                client(testConfigs.getInvokeUrl()).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT, "text/plain").
                header("API-Key", testConfigs.getApiKey());

        http().client(testConfigs.getInvokeUrl()).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
                validate(JsonMessageValidationContext.Builder.json());
    }


}

