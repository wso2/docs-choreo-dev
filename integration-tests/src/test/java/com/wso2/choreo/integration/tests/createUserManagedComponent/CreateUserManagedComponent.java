package com.wso2.choreo.integration.tests.createUserManagedComponent;

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
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.consol.citrus.message.MessageType;
import org.springframework.core.io.ClassPathResource;

import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * $(http()
 *
 * tests related to component creation from user managed repos
 */
public class CreateUserManagedComponent extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private static String invokeUrl;
    private static String apiKey;
    private static String apiId;
    private String repoName;
    private static ChoreoComponent testComponent;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoTestClientForGithub;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
        ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
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
        String srcGitHubURL = "https://github.com/".concat(Configuration.GITHUB_ORG).concat("/").concat(repoName);
        String graphQlQuery = "mutation{ createComponent(" +
                "      component: {" +
                "        name: \"" + componentName + "\"," +
                "        orgId: " + orgId + "," +
                "        orgHandler: \"" + orgHandle + "\"," +
                "        displayName: \"" + componentName + "\"," +
                "        displayType: \"" + Constant.displayType.restAPI + "\"," +
                "        projectId: \"" + projectId + "\"," +
                "        labels: \"\"," +
                "        version: \"1.0.0\"," +
                "        description: \"\"," +
                "        apiId: \"\"," +
                "        ballerinaVersion: \"swan-lake-alpha5\"," +
                "        triggerChannels: \"\"," +
                "        triggerID: null," +
                "        httpBase: true," +
                "        sampleTemplate: \"\"," +
                "        srcGitRepoUrl: \"" + srcGitHubURL + "\"" +
                "      }){" +
                "        id, orgId, projectId, handler" +
                "      }}";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper componentObjectMapper = new ObjectMapper();
        String componentRequestBody = componentObjectMapper.writeValueAsString(gqlRequestPayload);
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(componentRequestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createComponent/mutation_create_component_success.json"))
                .validate(json()
                        .ignore("$.data.createComponent.id")
                        .ignore("$.data.createComponent.handler")
                        .ignore("$.data.createComponent.projectId"))
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("createComponent");
                    componentHandler = component.get("handler").getAsString();
                    componentId = component.get("id").getAsString();
                }));
    }

    @Test(dependsOnMethods = {"testCreateUserManagedComponent"})
    @CitrusTest
    public void testCreatedComponentStatus() {
        // Poll component create status
        $(repeatOnError()
                .until("i = 15")
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

    @Test(dependsOnMethods = {"testCreatedComponentStatus"})
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
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createUserManagedComponent/get_pull_requests.json"))
                .validate(json()
                        .ignore("$.data.componentPullRequests[0].url")));
    }

    @Test(dependsOnMethods = {"testInitialPRGeneration"})
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
                                .client(choreoTestClient)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(listPrRequestBody),
                        http().client(choreoTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                        "templates/createUserManagedComponent/get_pull_requests_empty.json"))
                                .validate(json())));
    }

    @Test(dependsOnMethods = {"testPRMerge"})
    @CitrusTest
    public void testComponentRetrieval() throws JsonProcessingException {
        String graphQlQuery = "query{ component(" +
                "        projectId: \"" + projectId + "\"," +
                "        componentHandler: \"" + componentHandler + "\"," +
                "      ){" +
                "        id," +
                "        name," +
                "        handler," +
                "        description," +
                "        displayType," +
                "        displayName," +
                "        ownerName," +
                "        orgId," +
                "        orgHandler," +
                "        version," +
                "        labels," +
                "        createdAt," +
                "        updatedAt," +
                "        projectId," +
                "        apiId," +
                "        repository{" +
                "          nameApp," +
                "          nameConfig," +
                "          branch," +
                "          branchApp," +
                "          organizationApp," +
                "          organizationConfig," +
                "          isUserManage" +
                "        }," +
                "        apiVersions{" +
                "          apiVersion," +
                "          proxyName," +
                "          proxyUrl," +
                "          proxyId," +
                "          id," +
                "          state," +
                "          latest," +
                "          branch," +
                "          appEnvVersions{" +
                "            environmentId," +
                "            releaseId," +
                "            release{" +
                "              id," +
                "              metadata{" +
                "                choreoEnv" +
                "              }," +
                "              environmentId," +
                "              environment," +
                "              gitHash," +
                "              gitOpsHash," +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("component");
                    Gson gson = new Gson();
                    testComponent = gson.fromJson(component.toString(), RestApiChoreoComponent.class);
                }));
    }

    @Test(dependsOnMethods = {"testComponentRetrieval"})
    @CitrusTest
    public void testComponentDeployment() throws GetCommitHistoryException, IOException, InterruptedException,
            NoLatestCommitHashFoundException, NoLatestApiVersionFoundException, NoLatestAppEnvIdFoundException {
        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
        String latestVersionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
        String branch = testComponent.getRepository().getBranch();
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
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/deploy/gql_deploy_component_success.json"))
                .validate(json()));
    }

    @Test(dependsOnMethods = {"testComponentDeployment"})
    @CitrusTest
    public void testComponentDeploymentStatus() throws JsonProcessingException {
        String graphQlQuery = "query {" +
                "      DeploymentStatus(" +
                "        componentId: \"" + componentId + "\"," +
                "      ){" +
                "        success," +
                "        message," +
                "        data {" +
                "          conclusion," +
                "          status" +
                "        }" +
                "      }" +
                "    }";
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
                                .body(new ClassPathResource(
                                        "templates/deploy/deploy_status_success.json"))
                                .validate(json())));

    }

    @Test(dependsOnMethods = {"testComponentDeploymentStatus"})
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
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject invokeInformation = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonArray("invokeInformation").get(0).getAsJsonObject();
                    invokeUrl = invokeInformation.get("invokeUrl").getAsString();
                    apiId = invokeInformation.get("apiId").getAsString();
                }));

        String requestURI = Constant.APIS_ENDPOINT.concat("/").concat(apiId)
                .concat(Constant.GENERATE_KEY_ENDPOINT_SUFFIX).concat("?").concat(Constant.ORGANIZATION_ID)
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
        // Retry on failure for few seconds since config update takes some time to effect
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

    @Test(dependsOnMethods = {"testAPIInvocation"})
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
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createComponent/mutation_delete_component_success.json"))
                .validate(json()));
    }

    @Test(dependsOnMethods = {"testDeleteRestApiComponent"})
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

