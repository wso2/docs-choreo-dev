package com.wso2.choreo.integration.tests.createUserManagedNonEmptyComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * $(http()
 *
 * tests related to component creation from user managed non empty repo root.
 */
public class CreateUserManagedNonEmptyComponentRoot extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgHandle;
        private String orgId;
        private String orgUUID;
        private String projectId;
        private String componentId;
        private static String componentHandler;
        private static String invokeUrl;
        private static String prNumber;
        private static String apiKey;
        private static String apiId;
        private String repoName = "byor-greetings-app1";
        private String repoType = "UserManagedNonEmpty";
        private String repoBranch = "dev";
        private String prBranch;
        private String githubOrg;
        private String githubPAT;
        private static ChoreoComponent testComponent;

        @Autowired
        private HttpClient choreoTestClient;

        @Autowired
        private HttpClient choreoProjectsTestClient;

        @Autowired
        private HttpClient choreoTestClientForGithub;

        @Autowired
        private HttpClient choreoTestClientForSTS;

        @BeforeClass
        public void beforeClass()
                throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
                orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
                orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
                githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
                ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
                ChoreoProject project = org.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void testCreateUserManagedComponent() throws JsonProcessingException, IOException {

                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
                String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/")
                        .concat(repoName);
                APICreator testAPI = new APICreator();
                String repoSubpath = "";
                String graphQlQuery = testAPI.createUserManagedNonEmptyComponentCreationQuery(
                        componentName, orgId, orgHandle, projectId, srcGitHubURL, repoSubpath, repoType, repoBranch);
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {{
                        put(Constant.QUERY, graphQlQuery);
                }};

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

        @Test(dependsOnMethods = { "testCreateUserManagedComponent" })
        @CitrusTest
        public void testCreatedComponentStatus() {
                // Poll component create status
                $(repeatOnError()
                        .until("i = 50")
                        .index("i")
                        .autoSleep(5000)
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

        @Test(dependsOnMethods = { "testCreatedComponentStatus" })
        @CitrusTest
        public void testInitialPRGeneration() throws JsonProcessingException {
                String graphQlQuery = "query{ componentPullRequests(" +
                        "        componentId: \"" + componentId + "\"," +
                        "      ){" +
                        "        url, number" +
                        "      }}";
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
                        .validate((message, context) -> {
                                JsonObject prInformation = new JsonParser().parse((String) message.getPayload())
                                        .getAsJsonObject()
                                        .getAsJsonObject("data")
                                        .getAsJsonArray("componentPullRequests").get(0)
                                        .getAsJsonObject();
                                prNumber = prInformation.get("number").getAsString();
                        }));
        }

        @Test(dependsOnMethods = { "testInitialPRGeneration" })
        @CitrusTest
        public void testPRMerge() throws JsonProcessingException {
                String requestURI = "/repos/".concat(githubOrg).concat("/").concat(repoName)
                        .concat("/pulls/" + prNumber + "/merge");
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("commit_title", "Merge initial PR");
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(requestBodyMap);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);

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
                        "        componentId: \"" + componentId + "\"," +
                        "      ){" +
                        "        url, number" +
                        "      }}";
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
                                http().client(choreoProjectsTestClient)
                                        .receive()
                                        .response(HttpStatus.OK)
                                        .message()
                                        .type(MessageType.JSON)
                                        .body(new ClassPathResource(
                                                "templates/createUserManagedComponent/get_pull_requests_empty.json"))
                                        .validate(json())));
        }

        @Test(dependsOnMethods = { "testPRMerge" })
        @CitrusTest
        public void testBranchDelete() throws JsonProcessingException {
                String requestURI = "/repos/".concat(githubOrg).concat("/").concat(repoName)
                        .concat("/pulls/" + prNumber);
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);

                // get merged PR branch
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
                                JsonObject prBranchInformation = new JsonParser()
                                        .parse((String) message.getPayload()).getAsJsonObject()
                                        .getAsJsonObject("head");
                                prBranch = prBranchInformation.get("ref").getAsString();
                        }));

                // delete merged PR branch
                String deleteRequestURI = "/repos/".concat(githubOrg).concat("/").concat(repoName)
                        .concat("/git/refs/heads/" + prBranch);

                $(http()
                        .client(choreoTestClientForGithub)
                        .send()
                        .delete(deleteRequestURI)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                        .client(choreoTestClientForGithub)
                        .receive()
                        .response(HttpStatus.NO_CONTENT));
        }

        @Test(dependsOnMethods = { "testBranchDelete" })
        @CitrusTest
        public void testComponentRetrieval() throws JsonProcessingException, IOException {
                APICreator testAPI = new APICreator();
                String graphQlQuery = testAPI.getComponentDetailsQuery(projectId, componentHandler);
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put(Constant.QUERY, graphQlQuery);
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

        @Test(dependsOnMethods = { "testComponentRetrieval" })
        @CitrusTest
        public void testComponentDeployment() throws GetCommitHistoryException, IOException, InterruptedException,
                NoLatestCommitHashFoundException, NoLatestApiVersionFoundException,
                NoLatestAppEnvIdFoundException {
                JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
                String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
                String latestVersionId = testComponent.getLatestApiVersion().getId();
                String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
                String branch = testComponent.getRepository().getBranch(); // todokeshi check if branch correct
                String name = testComponent.getName();

                String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
                        .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                        .concat(devEnvIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");
                HashMap<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("moduleName", name);
                                put("commitHash", latestCommitSha);
                                put("applyNow", false);
                                put("operation", 0);
                                put("sourceUuid", "");
                                put("configs", "");
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
                        "        deployment: {" +
                        "          componentId: \"" + componentId + "\"," +
                        "          versionId: \"" + latestVersionId + "\"," +
                        "          envId: \"" + devEnvIdToDeploy + "\"," +
                        "          branch: \"" + branch + "\"," +
                        "          sha: \"" + latestCommitSha + "\"," +
                        "        }" +
                        "      ) { message, success }}";
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

        @Test(dependsOnMethods = { "testComponentDeployment" })
        @CitrusTest
        public void testDeploymentStatusByVersion() throws Exception {
                String versionId = testComponent.getLatestApiVersion().getId();

                String graphQlQuery = "query {" +
                        "      deploymentStatusByVersion(" +
                        "        componentId: \"" + componentId + "\"," +
                        "        versionId: \"" + versionId + "\"" +
                        "  ) {" +
                        "    id" +
                        "    sha" +
                        "    completed_at" +
                        "    started_at" +
                        "    name" +
                        "    status" +
                        "    conclusion" +
                        "  }" +
                        "}";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Poll deployment status
                $(repeatOnError()
                        .until("i = 50")
                        .index("i")
                        .autoSleep(5000)
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
                                                "templates/deploy/deploy_status_by_version_success.json"))));

        }

        @Test(dependsOnMethods = { "testDeploymentStatusByVersion" })
        @CitrusTest
        public void testComponentDeploymentStatus() throws Exception {
                String versionId = testComponent.getLatestApiVersion().getId();
                String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
                Map<String, String> params = new HashMap<>();
                params.put("orgHandler", orgHandle);
                params.put("orgUuid", orgUUID);
                params.put("componentId", componentId);
                params.put("versionId", versionId);
                params.put("environmentId", devEnvIdToDeploy);

                String graphQlQuery = ComponentUtils.generateStringFromTemplate(
                        "templates/deploy/graphql/componentDeployment.mustache", params);
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
                String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);

                Map<String, String> responseParams = new HashMap<>();
                responseParams.put("environmentId", devEnvIdToDeploy);
                responseParams.put("sha", latestCommitSha);
                responseParams.put("versionId", versionId);

                String expectedResponse = ComponentUtils.generateStringFromTemplate(
                        "templates/deploy/deploy_managed_status_success.mustache", responseParams);

                // Poll deployment status
                $(repeatOnError()
                        .until("i = 25")
                        .index("i")
                        .autoSleep(5000)
                        .actions(
                                http()
                                        .client(choreoTestClient)
                                        .send()
                                        .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                        .message()
                                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                                        .body(requestBody)
                                        .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                                http().client(choreoTestClient)
                                        .receive()
                                        .response(HttpStatus.OK)
                                        .message()
                                        .body(expectedResponse)));
        }

        @Test(dependsOnMethods = { "testComponentDeploymentStatus" })
        @CitrusTest
        public void testAPIInvocation() throws NoLatestApiVersionFoundException, IOException {

                String latestVersionId = testComponent.getLatestApiVersion().getId();
                String graphQlQuery = "query {" +
                        "      invokeInformation(" +
                        "        orgHandler: \"" + orgHandle + "\"," +
                        "        orgUuid: \"" + orgUUID + "\"," +
                        "        componentId: \"" + componentId + "\"," +
                        "        versionId: \"" + latestVersionId + "\"," +
                        "        componentType: \"restAPI\"" +
                        "      ) { apiId, invokeUrl } }";
                HashMap<String, String> gqlRequestPayload = new HashMap<>() {
                        {
                                put("query", graphQlQuery);
                        }
                };
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

                // Get invokeUrl and apiId
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
                                apiId = invokeInformation.get("apiId").getAsString();
                        }));

                String requestURI = Constant.APIS_ENDPOINT.concat("/").concat(apiId)
                        .concat(Constant.GENERATE_KEY_ENDPOINT_SUFFIX).concat("?")
                        .concat(Constant.ORGANIZATION_ID)
                        .concat("=").concat(orgUUID);

                // Generate API key for invocation
                $(http()
                        .client(choreoTestClientForSTS)
                        .send()
                        .post(requestURI)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                        .client(choreoTestClientForSTS)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .validate((message, context) -> {
                                apiKey = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                                        .get("apikey").getAsString();
                        }));

                // Test API Invocation
                // Retry on failure for few seconds since config update takes some time to
                // effect
                String apiInvocationRequestURI = "/greeting?name=TestUser";

                $(repeatOnError()
                        .until("i = 15")
                        .index("i")
                        .autoSleep(6000)
                        .actions(
                                http()
                                        .client(invokeUrl)
                                        .send()
                                        .get(apiInvocationRequestURI)
                                        .message()
                                        .header(HttpHeaders.ACCEPT, "text/plain")
                                        .header("API-Key", apiKey),
                                http()
                                        .client(invokeUrl)
                                        .receive()
                                        .response(HttpStatus.OK)
                                        .message()
                                        .type(MessageType.PLAINTEXT)));
        }

        @Test(dependsOnMethods = { "testAPIInvocation" })
        @CitrusTest
        public void testDeleteRestApiComponent() throws JsonProcessingException {
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
}
