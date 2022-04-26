package com.wso2.choreo.integration.tests.webhook;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.consol.citrus.message.MessageType;

import org.springframework.core.io.ClassPathResource;

import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * $(http()
 *
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
        private String orgHandle;
        private String orgId;
        private String orgUUID;
        private String projectId;
        private String componentId;
        private static String componentHandler;
        private static String invokeUrl;
        private static String sha;
        private String repoName;
        private static ChoreoComponent testComponent;
        private String namespace;
        private String obsId;

        @Autowired
        private HttpClient choreoTestClient;

        @Autowired
        private HttpClient choreoTestClientForGithub;

        @Autowired
        private HttpClient choreoCPTestClient;

        @Autowired
        private HttpClient choreoProjectsTestClient;

        @BeforeClass
        public void beforeClass()
                        throws IOException, InterruptedException, ProjectCreationException,
                        TokenRetrievalException {
                TokenHandler tokenHandler = new TokenHandler();
                accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
                ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                                String.valueOf(Configuration.TEST_CHOREO_ORG_ID),
                                Configuration.TEST_CHOREO_ORG_UUID);
                orgHandle = org.getOrgHandle();
                orgId = org.getOrgId();
                orgUUID = org.getOrgUUID();
                ChoreoProject project = org.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void testCreateUserManagedComponent() throws JsonProcessingException {
                // Creating new GitHub repo
                repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("name", repoName);
                                put("auto_init", true);
                                put("private", true);
                                put("gitignore_template", "nanoc");
                        }
                };
                String requestURI = "/orgs/".concat(Configuration.GITHUB_ORG).concat("/repos");
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(requestBodyMap);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.GITHUB_PAT);

                $(http()
                                .client(choreoTestClientForGithub)
                                .send()
                                .post(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClientForGithub)
                                .receive()
                                .response(HttpStatus.CREATED));

                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
                String srcGitHubURL = "https://github.com/".concat(Configuration.GITHUB_ORG).concat("/")
                                .concat(repoName);
                String graphQlQuery = "mutation{ createComponent(" +
                                " component: {" +
                                " name: \"" + componentName + "\"," +
                                " orgId: " + orgId + "," +
                                " orgHandler: \"" + orgHandle + "\"," +
                                " displayName: \"" + componentName + "\"," +
                                " displayType: \"" + Constant.displayType.webhook + "\"," +
                                " projectId: \"" + projectId + "\"," +
                                " labels: \"\"," +
                                " version: \"1.0.0\"," +
                                " description: \"\"," +
                                " apiId: \"\"," +
                                " ballerinaVersion: \"swan-lake-alpha5\"," +
                                " triggerChannels: \"IssuesService\"," +
                                " triggerID: 35," +
                                " httpBase: true," +
                                " sampleTemplate: \"\"," +
                                " srcGitRepoUrl: \"" + srcGitHubURL + "\"" +
                                " }){" +
                                " id, orgId, projectId, handler" +
                                " }}";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper componentObjectMapper = new ObjectMapper();
                String componentRequestBody = componentObjectMapper.writeValueAsString(gqlRequestPayload);
                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(componentRequestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/createComponent/mutation_create_component_success.json"))
                                .validate(json()
                                                .ignore("$.data.createComponent.id")
                                                .ignore("$.data.createComponent.handler")
                                                .ignore("$.data.createComponent.projectId"))
                                .validate((message, context) -> {
                                        JsonObject component = new JsonParser().parse((String) message.getPayload())
                                                        .getAsJsonObject()
                                                        .getAsJsonObject("data")
                                                        .getAsJsonObject("createComponent");
                                        componentHandler = component.get("handler").getAsString();
                                        componentId = component.get("id").getAsString();
                                }));
        }

        @Test(dependsOnMethods = {
                        "testCreateUserManagedComponent"
        })
        @CitrusTest
        public void testCreatedComponentStatus() {
                // Poll component create status
                $(repeatOnError()
                                .until("i = 25")
                                .index("i")
                                .autoSleep(6000)
                                .actions(
                                                http()
                                                                .client(choreoTestClient)
                                                                .send()
                                                                .get("/orgs/"
                                                                                .concat(orgHandle)
                                                                                .concat("/projects/")
                                                                                .concat(projectId)
                                                                                .concat("/components/")
                                                                                .concat(componentId)
                                                                                .concat("/init/status"))
                                                                .message()
                                                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                                                http().client(choreoTestClient)
                                                                .receive()
                                                                .response(HttpStatus.OK)
                                                                .message()
                                                                .body(new ClassPathResource(
                                                                                "templates/createComponent/get_create_status_success.json"))
                                                                .validate(json()
                                                                                .ignore("$.message"))));
        }

        @Test(dependsOnMethods = {
                        "testCreatedComponentStatus"
        })
        @CitrusTest
        public void testInitialPRGeneration() throws JsonProcessingException {
                String graphQlQuery = "query{ componentPullRequests(" +
                                " componentId: \"" + componentId + "\"," +
                                " ){" +
                                " url, number" +
                                " }}";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Check if initial PR has been generated
                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/createUserManagedComponent/get_pull_requests.json"))
                                .validate(json()
                                                .ignore("$.data.componentPullRequests[0].url")));
        }

        @Test(dependsOnMethods = {
                        "testInitialPRGeneration"
        })
        @CitrusTest
        public void testPRMerge() throws JsonProcessingException {
                String requestURI = "/repos/".concat(Configuration.GITHUB_ORG).concat("/").concat(repoName)
                                .concat("/pulls/1/merge");
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("commit_title", "Merge initial PR");
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(requestBodyMap);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.GITHUB_PAT);

                // Merge initial PR
                $(http()
                                .client(choreoTestClientForGithub)
                                .send()
                                .put(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoTestClientForGithub)
                                .receive()
                                .response(HttpStatus.OK));

                String graphQlQuery = "query{ componentPullRequests(" +
                                " componentId: \"" + componentId + "\"," +
                                " ){" +
                                " url, number" +
                                " }}";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };

                ObjectMapper pullRequestObjectMapper = new ObjectMapper();
                String listPrRequestBody = pullRequestObjectMapper.writeValueAsString(gqlRequestPayload);

                $(repeatOnError()
                                .until("i = 3")
                                .index("i")
                                .autoSleep(5000)
                                .actions(
                                                http()
                                                                .client(choreoProjectsTestClient)
                                                                .send()
                                                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                                                .message()
                                                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                                                .header(HttpHeaders.CONTENT_TYPE,
                                                                                MediaType.APPLICATION_JSON_VALUE)
                                                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                                                .body(listPrRequestBody),
                                                http()
                                                                .client(choreoProjectsTestClient)
                                                                .receive()
                                                                .response(HttpStatus.OK)
                                                                .message()
                                                                .type(MessageType.JSON)
                                                                .body(new ClassPathResource(
                                                                                "templates/createUserManagedComponent/get_pull_requests_empty.json"))
                                                                .validate(json())));
        }

        @Test(dependsOnMethods = {
                        "testPRMerge"
        })
        @CitrusTest
        public void testGetShaOfWebhookBal() {
                String requestURI = "/repos/".concat(Configuration.GITHUB_ORG).concat("/").concat(repoName)
                                .concat("/contents/webhook.bal");
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.GITHUB_PAT);
                $(http()
                                .client(choreoTestClientForGithub)
                                .send()
                                .get(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoTestClientForGithub)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                        JsonObject component = new JsonParser().parse((String) message.getPayload())
                                                        .getAsJsonObject();
                                        sha = component.get("sha").getAsString();
                                }));
        }

        @Test(dependsOnMethods = {
                        "testGetShaOfWebhookBal"
        })
        @CitrusTest
        public void testCommitFile() throws IOException {
                String requestURI = "/repos/".concat(Configuration.GITHUB_ORG).concat("/").concat(repoName)
                                .concat("/contents/webhook.bal");
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("message", "Add log to onIssueOpend");
                                put("content", Base64.getEncoder().encodeToString((new ClassPathResource(
                                                "templates/webhook/github_webhook.bal")).getInputStream()
                                                .readAllBytes()));
                                put("sha", sha);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(requestBodyMap);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.GITHUB_PAT);

                // Commit the webhook.bal file to the repository
                $(http()
                                .client(choreoTestClientForGithub)
                                .send()
                                .put(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoTestClientForGithub)
                                .receive()
                                .response(HttpStatus.OK));
        }

        @Test(dependsOnMethods = {
                        "testCommitFile"
        })
        @CitrusTest
        public void testComponentRetrieval() throws JsonProcessingException {
                String graphQlQuery = "query{ component(" +
                                " projectId: \"" + projectId + "\"," +
                                " componentHandler: \"" + componentHandler + "\"," +
                                " ){" +
                                " id," +
                                " name," +
                                " handler," +
                                " description," +
                                " displayType," +
                                " displayName," +
                                " ownerName," +
                                " orgId," +
                                " orgHandler," +
                                " version," +
                                " labels," +
                                " createdAt," +
                                " updatedAt," +
                                " projectId," +
                                " apiId," +
                                " repository{" +
                                " nameApp," +
                                " nameConfig," +
                                " branch," +
                                " branchApp," +
                                " organizationApp," +
                                " organizationConfig," +
                                " isUserManage" +
                                " }," +
                                " apiVersions{" +
                                " apiVersion," +
                                " proxyName," +
                                " proxyUrl," +
                                " proxyId," +
                                " id," +
                                " state," +
                                " latest," +
                                " branch," +
                                " appEnvVersions{" +
                                " environmentId," +
                                " releaseId," +
                                " release{" +
                                " id," +
                                " metadata{" +
                                " choreoEnv" +
                                " }," +
                                " environmentId," +
                                " environment," +
                                " gitHash," +
                                " gitOpsHash," +
                                " }" +
                                " }" +
                                " }" +
                                " }" +
                                " }";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                        JsonObject component = new JsonParser().parse((String) message.getPayload())
                                                        .getAsJsonObject()
                                                        .getAsJsonObject("data")
                                                        .getAsJsonObject("component");
                                        Gson gson = new Gson();
                                        testComponent = gson.fromJson(component.toString(),
                                                        RestApiChoreoComponent.class);
                                }));
        }

        @Test(dependsOnMethods = {
                        "testComponentRetrieval"
        })
        @CitrusTest
        public void testComponentDeployment() throws GetCommitHistoryException,
                        IOException, InterruptedException,
                        NoLatestCommitHashFoundException, NoLatestApiVersionFoundException,
                        NoLatestAppEnvIdFoundException {
                JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
                String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
                String latestVersionId = testComponent.getLatestApiVersion().getId();
                String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
                String branch = testComponent.getRepository().getBranch();
                String name = testComponent.getName();

                String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
                                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                                .concat(devEnvIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");

                List<BalConfig> configs = new ArrayList<>();
                configs.add(new BalConfig("config.webhookSecret", true, "abcd", "string"));
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("moduleName", name);
                                put("commitHash", latestCommitSha);
                                put("applyNow", false);
                                put("operation", 0);
                                put("sourceUuid", "");
                                put("configs", configs);
                        }
                };

                ObjectMapper configurationsObjectMapper = new ObjectMapper();
                String configurationsRequestBody = configurationsObjectMapper.writeValueAsString(requestBodyMap);

                // Update configurations
                $(http()
                                .client(choreoTestClient)
                                .send()
                                .post(configurationsUpdateRequestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(configurationsRequestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClient)
                                .receive()
                                .response(HttpStatus.OK));

                String graphQlQuery = "mutation {deployComponent(" +
                                " deployment: {" +
                                " componentId: \"" + componentId + "\"," +
                                " versionId: \"" + latestVersionId + "\"," +
                                " envId: \"" + devEnvIdToDeploy + "\"," +
                                " branch: \"" + branch + "\"," +
                                " sha: \"" + latestCommitSha + "\"," +
                                " }" +
                                " ) { message, success }}";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Deploy component
                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource("templates/deploy/gql_deploy_component_success.json"))
                                .validate(json()));
        }

        @Test(dependsOnMethods = {
                        "testComponentDeployment"
        })
        @CitrusTest
        public void testComponentDeploymentStatus() throws JsonProcessingException {
                String graphQlQuery = "query {" +
                                " DeploymentStatus(" +
                                " componentId: \"" + componentId + "\"," +
                                " ){" +
                                " success," +
                                " message," +
                                " data {" +
                                " conclusion," +
                                " status" +
                                " }" +
                                " }" +
                                " }";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Poll deployment status
                $(repeatOnError()
                                .until("i = 25")
                                .index("i")
                                .autoSleep(6000)
                                .actions(
                                                http()
                                                                .client(choreoProjectsTestClient)
                                                                .send()
                                                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                                                .message()
                                                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                                                .body(requestBody)
                                                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                                                http().client(choreoProjectsTestClient)
                                                                .receive()
                                                                .response(HttpStatus.OK)
                                                                .message()
                                                                .body(new ClassPathResource(
                                                                                "templates/deploy/deploy_status_success.json"))
                                                                .validate(json())));

        }

        @Test(dependsOnMethods = {
                        "testComponentDeploymentStatus"
        })
        @CitrusTest
        public void testAPIInvocation() throws NoLatestApiVersionFoundException,
                        IOException, NoSuchAlgorithmException, InvalidKeyException {

                String latestVersionId = testComponent.getLatestApiVersion().getId();
                String graphQlQuery = "query {" +
                                " invokeInformation(" +
                                " orgHandler: \"" + orgHandle + "\"," +
                                " orgUuid: \"" + orgUUID + "\"," +
                                " componentId: \"" + componentId + "\"," +
                                " versionId: \"" + latestVersionId + "\"," +
                                " componentType: \"webhook\"" +
                                " ) { apiId, invokeUrl } }";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                        JsonObject invokeInformation = new JsonParser()
                                                        .parse((String) message.getPayload()).getAsJsonObject()
                                                        .getAsJsonObject("data")
                                                        .getAsJsonArray("invokeInformation").get(0).getAsJsonObject();
                                        invokeUrl = invokeInformation.get("invokeUrl").getAsString();
                                }));

                // Read the request as a json make it as a compact json string
                // Make the hex digest of the body, to be sent with the mock request
                JsonObject requestJsonObj = (new JsonParser()).parse(new String(new ClassPathResource(
                                "templates/webhook/request.json").getInputStream().readAllBytes())).getAsJsonObject();
                String requestPayloadJson = requestJsonObj.toString();
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec("abcd".getBytes("UTF-8"),
                                "HmacSHA256");
                sha256_HMAC.init(secret_key);
                byte[] hexDigest = sha256_HMAC.doFinal(requestPayloadJson.getBytes("UTF-8"));
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
                                                                .client(invokeUrl)
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
                                                                .client(invokeUrl)
                                                                .receive()
                                                                .response(HttpStatus.OK)
                                                                .message()
                                                                .type(MessageType.PLAINTEXT)));
        }

        @Test(dependsOnMethods = {
                        "testAPIInvocation"
        })
        @CitrusTest
        public void testFetchObservabilityId() throws NoLatestApiVersionFoundException, IOException {

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

        @Test(dependsOnMethods = {
                        "testFetchObservabilityId"
        })
        @CitrusTest
        public void testObservabilityLogs() throws NoLatestApiVersionFoundException, IOException {

                String releaseId = testComponent.getLatestApiVersion().getAppEnvVersions().get(0).getReleaseId();
                OffsetDateTime currentDateTimeAtUTC = OffsetDateTime.now(ZoneOffset.UTC)
                                .truncatedTo(ChronoUnit.MILLIS);
                OffsetDateTime oneHourAgoDateTimeAtUTC = currentDateTimeAtUTC.minusHours(1);
                OffsetDateTime oneHourAfterDateTimeAtUTC = currentDateTimeAtUTC.plusHours(1);

                String requestURI = "observability/logging/0.1.0/applications/" + obsId +
                                "/logsV2?" +
                                "startTime=" + oneHourAgoDateTimeAtUTC.toString() + "&endTime=" +
                                oneHourAfterDateTimeAtUTC.toString() +
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

        @Test(dependsOnMethods = {
                        "testObservabilityLogs"
        }, alwaysRun = true)
        @CitrusTest
        public void testDeleteWebhookComponent() throws JsonProcessingException {
                String graphqlQuery = "mutation { deleteComponentV2(" +
                                "orgHandler: \"" + orgHandle + "\"," +
                                "componentId: \"" + componentId + "\"," +
                                "projectId: \"" + projectId + "\"){ status }" +
                                "}";

                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphqlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Delete component
                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/createComponent/mutation_delete_component_success.json"))
                                .validate(json()));
        }

        @Test(dependsOnMethods = {
                        "testDeleteWebhookComponent"
        }, alwaysRun = true)
        @CitrusTest
        public void testDeleteRepo() {
                String requestURI = "/repos/".concat(Configuration.GITHUB_ORG).concat("/").concat(repoName);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.GITHUB_PAT);

                // Delete repository
                $(http()
                                .client(choreoTestClientForGithub)
                                .send()
                                .delete(requestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                                .client(choreoTestClientForGithub)
                                .receive()
                                .response(HttpStatus.NO_CONTENT));
        }
}
