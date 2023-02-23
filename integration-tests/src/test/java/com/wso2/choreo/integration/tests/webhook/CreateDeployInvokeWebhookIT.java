package com.wso2.choreo.integration.tests.webhook;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.response.Response;

import org.apache.commons.codec.binary.Hex;
import org.hamcrest.core.StringRegularExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
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

    private String orgUUID;
    private String projectId;
    private String repoName;
    private String namespace;
    private String obsId;
    private String orgId;
    private String githubOrg;
    private String githubPAT;
    private String devInvokeURL;
    private ChoreoProject project;
    private ChoreoComponent choreoComponent;
    Environment[] en;

    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoProjectsTestClient;
    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}};
    }

    @BeforeClass
    public void setup_CreateDeployInvokeWebhookIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        project  = GraphQL.createProject(accessToken);
        projectId = project.getId();

        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
    }

    @Test
    @CitrusTest
    public void createUserManagedComponent_CreateDeployInvokeWebhookIT() throws Exception {
        // Creating component
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).
                srcGitRepoUrl("https://github.com/choreo-test-apps/GitHub-web-hook").
                displayName(componentName).projectId(projectId).
                triggerChannels("IssuesService").triggerID("88").
                displayType(Constant.displayType.webhook.name()).build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertEquals(choreoComponent.getProjectId(), projectId);

    }

    @Test(dependsOnMethods = {"createUserManagedComponent_CreateDeployInvokeWebhookIT"})
    @CitrusTest
    public void componentDeployment_CreateDeployInvokeWebhookIT() throws Exception {
        BalConfig balConfigs = BalConfig.builder().isRequired(true).configKeyName("config.webhookSecret").valueType("string").valueOrSource("abcd").build();
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, balConfigs);
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDeployment_CreateDeployInvokeWebhookIT"})
    @CitrusTest
    public void invokeAPI_CreateDeployInvokeWebhookIT() throws Exception {
        String apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();

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

    @Test(dependsOnMethods = {"invokeAPI_CreateDeployInvokeWebhookIT"})
    @CitrusTest
    public void waitForObservabilityLogs_CreateDeployInvokeWebhookIT() throws Exception {

        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);

        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        String devReleaseId =  choreoComponent.getReleaseIdForEnvironment(devEnv.getChoreoEnv());

    }

    @Test(dependsOnMethods = {"waitForObservabilityLogs_CreateDeployInvokeWebhookIT"})
    @CitrusTest
    public void fetchObservabilityId_CreateDeployInvokeWebhookIT() throws Exception {

        String releaseId = choreoComponent.getLatestApiVersion().getAppEnvVersions().get(0).getReleaseId();
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

    @Test(dataProvider = "env-provider", dependsOnMethods = {"fetchObservabilityId_CreateDeployInvokeWebhookIT"})
    @CitrusTest
    public void  observabilityLogs_CreateDeployInvokeWebhookIT(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(observabilityIdInformation.getObsId())
                .concat("/logsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .queryParam("sort", "desc")
                .queryParam("limit", "95")
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns.size()", greaterThanOrEqualTo(1))
                        .expression("$.columns[*].name", hasItems("TimeGenerated", "LogLevel", "LogEntry", "LogContext"))
                        .expression("$.columns[*].type", hasItems("datetime", "string", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThanOrEqualTo(1))
                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
                )
        );
    }

    @Test(dependsOnMethods = {"observabilityLogs_CreateDeployInvokeWebhookIT"}, alwaysRun = true)
    @CitrusTest
    public void deleteWebhookComponent_CreateDeployInvokeWebhookIT() throws Exception {
        Response res = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }
}
