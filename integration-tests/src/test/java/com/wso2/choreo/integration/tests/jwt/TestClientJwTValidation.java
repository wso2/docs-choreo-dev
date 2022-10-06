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
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.responses.CreateComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.common.utils.GQLutil;
import com.wso2.choreo.integration.common.utils.GitUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
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
//        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
//        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
//        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
//        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
//        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
//        githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
//        ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
//        ChoreoProject project = org.createProject(accessToken);
//        projectId = project.getId();


        orgHandle = "dasunatwso2com";
        orgId = "869";
        orgUUID = "fec0832e-94dd-4749-aa0f-7da5ed9e0a31";
        githubOrg = "dasunshakhya2";
        githubPAT = "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";
        projectId = "477419e5-43c4-490c-b48a-1dc8086ccd7b";
        accessToken = "Bearer eyJ4NXQiOiJNbUV5WlRSaFpHTTROamc1WW1SbU9XVXlOalkxT1dReVpURXlNREJoTXpVd01ESTFOak5pWlRkalptWXhZMlkzWWpCaU4ySTRaRFppTW1Jek5qYzJPUSIsImtpZCI6Ik1tRXlaVFJoWkdNNE5qZzVZbVJtT1dVeU5qWTFPV1F5WlRFeU1EQmhNelV3TURJMU5qTmlaVGRqWm1ZeFkyWTNZakJpTjJJNFpEWmlNbUl6TmpjMk9RX1JTMjU2IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIxYzY5MjMwNi1iOGIwLTQ1ODUtYjNjYy1lOGVmZjQ4ODk5YTEiLCJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiaXNzIjoiaHR0cHM6XC9cL3N0cy5wcmV2aWV3LWR2LmNob3Jlby5kZXY6NDQzXC9vYXV0aDJcL3Rva2VuIiwiYXVkIjpbIld4cXkwbGlDZkxCc2RwWE9oa2N4Wno2dUxQa2EiLCJodHRwczpcL1wvc3RzLnByZXZpZXctZHYuY2hvcmVvLmRldjo0NDNcL29hdXRoMlwvdG9rZW4iXSwibmJmIjoxNjY1MDI1NjMwLCJhenAiOiJXeHF5MGxpQ2ZMQnNkcFhPaGtjeFp6NnVMUGthIiwic2NvcGUiOiJhcGltOmFkbWluIGFwaW06YXBpX21hbmFnZSBhcGltOmFwaV9wdWJsaXNoIGFwaW06YXBpX3NldHRpbmdzIGFwaW06ZGNyOmFwcF9tYW5hZ2UgYXBpbTpkb2N1bWVudF9tYW5hZ2UgYXBpbTpwdWJsaXNoZXJfc2V0dGluZ3MgYXBpbTpzdWJzY3JpcHRpb25fbWFuYWdlIGFwaW06c3Vic2NyaXB0aW9uX3ZpZXcgYXBpbTp0aWVyX21hbmFnZSBjaG9yZW86Y29tcG9uZW50X21hbmFnZSBjaG9yZW86ZGVwbG95bWVudF9tYW5hZ2UgY2hvcmVvOmRldl9lbnZfbWFuYWdlIGNob3Jlbzpwcm9kX2Vudl9tYW5hZ2UgY2hvcmVvOnByb2plY3RfbWFuYWdlIGNob3Jlbzpyb2xlX21hbmFnZSBjaG9yZW86dXNlcl9tYW5hZ2UgZW52aXJvbm1lbnRzOnZpZXdfZGV2IGVudmlyb25tZW50czp2aWV3X3Byb2QiLCJvcmdhbml6YXRpb24iOnsiaGFuZGxlIjoiZGFzdW5hdHdzbzJjb20iLCJ1dWlkIjoiZmVjMDgzMmUtOTRkZC00NzQ5LWFhMGYtN2RhNWVkOWUwYTMxIn0sIm9yZ2FuaXphdGlvbnMiOlsiZmVjMDgzMmUtOTRkZC00NzQ5LWFhMGYtN2RhNWVkOWUwYTMxIiwiYzI5Y2Y2M2UtZTViYy00ODhlLTk5OGEtODE4NjZkMmIyNjZiIiwiYWJmNjRjM2ItMjU4ZC00NzQ2LTgyNzktZDZjNWY2M2VhNTU4Il0sImV4cCI6MTY2NTAyOTIzMCwiaWRwX2NsYWltcyI6eyJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiYXV0aGVudGljYXRlZF9pZHAiOiJHb29nbGUiLCJuYW1lIjoiRGFzdW4gU2FtYXJhc2luZ2hlIiwiZ2l2ZW5fbmFtZSI6IkRhc3VuIiwiZmFtaWx5X25hbWUiOiJTYW1hcmFzaW5naGUiLCJlbWFpbCI6ImRhc3VuQHdzbzIuY29tIn0sImlhdCI6MTY2NTAyNTYzMCwianRpIjoiMDI4NDJjYzAtZDM1Mi00YzY2LWJmNjctZmEwODViNGJlYjdiIn0.AgS3BWeyzF0BdlLxModCOfR7HrOPP8CI_-QXVf3HQni6TkXQsDT4OhHcQlaV_dWyR2euJ-kbL8Gqh9h2_wUvduPCWQp7GxOPzIcH4xtp8EOuHbb2h9oew8HKoQswCvzOuldUuoX_oqAZiUY_flfIQvjzcKbVvmo44l_1dQ1VcZqqfKYKHSkTaT8DUpUN79bY_M-j4HTg_uvYzuhfox9W2ghuUchaoWLBCwOahQjrJlhZ3QCzFxI__MNyZ9Xt2wEIlZxXUhgLl9q4oCvtZ0O6sZkabO4rIOJgPcL5NqExszkq2yD5mG7G4tHHch698DCcmj1_Asfoj6J4uxscg-37vD1cKiarpafK5xNDppT85GIXCq5dX0QLynePpA1Pb4m8WJp0grolmP3PKDq6YBSmVE6H5ujuTtvYK_yNBdeYXOD1Zv3rgfVxdAMGXaeDlODkys1v68x-lQmE-B-9cDtEpxZxostkHACr6kgWnKmxdanpQPEqDx5TXFsrvAuTv4doYOwRwiVwnmqlavy1f4Spc25o-jDYXf6WQ48a4RcwTDWMGTHbuiuZJcn65F_IBZEab9_erwmHjURohI_gV0ZZPNSUlus49Ddv1yBztoezqX1ZhBeUHiz33UlapMom1ssx21EFX1-wJmtmzJode6lERTROVX1B-jZxlfnHwVOEJxw";


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

                    System.out.println(message.getPayload().toString());
                    JsonObject component = new JsonParser().
                            parse((String) message.getPayload()).
                            getAsJsonObject().
                            getAsJsonObject("data").
                            getAsJsonObject("createComponent");

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

