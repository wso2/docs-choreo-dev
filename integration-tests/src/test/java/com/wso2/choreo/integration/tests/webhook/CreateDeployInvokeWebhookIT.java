package com.wso2.choreo.integration.tests.webhook;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.FileUtil;

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.fail;

/**
 * $(http()
 * <p>
 * tests related to webhooks, below steps are executed
 * 1. Create a webhook with an user managed Github respository
 * 2. Commit the webhook.bal file with a log (a log will be printed when Github
 * sends an event)
 * 3. Deploy the component
 * 4. Send a mock event to the deployed webhook
 * 5. Analyze the logs to check whether the relevant log is printed or not
 */
public class CreateDeployInvokeWebhookIT extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static ChoreoComponent testComponent;
    ChoreoOrganization org;
    private String orgUUID;
    private String projectId;
    private String repoName;
    private String namespace;
    private String obsId;
    private String devInvokeURL;

    private ComponentCreationResponse response;

    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void beforeClassWHIT()
            throws IOException, InterruptedException, ProjectCreationException,
            TokenRetrievalException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void testCreateUserManagedComponentWHIT() throws IOException {

        // Creating new GitHub repo
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");

        // Creating component
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));


        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(componentName).
                srcGitRepoUrl(GitHub.getGitHubRepoUrl(repoName)).
                displayName(componentName).projectId(projectId).
                triggerChannels("IssuesService").triggerID("35").
                displayType(Constant.displayType.webhook.name()).build();
        response = GraphQL.createUserManagedComponent(graphqlDTO, accessToken);
        Assert.assertEquals(response.getProjectId(), projectId);

    }

    @Test(dependsOnMethods = {"testCreateUserManagedComponentWHIT"})
    @CitrusTest
    public void testCreatedComponentStatusWHIT() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, response.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }

    @Test(dependsOnMethods = {"testCreatedComponentStatusWHIT"})
    @CitrusTest
    public void testInitialPRGenerationWHIT() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 1);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"testInitialPRGenerationWHIT"})
    @CitrusTest
    public void testPRMergeWHIT() throws IOException, UnexpectedResponseException {
        Response ghres = GitHub.mergePR(repoName, "1");
        Assert.assertEquals(ghres.getStatusCode(), HttpStatus.OK.value());

        PullRequest[] pullRequests = GraphQL.getComponentPullRequests(response.getId(), accessToken, 0);
        Assert.assertEquals(pullRequests.length, 0);
    }


    @Test(dependsOnMethods = {"testPRMergeWHIT"})
    @CitrusTest
    public void testCommitFileWHIT() throws IOException {
        String encodedContent = FileUtil.readFileEncodedContent("templates/webhook/github_webhook.bal");
        Response response = GitHub.mergeNewCode(repoName, "webhook.bal", "Add log to onIssueOpend", encodedContent);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testCommitFileWHIT"})
    @CitrusTest
    public void testComponentRetrievalWHIT() throws IOException {
        testComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        testComponent.setOrganization(org);
        Assert.assertNotNull(testComponent);
    }

    @Test(dependsOnMethods = {"testComponentRetrievalWHIT"})
    @CitrusTest
    public void testComponentDeploymentWHIT() throws Exception {
        BalConfig balConfigs = BalConfig.builder().isRequired(true).configKeyName("config.webhookSecret").valueType("string").valueOrSource("abcd").build();
        Orgs.addConfiguration(choreoTestClient, this, testComponent, "dev", balConfigs);
        GraphQL.deployComponent( testComponent,accessToken);
    }

    @Test(dependsOnMethods = {"testComponentDeploymentWHIT"})
    @CitrusTest
    public void testDeploymentStatusByVersionWHIT() throws Exception {
        GraphQL.deploymentStatusByVersion(testComponent,accessToken);
    }

    @Test(dependsOnMethods = {"testDeploymentStatusByVersionWHIT"})
    @CitrusTest
    public void testComponentDeploymentStatusWHIT() throws Exception {
        devInvokeURL =   GraphQL.componentDeployment(testComponent, "dev", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"testComponentDeploymentStatusWHIT"})
    @CitrusTest
    public void testAPIInvocationWHIT() throws Exception {
        String apiKey = ComponentUtils.getApiKey(testComponent, accessToken);

        // Read the request as a json make it as a compact json string
        // Make the hex digest of the body, to be sent with the mock request
        JsonObject requestJsonObj = (new JsonParser()).parse(new String(new ClassPathResource(
                "templates/webhook/request.json").getInputStream().readAllBytes())).getAsJsonObject();
        String requestPayloadJson = requestJsonObj.toString();
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec("abcd".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hexDigest = sha256_HMAC.doFinal(requestPayloadJson.getBytes(StandardCharsets.UTF_8));
        String apiInvocationRequestURI = "/";

        // Test API Invocation
        // Retry on failure for few seconds since config update takes some time to
        // effect
        $(repeatOnError()
                .until("i = 10")
                .index("i")
                .autoSleep(6000)
                .actions(
                        http()
                                .client(devInvokeURL)
                                .send()
                                .post(apiInvocationRequestURI)
                                .message()
                                .header(HttpHeaders.ACCEPT, "text/plain")
                                .header("X-GitHub-Event", "issues")
                                .header("X-Hub-Signature-256",
                                        "sha256=" + Hex.encodeHexString(
                                                hexDigest))
                                .body(new ClassPathResource(
                                        "templates/webhook/request.json")),
                        http()
                                .client(devInvokeURL)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.PLAINTEXT)));
    }

    @Test(dependsOnMethods = {"testAPIInvocationWHIT"})
    @CitrusTest
    public void testFetchObservabilityIdWHIT() throws Exception {

        String releaseId = testComponent.getLatestApiVersion().getAppEnvVersions().get(0).getReleaseId();
        String environmentsGraphQlQuery = "query {" +
                "environments(orgUuid:\"" + orgUUID + "\"){" +
                "organizationUuid," +
                "orgShared," +
                "name," +
                "description," +
                "id," +
                "choreoEnv," +
                "projectId," +
                "promoteFrom," +
                "namespace," +
                "}" +
                "}";
        HashMap<String, String> environmentsGqlRequestPayload = new HashMap<>() {
            {
                put("query", environmentsGraphQlQuery);
            }
        };
        ObjectMapper envsObjectMapper = new ObjectMapper();
        String envsRequestBody = envsObjectMapper.writeValueAsString(environmentsGqlRequestPayload);
        $(http()
                .client(choreoProjectsTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(envsRequestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoProjectsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    namespace = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonArray("environments")
                            .get(0)
                            .getAsJsonObject()
                            .get("namespace")
                            .getAsString();
                }));

        String obsGraphQlQuery = "query {" +
                "observerbilityIds(" +
                "releaseIds: \"" + releaseId + "\"" +
                "){" +
                "obsId," +
                "releaseId," +
                "verzion," +
                "}" +
                "}";
        HashMap<String, String> obsGqlRequestPayload = new HashMap<>() {
            {
                put("query", obsGraphQlQuery);
            }
        };
        ObjectMapper obsObjectMapper = new ObjectMapper();
        String obsRequestBody = obsObjectMapper.writeValueAsString(obsGqlRequestPayload);

        $(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(6000)
                .actions(
                        http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE,
                                        MediaType.APPLICATION_JSON_VALUE)
                                .body(obsRequestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        obsId = new JsonParser()
                                                .parse((String) message
                                                        .getPayload())
                                                .getAsJsonObject()
                                                .getAsJsonObject("data")
                                                .getAsJsonArray("observerbilityIds")
                                                .get(0)
                                                .getAsJsonObject()
                                                .get("obsId")
                                                .getAsString();
                                    } catch (UnsupportedOperationException e) {
                                        fail("Could not fetch the observerbility id: "
                                                .concat((String) message
                                                        .getPayload()));
                                    }
                                })));
    }

    @Test(dependsOnMethods = {"testFetchObservabilityIdWHIT"})
    @CitrusTest
    public void testObservabilityLogsWHIT() throws Exception {

        String releaseId = testComponent.getLatestApiVersion().getAppEnvVersions().get(0).getReleaseId();
        OffsetDateTime currentDateTimeAtUTC = OffsetDateTime.now(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
        OffsetDateTime oneHourAgoDateTimeAtUTC = currentDateTimeAtUTC.minusHours(1);
        OffsetDateTime oneHourAfterDateTimeAtUTC = currentDateTimeAtUTC.plusHours(1);

        String requestURI = "observability/logging/0.1.0/applications/" + obsId +
                "/logsV2?" +
                "startTime=" + oneHourAgoDateTimeAtUTC + "&endTime=" +
                oneHourAfterDateTimeAtUTC +
                "&releaseId=" + releaseId +
                "&namespace=" + namespace +
                "&searchPhrase=ChoreoIntegrationTest" +
                "&sort=desc&limit=95";

        $(repeatOnError()
                .until("i = 25")
                .index("i")
                .autoSleep(6000)
                .actions(
                        http()
                                .client(choreoCPTestClient)
                                .send()
                                .get(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header("x-console-version", "v2")
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(choreoCPTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate(jsonPath().expression(
                                        "$.rows.size()",
                                        greaterThanOrEqualTo(1)))));
    }

    @Test(dependsOnMethods = {"testObservabilityLogsWHIT"}, alwaysRun = true)
    @CitrusTest
    public void testDeleteWebhookComponentWHIT() throws Exception {
        Response res = GraphQL.deleteComponent(response.getId(), projectId, accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testDeleteWebhookComponentWHIT"}, alwaysRun = true)
    @CitrusTest
    public void testDeleteRepoWHIT() {
        Response response = GitHub.deleteGitHubRepo(repoName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT.value());
    }

}
