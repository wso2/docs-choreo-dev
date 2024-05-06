package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.webhook.Trigger;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

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
public class TestWebhookDp extends TestBase {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;


    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_CreateDeployInvokeWebhook() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedComponent_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        // Creating component
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());

        Repository repo = Repository.builder().
                repoUrl("https://github.com/choreo-test-apps/GitHub-web-hook").
                branch("main").
                subPath("").build();

        Trigger trigger = Trigger.builder().channels("IssuesService").id("88").build();

        GraphqlDTO dto = ComponentUtils.createWebhookComponentRequest(componentName, project, repo, trigger);
        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);

        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createUserManagedComponent_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeployment_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        BalConfig balConfigs = BalConfig.builder().isRequired(true).configKeyName("config.webhookSecret").valueType("string").valueOrSource("abcd").build();
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD, balConfigs);
        dp.setDeploymentStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"componentDeployment_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void promote_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD, dp.getChoreoProject());
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promote_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPI_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        String apiKey = APICreator.getAPIKey(dp.getChoreoComponent().getApiId(), accessToken).getApikey();

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
                                .client(dp.getDeploymentStatusDTO().getInvokeUrl())
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
                                .client(dp.getDeploymentStatusDTO().getInvokeUrl())
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.PLAINTEXT)));
    }

    @Test(dependsOnMethods = {"invokeAPI_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void waitForObservabilityLogs_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {

        dp.updateEnvironments(ComponentUtils.getEnvironments(this, citrusClients, accessToken, dp.getChoreoComponent()));
    }


    @Test(dependsOnMethods = {"waitForObservabilityLogs_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void testLiveLogs_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
            ComponentUtils.verifyComponentLevelDPLogsLive(this,citrusClients, accessToken,dp.getChoreoProject(), dp.getChoreoComponent(),env);
        }
    }

    @Test(dependsOnMethods = {"testLiveLogs_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void testGroupedLogs_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        for (Environment env : dp.getEnvironments()) {
           ComponentUtils.verifyComponentLevelDPLogsLive(this,citrusClients, accessToken,dp.getChoreoProject(), dp.getChoreoComponent(),env);
        }
    }

    @Test(dependsOnMethods = {"testGroupedLogs_CreateDeployInvokeWebhook"}, dataProvider = "dps")
    @CitrusTest
    public void deleteWebhookComponent_CreateDeployInvokeWebhook(DataProviderWrapper dp) throws Exception {
        Response res = GraphQL.deleteComponent(dp.getChoreoComponent().getId(), dp.getChoreoProject().getId(), accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }
}
