package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.responses.CreateComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.common.utils.GQLutil;
import com.wso2.choreo.integration.common.utils.GitUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
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
import java.util.logging.Logger;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.dsl.JsonSupport.json;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class TestClientJwTValidation extends TestNGCitrusSpringSupport {
    private final Logger logger = Logger.getLogger("TestClientJwTValidation");


    private String githubOrg;
    private String githubPAT;
    private String orgId;
    private String orgHandle;
    private String projectId;
    private String orgUUID;
    private String invokeUrl;
    private String apiKey;
    private String apiId;
    String repoName;
    private String serviceBalSha;
    private String accessToken;

    private static ChoreoComponent testComponent;
    private CreateComponent response;
    @Autowired
    private HttpClient choreoTestClientForAsgardeo;
    @Autowired
    private HttpClient choreoTestClientForSTS;
    @Autowired
    private HttpClient choreoTestClientForGithub;
    @Autowired
    private HttpClient choreoProjectsTestClient;
    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup() throws TokenRetrievalException, IOException, ProjectCreationException, InterruptedException {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
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
    public void testCreateUserManagedComponent() throws JsonProcessingException {
        String requestURI = "/orgs/".concat(githubOrg).concat("/repos");
        String gitRepoCreationRequest = GitUtil.getGitRepo(repoName, true, true, "nanoc");
        String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);
        String srcGitHubURL = "https://github.com/".concat(githubOrg).concat("/") + repoName + "/jwt/main";
        $(http().client(choreoTestClientForGithub).
                send().
                post(requestURI).
                message().
                header(HttpHeaders.AUTHORIZATION, authHeader).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(gitRepoCreationRequest).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http().client(choreoTestClientForGithub).
                receive().response(HttpStatus.CREATED));
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        String componentRequestBody = GQLutil.createComponent(componentName, orgId, orgHandle, Constant.displayType.restAPI.name(), projectId, srcGitHubURL, null, "");


        $(http().client(choreoProjectsTestClient).
                send().
                post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(componentRequestBody).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().client(choreoProjectsTestClient).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/createComponent/mutation_create_component_success.json")).
                validate(json().ignore("$.data.createComponent.id").ignore("$.data.createComponent.handler").ignore("$.data.createComponent.projectId")).
                validate((message, context) -> {
                    response = ObjectMapperUtil.mapStringToObject(CreateComponent.class, message.getPayload().toString(), "createComponent");

                }));
    }

    @Test(dependsOnMethods = {"testCreateUserManagedComponent"})
    @CitrusTest
    public void testCreatedComponentStatus() {
        // Poll component create status
        $(repeatOnError().
                until("i = 25").
                index("i").
                autoSleep(10000).
                actions(http().
                        client(choreoTestClient).
                        send().
                        get("/orgs/".concat(orgHandle).concat("/projects/").concat(projectId).concat("/components/").concat(response.getId()).concat("/init/status")).
                        message().
                        header(HttpHeaders.AUTHORIZATION, accessToken).
                        accept(String.valueOf(MediaType.APPLICATION_JSON)), http().
                        client(choreoTestClient).
                        receive().
                        response(HttpStatus.OK).
                        message().
                        body(new ClassPathResource("templates/createComponent/get_create_status_success.json")).
                        validate(json().ignore("$.message"))));
    }


    @Test(dependsOnMethods = {"testCreatedComponentStatus"})
    @CitrusTest
    public void testInitialPRGeneration() throws JsonProcessingException {
        String requestBody = GQLutil.getComponentPullRequests(response.getId());

        // Check if initial PR has been generated
        $(http().
                client(choreoProjectsTestClient).
                send().post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                message().header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(requestBody).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().
                client(choreoProjectsTestClient).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/createUserManagedComponent/get_pull_requests.json")).
                validate(JsonMessageValidationContext.Builder.json().ignore("$.data.componentPullRequests[0].url")));
    }


    @Test(dependsOnMethods = {"testInitialPRGeneration"})
    @CitrusTest
    public void testPRMerge() throws JsonProcessingException {
        String requestURI = "/repos/".concat(githubOrg).concat("/").concat(repoName).concat("/pulls/1/merge");

        String requestBody = GitUtil.mergePR();
        String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);

        // Merge initial PR
        $(http().client(choreoTestClientForGithub).
                send().
                put(requestURI).
                message().
                header(HttpHeaders.AUTHORIZATION, authHeader).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(requestBody).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().
                client(choreoTestClientForGithub).
                receive().response(HttpStatus.OK));

        String listPrRequestBody = GQLutil.getComponentPullRequests(response.getId());

        $(repeatOnError().
                until("i = 3").
                index("i").
                autoSleep(5000).
                actions(http().
                        client(choreoProjectsTestClient).
                        send().
                        post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                        message().
                        header(HttpHeaders.AUTHORIZATION, accessToken).
                        header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                        accept(String.valueOf(MediaType.APPLICATION_JSON)).
                        body(listPrRequestBody), http().client(choreoProjectsTestClient).
                        receive().
                        response(HttpStatus.OK).
                        message().
                        type(MessageType.JSON).
                        body(new ClassPathResource("templates/createUserManagedComponent/get_pull_requests_empty.json")).
                        validate(JsonMessageValidationContext.Builder.json())));
    }

    @Test(dependsOnMethods = {"testPRMerge"})
    @CitrusTest
    public void testGetSHA() {
        String requestUrl = "/repos/" + githubOrg + "/" + repoName + "/contents/service.bal";
        String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);

        // Update service.bal with new code
        $(http().
                client(choreoTestClientForGithub).
                send().get(requestUrl).
                message().
                header(HttpHeaders.AUTHORIZATION, authHeader).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().
                client(choreoTestClientForGithub).
                receive().
                response(HttpStatus.OK).
                message().
                validate((message, testContext) -> {
                    JsonObject jsonObject = new JsonParser().parse(message.getPayload().toString()).getAsJsonObject();
                    serviceBalSha = jsonObject.get("sha").getAsString();
                }));
    }

    @Test(dependsOnMethods = {"testGetSHA"})
    @CitrusTest
    public void testMergeNewCode() throws JsonProcessingException {

        String requestUrl = "/repos/" + githubOrg + "/" + repoName + "/contents/service.bal";
        String encodedContent = FileUtil.readFile("src/test/resources/templates/encodedbal/service.bal");
        String payload = GitUtil.mergeNewCode("update code", encodedContent, serviceBalSha);
        String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);
        $(http().
                client(choreoTestClientForGithub).
                send().
                put(requestUrl).
                message().
                header(HttpHeaders.AUTHORIZATION, authHeader).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(payload).accept(String.valueOf(MediaType.APPLICATION_JSON)));


        String listPrRequestBody = GQLutil.getComponentPullRequests(response.getId());

        $(repeatOnError().
                until("i = 3").
                index("i").
                autoSleep(5000).
                actions(http().
                        client(choreoProjectsTestClient).
                        send().
                        post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                        message().
                        header(HttpHeaders.AUTHORIZATION, accessToken).
                        header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                        accept(String.valueOf(MediaType.APPLICATION_JSON)).
                        body(listPrRequestBody), http().client(choreoProjectsTestClient).
                        receive().
                        response(HttpStatus.OK).
                        message().
                        type(MessageType.JSON).
                        body(new ClassPathResource("templates/createUserManagedComponent/get_pull_requests_empty.json")).
                        validate(JsonMessageValidationContext.Builder.json())));
    }

    @Test(dependsOnMethods = {"testMergeNewCode"})
    @CitrusTest
    public void testComponentRetrieval() throws JsonProcessingException {

        String requestBody = GQLutil.getComponentDetailsQuery(projectId, response.getHandler());
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

                    testComponent = ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, message.getPayload().toString(), "component");
                }));
    }

    @Test(dependsOnMethods = {"testComponentRetrieval"})
    @CitrusTest
    public void testComponentDeployment() throws GetCommitHistoryException, IOException, NoLatestCommitHashFoundException, NoLatestApiVersionFoundException, NoLatestAppEnvIdFoundException {
        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
        String latestVersionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
        String branch = testComponent.getRepository().getBranch();
        String name = testComponent.getName();

        String configurationsUpdateRequestURI = "/orgs/".
                concat(orgHandle).
                concat("/projects/").
                concat(projectId).
                concat("/components/").
                concat(response.getId()).
                concat("/envs/").concat(devEnvIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");
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
        $(http().
                client(choreoTestClient).
                send().post(configurationsUpdateRequestURI).
                message().header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(configurationsRequestBody).accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http().
                client(choreoTestClient).
                receive().response(HttpStatus.OK));


        String requestBody = GQLutil.deploy(response.getId(), latestVersionId, devEnvIdToDeploy, branch, latestCommitSha);

        // Deploy component
        $(http().
                client(choreoProjectsTestClient).
                send().post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                message().header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(requestBody).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().
                client(choreoProjectsTestClient).
                receive().response(HttpStatus.OK).
                message().type(MessageType.JSON).
                body(new ClassPathResource("templates/deploy/gql_deploy_component_success.json")).
                validate(JsonMessageValidationContext.Builder.json()));
    }

    @Test(dependsOnMethods = {"testComponentDeployment"})
    @CitrusTest
    public void testDeploymentStatusByVersion() throws Exception {
        String versionId = testComponent.getLatestApiVersion().getId();


        String requestBody = GQLutil.getDeploymentStatusByVersion(response.getId(), versionId);
        // Poll deployment status
        $(repeatOnError().until("i = 25").
                index("i").
                autoSleep(25000).
                actions(http().
                        client(choreoProjectsTestClient).
                        send().
                        post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                        message().
                        header(HttpHeaders.AUTHORIZATION, accessToken).
                        body(requestBody).
                        accept(String.valueOf(MediaType.APPLICATION_JSON)), http().
                        client(choreoProjectsTestClient).
                        receive().
                        response(HttpStatus.OK).
                        message().
                        body(new ClassPathResource("templates/deploy/deploy_status_by_version_success.json")).
                        validate(JsonMessageValidationContext.Builder.json())));

    }

    @Test(dependsOnMethods = {"testDeploymentStatusByVersion"})
    @CitrusTest
    public void testComponentDeploymentStatus() throws Exception {
        String versionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandle);
        params.put("orgUuid", orgUUID);
        params.put("componentId", response.getId());
        params.put("versionId", versionId);
        params.put("environmentId", devEnvIdToDeploy);

        String graphQlQuery = ComponentUtils.generateStringFromTemplate("templates/deploy/graphql/componentDeployment.mustache", params);
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
                .autoSleep(25000)
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



    @Test(dependsOnMethods = {"testComponentDeploymentStatus"})
    @CitrusTest
    public void testAPIInvocation() throws NoLatestApiVersionFoundException, IOException {
        String latestVersionId = testComponent.getLatestApiVersion().getId();
        String requestBody = GQLutil.getInvokeUrlInformation(orgHandle, orgUUID, response.getId(), latestVersionId);

        // Get invokeUrl and apiId
        $(http().
                client(choreoProjectsTestClient).
                send().post(Constant.GRAPHQL_ENDPOINT_SUFFIX).
                message().header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                body(requestBody).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http().
                client(choreoProjectsTestClient).
                receive().response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                validate((message, context) -> {
                    JsonObject invokeInformation = new JsonParser().parse((String) message.getPayload()).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("invokeInformation").get(0).getAsJsonObject();
                    invokeUrl = invokeInformation.get("invokeUrl").getAsString();
                    apiId = invokeInformation.get("apiId").getAsString();
                }));

        String requestURI = Constant.APIS_ENDPOINT.concat("/").concat(apiId).concat(Constant.GENERATE_KEY_ENDPOINT_SUFFIX).concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(orgUUID);

        // Generate API key for invocation
        $(http().
                client(choreoTestClientForSTS).
                send().post(requestURI).
                message().
                header(HttpHeaders.AUTHORIZATION, accessToken).
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http().
                client(choreoTestClientForSTS).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                validate((message, context) -> {
                    apiKey = new JsonParser().parse((String) message.getPayload()).getAsJsonObject().get("apikey").getAsString();
                }));

        // Test API Invocation
        // Retry on failure for few seconds since config update takes some time to effect
        String apiInvocationRequestURI = "/getJwt";

        $(repeatOnError().
                until("i = 15").
                index("i").
                autoSleep(25000).
                actions(http().
                        client(invokeUrl).
                        send().
                        get(apiInvocationRequestURI).
                        message().
                        header(HttpHeaders.ACCEPT, "text/plain").
                        header("API-Key", apiKey), http().client(invokeUrl).
                        receive().
                        response(HttpStatus.OK).
                        message().
                        type(MessageType.JSON).
                        body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
                        validate(JsonMessageValidationContext.Builder.json())));
    }


}

