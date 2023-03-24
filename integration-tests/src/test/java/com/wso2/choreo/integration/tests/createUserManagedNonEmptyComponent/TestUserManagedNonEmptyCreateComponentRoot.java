package com.wso2.choreo.integration.tests.createUserManagedNonEmptyComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * $(http()
 * tests related to component creation from user managed non empty repo root.
 */
public class TestUserManagedNonEmptyCreateComponentRoot extends TestNGCitrusSpringSupport {
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
        private final String repoName = "byor-greetings-app1";
        private String prBranch;
        private String githubOrg;
        private String githubPAT;
        private static ChoreoComponent testComponent;
        private static ChoreoComponent testComponentV2;
        private String repoType = "UserManagedNonEmpty";
        private String repoBranch = "dev";
        private String repoBranchV2 = "dev-v2";
        private ApiVersion apiVersion;

        private String revisionUUID;
        private int revisionId;

        private String buildId;
        @Autowired
        private HttpClient choreoTestClient;

        @Autowired
        private HttpClient choreoProjectsTestClient;

        @Autowired
        private HttpClient choreoTestClientForGithub;

        @Autowired
        private HttpClient choreoTestClientForSTS;

        @BeforeClass
        public void setup_TestUserManagedNonEmptyCreateComponentRoot()
                throws Exception {

                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
                orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
                orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
                githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);


                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                ChoreoProject project = GraphQL.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void createUserManagedComponent_TestUserManagedNonEmptyCreateComponentRoot() throws  IOException {

                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
                String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/")
                        .concat(repoName);
                APICreator testAPI = new APICreator();
                String repoSubpath = "";
                String componentRequestBody = testAPI.createUserManagedNonEmptyComponentCreationQuery(
                        componentName, orgId, orgHandle, projectId, srcGitHubURL, repoSubpath, repoType, repoBranch);

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

        @Test(dependsOnMethods = { "createUserManagedComponent_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void createdComponentStatus_TestUserManagedNonEmptyCreateComponentRoot() {
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

        @Test(dependsOnMethods = { "createdComponentStatus_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void componentRetrieval_TestUserManagedNonEmptyCreateComponentRoot() throws IOException {
                APICreator testAPI = new APICreator();
                String requestBody = testAPI.getComponentDetailsQuery(projectId, componentHandler);

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
                                apiVersion = testComponent.getApiVersions().stream().filter(v -> v.isLatest()).findFirst().get();
                        }));
        }

        @Test(dependsOnMethods = {"componentRetrieval_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void createNewVersion_TestUserManagedNonEmptyCreateComponentRoot() throws IOException {
                // Creating new branch
                GitHub.createNewBranch(githubOrg,repoName,repoBranch,repoBranchV2);

                APICreator testAPI = new APICreator();

                String newVersionCreatorQuery = testAPI.createNewComponentVersionQuery(orgHandle, orgUUID, componentId,
                        "restAPI",apiVersion.getId(),repoBranchV2);

                $(http()
                        .client(choreoProjectsTestClient)
                        .send()
                        .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(newVersionCreatorQuery)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                        .client(choreoProjectsTestClient)
                        .receive()
                        .response(HttpStatus.OK));
        }

        @Test(dependsOnMethods = { "createNewVersion_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void createdComponentVersionStatus_TestUserManagedNonEmptyCreateComponentRoot() {
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

        @Test(dependsOnMethods = { "createdComponentVersionStatus_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void componentDeployment_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                // Retrieve the latest component.
                testComponentV2 = GraphQL.getComponentDetails(projectId,componentHandler, accessToken);

                JsonArray commitHistory = testComponentV2.getCommitHistory(accessToken);
                String latestCommitSha = testComponentV2.getLatestCommitHash(commitHistory);
                String latestVersionId = testComponentV2.getLatestApiVersion().getId();

                String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");

                String branch = testComponentV2.getRepository().getBranch(); // todokeshi check if branch correct
                String name = testComponentV2.getName();

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

        @Test(dependsOnMethods = { "componentDeployment_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void deploymentStatusByVersion_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                String versionId = testComponentV2.getLatestApiVersion().getId();

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

        @Test(dependsOnMethods = { "deploymentStatusByVersion_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void componentDeploymentStatus_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                String versionId = testComponentV2.getLatestApiVersion().getId();
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

                JsonArray commitHistory = testComponentV2.getCommitHistory(accessToken);
                String latestCommitSha = testComponentV2.getLatestCommitHash(commitHistory);

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
                                        .body(expectedResponse)
                                        .extract((message, context) -> {
                                        JsonObject component = new JsonParser().parse((String) message.getPayload())
                                                .getAsJsonObject();
                                        this.buildId = component
                                                .get("data").getAsJsonObject()
                                                .get("componentDeployment").getAsJsonObject()
                                                .get("build").getAsJsonObject()
                                                .get("buildId").getAsString();
                                })));
        }

        @Test(dependsOnMethods = {"componentDeploymentStatus_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void componentPromotionToProd_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                // Retrieve the latest component.
                testComponentV2 = GraphQL.getComponentDetails(projectId,componentHandler, accessToken);

                Commit[] commitHistory = GraphQL.getCommitHistoryBranch(testComponent.getId(), repoBranch, accessToken);
                Orgs.addConfigurationForNewVersion(choreoTestClient, this, testComponentV2, commitHistory, Constant.PROD_ENVIRONMENT, testComponent);
                testComponentV2.promoteNewVersion(accessToken,
                        testComponentV2.getReleaseIdForEnvironmentV2(Constant.DEV_ENVIRONMENT),
                        testComponent.getAppEnvIdForVersion(testComponent.getApiVersions().get(0),Constant.PROD_ENVIRONMENT));
        }

        @Test(dependsOnMethods = { "componentPromotionToProd_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void invokeAPIDev_TestUserManagedNonEmptyCreateComponentRoot() throws NoLatestApiVersionFoundException, IOException {

                String latestVersionId = testComponentV2.getLatestApiVersion().getId();
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

        @Test(dependsOnMethods = {"invokeAPIDev_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void addPromoteConfiguration_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                //Retrieve latest component.
                testComponentV2 = GraphQL.getComponentDetails(projectId, componentHandler, accessToken);

                List<Commit> commitHistory = GraphQL.getCommitHistory(this, choreoTestClient,
                        testComponentV2.getId(), accessToken);
                Orgs.addConfiguration(this, choreoTestClient, testComponentV2, commitHistory,
                        Constant.PROD_ENVIRONMENT);
        }

        @Test(dependsOnMethods = {"addPromoteConfiguration_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void promote_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                GraphQL.promoteComponent(testComponentV2, accessToken);
        }

        @Test(dependsOnMethods = {"promote_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void componentProdDeploymentStatus_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                GraphQL.componentDeployment(testComponentV2, "prod", accessToken);
        }

        @Test(dependsOnMethods = {"componentProdDeploymentStatus_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void invokeAPIProd_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                ComponentUtils.invokeApiEndpoint(accessToken, testComponentV2, Constant.Environment.Production);
        }

        @Test(dependsOnMethods = {"invokeAPIProd_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void ComponentDeploymentAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot()
                throws Exception {
                ProxyAPI proxyAPI = APICreator.getProxyAPI(testComponentV2.getApiId(), orgUUID, accessToken).getEntity();
                String apiYamlFilename = "templates/graphql/requests/restAPIV2UpdateAPIWithRateLimit.mustache";
                Map<String, String> apiYamlParams = new HashMap<>();
                apiYamlParams.put("apiId", proxyAPI.getId());
                apiYamlParams.put("apiName", proxyAPI.getName());
                apiYamlParams.put("basePath", proxyAPI.getContext() + "/2.0.0");
                apiYamlParams.put("revisionId", String.valueOf(revisionId));
                String apiPayload = ObjectMapperUtil.mapObjectToString(apiYamlFilename, apiYamlParams);

                JsonArray jsonArray = testComponentV2.getRevisions(accessToken, apiId, orgUUID);
                for (JsonElement jsonElement : jsonArray) {
                        if (jsonElement.getAsJsonObject().has("deploymentInfo")) {
                                JsonArray deploymentInfo = jsonElement.getAsJsonObject()
                                        .get("deploymentInfo").getAsJsonArray();
                                if (deploymentInfo.size() > 0) {
                                        JsonObject deploymentInfoObject = deploymentInfo.get(0).getAsJsonObject();
                                        if (deploymentInfoObject.has("name") &&
                                                "dev-us-east-azure".equals(deploymentInfoObject.get("name")
                                                        .getAsString())) {
                                                this.revisionUUID =
                                                        deploymentInfoObject.get("revisionUuid").getAsString();
                                                this.revisionId =
                                                        Integer.parseInt(jsonElement.getAsJsonObject()
                                                                .get("displayName").getAsString()
                                                                .split(" ")[1]);
                                        }
                                }
                        }
                }
                Assert.assertNotNull(this.revisionUUID, "Revision ID is null");
                DeploySettings deploySettings = APICreator.deployRevision(testComponentV2.getId(),
                        testComponentV2.getLatestApiVersion().getId(),
                        testComponentV2.getLatestAppEnvId("dev"), orgUUID,
                        revisionUUID, buildId, apiId, accessToken, apiPayload);
                boolean requestSuccess = false;
                DeploymentStatus deploymentStatus = null;
                int count = 0;
                while (!requestSuccess && count < 10) {
                        deploymentStatus = APICreator.checkDeploymentStatus(componentId,
                                testComponentV2.getLatestApiVersion().getId(),
                                deploySettings.getRequestId(), accessToken);
                        if ("completed".equals(deploymentStatus.getStatus())) {
                                requestSuccess = true;
                        } else {
                                Thread.sleep(2000);
                                count++;
                        }
                }
                Assert.assertTrue(requestSuccess, "Deployment request failed : Current Action : " +
                        deploymentStatus.getCurrent_Action() + " Status : " + deploymentStatus.getStatus());
        }

        @Test(dependsOnMethods = {"ComponentDeploymentAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void invokeRestAPIAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot()
                throws Exception {
                // To give a time to deploy the API.
                Thread.sleep(10000);
                // Rate limiting counter resets based on the system clock.
                long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
                if (timeRemainingTillNextMinute < 15000) {
                        Thread.sleep(timeRemainingTillNextMinute + 5000);
                }
                boolean isThrottled = false;
                try {
                        ComponentUtils.invokeApiEndpointMultipleTimes(accessToken, testComponentV2, Constant.Environment.Development,
                                15);
                } catch (InvokeAPICheckException e) {
                        isThrottled = true;
                }

                Assert.assertTrue(isThrottled, "Requests are not rate limited at the desired limit");
                timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
                Thread.sleep(timeRemainingTillNextMinute + 5000);
                try {
                        ComponentUtils.invokeApiEndpoint(accessToken, testComponentV2, Constant.Environment.Production);
                } catch (Exception e) {
                        Assert.fail("Unexpected error occured while invoking API : " + e.getMessage());
                }
        }

        @Test(dependsOnMethods = { "invokeAPIProd_TestUserManagedNonEmptyCreateComponentRoot" ,
                "invokeRestAPIAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void deleteBranchV2_TestUserManagedNonEmptyCreateComponentRoot() throws JsonProcessingException {
                String authHeader = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(githubPAT);

                // delete merged PR branch
                String deleteRequestURI = "/repos/".concat(githubOrg).concat("/").concat(repoName)
                        .concat("/git/refs/heads/" + repoBranchV2);

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
}
