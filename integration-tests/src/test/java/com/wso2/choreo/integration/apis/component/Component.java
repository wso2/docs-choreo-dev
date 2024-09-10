/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.apis.component;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.consol.citrus.validation.json.JsonTextMessageValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.container.RepeatUntilTrue.Builder.repeat;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

@Log4j2
public class Component extends ControlPlaneAPI {
    private static final String CONTEXT = "/component-mgt/1.0.0";
    private static final String MANAGED_AUTH_ENABLE_LOCAL_DEVELOPMENT_ENDPOINT = "/managed-auth/local-development";
    private static final String COMPONENT_CREATION_STATUS_ENDPOINT = "/component-creation/v1";

    public static void triggerConfigurableGeneration(TestActionRunner runner, HttpClient client,
            ChoreoComponent component, List<Commit> commitHistory, String branchName) throws Exception {
        String componentId = component.getId();
        String latestVersionId = component.getLatestApiVersion().getId();
        String latestCommitSha = component.getLatestCommitHash(commitHistory.toArray(Commit[]::new));
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configGenerationTriggerURI = CONTEXT.concat("/orgs/").concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/triggers/").concat("configurable-generation");
        Map<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("componentId", componentId);
                put("versionId", latestVersionId);
                put("branch", branchName);
                put("sha", latestCommitSha);
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap).replace("required",
                "isRequired");
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(configGenerationTriggerURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(configurationsRequestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));
    }

    public static void waitForAsyncComponentCreationSuccess(TestNGCitrusSpringSupport runner, HttpClient client,
                                                            String accessToken, String componentId) {
        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(COMPONENT_CREATION_STATUS_ENDPOINT.concat("/operation-status?ids=")
                                        .concat(componentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource("templates/createComponent/async_component_create_status.json"))
                                .validate(json()
                                )));
    }

    public static void waitForComponentCreationSuccess(TestNGCitrusSpringSupport runner, HttpClient client,
            String accessToken,
            String projectId,
            String componentId) throws IOException {
        String expectedResponse = ComponentUtils.generateStringFromTemplate(
                "templates/createComponent/get_create_status_success.json", null);
        runner.variable("isComponentCreationSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 50) or ( ${isComponentCreationSuccess} = true )")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(CONTEXT.concat("/orgs/")
                                        .concat(ORG_HANDLE)
                                        .concat("/projects/")
                                        .concat(projectId)
                                        .concat("/components/")
                                        .concat(componentId)
                                        .concat("/init/status"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code == HttpStatus.OK.value()) {
                                        JsonTextMessageValidator validator = new JsonTextMessageValidator();
                                        Message expected = new DefaultMessage(expectedResponse);
                                        validator.validateMessage(message, expected, context,
                                                new JsonMessageValidationContext());
                                        context.setVariable("isComponentCreationSuccess", true);
                                    }
                                })));
    }

    public static JsonArray getDeploymentBuildSteps(TestActionRunner runner, HttpClient client, String accessToken,
            String projectId,
            String componentId, String runId) {
        AtomicReference<JsonArray> steps = new AtomicReference<>(new JsonArray());
        runner.$(repeatOnError()
                .until("i = 10")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(CONTEXT.concat("/orgs/")
                                        .concat(ORG_HANDLE)
                                        .concat("/projects/")
                                        .concat(projectId)
                                        .concat("/components/")
                                        .concat(componentId)
                                        .concat("/runs/")
                                        .concat(runId)
                                        .concat("/logs"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    String payload = message.getPayload(String.class);
                                    JsonObject dataJsonObject = new JsonParser().parse(payload).getAsJsonObject()
                                            .getAsJsonObject("data");
                                    steps.set(dataJsonObject.getAsJsonObject("build").getAsJsonArray("steps"));
                                })));

        return steps.get();

    }

    public static String waitForComponentBuildSuccess(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                    String projectId,
                                                    String componentId, String runId, String name) {
        runner.variable("isComponentBuildCompleted", false);
        AtomicReference<JsonArray> steps = new AtomicReference<>(new JsonArray());
        AtomicReference<String> conclusion = new AtomicReference<>("");
        AtomicReference<String> status = new AtomicReference<>("");
        runner.$(repeat()
                .until("(i = 20) or ( ${isComponentBuildCompleted} = true )")
                .index("i")
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(CONTEXT.concat("/orgs/")
                                        .concat(ORG_HANDLE)
                                        .concat("/projects/")
                                        .concat(projectId)
                                        .concat("/components/")
                                        .concat(componentId)
                                        .concat("/runs/")
                                        .concat(runId)
                                        .concat("/logs"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    String payload = message.getPayload(String.class);
                                    JsonObject dataJsonObject = new JsonParser().parse(payload).getAsJsonObject()
                                            .getAsJsonObject("data");
                                    steps.set(dataJsonObject.getAsJsonObject("build").getAsJsonArray("steps"));
                                    for (JsonElement element : steps.get()) {
                                        JsonObject jsonObject = element.getAsJsonObject();
                                        String stepName = jsonObject.get("name").getAsString();
                                        if (name.equalsIgnoreCase(stepName)) {
                                            status.set(jsonObject.get("status").getAsString());
                                            if ("completed".equals(status.get())) {
                                                if (!jsonObject.get("conclusion").isJsonNull()) {
                                                    context.setVariable("isComponentBuildCompleted", true);
                                                    conclusion.set(jsonObject.get("conclusion").getAsString());
                                                }
                                            }
                                            SleepUtil.sleep(30);
                                        }
                                    }
                                })));
        if (!"completed".equals(status.get())) {
            throw new RuntimeException("Component build not completed.");
        } else {
            return conclusion.get();
        }
    }

    public static String waitForComponentBuildDeployComplete(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                      String projectId,
                                                      String componentId, String runId, int sleepInterval) {
        runner.variable("isComponentBuildDeployCompleted", false);
        AtomicReference<JsonArray> steps = new AtomicReference<>(new JsonArray());
        AtomicReference<String> deployStatus = new AtomicReference<>("");
        runner.$(repeat()
                .until("(i = 10) or ( ${isComponentBuildDeployCompleted} = true )")
                .index("i")
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(CONTEXT.concat("/orgs/")
                                        .concat(ORG_HANDLE)
                                        .concat("/projects/")
                                        .concat(projectId)
                                        .concat("/components/")
                                        .concat(componentId)
                                        .concat("/runs/")
                                        .concat(runId)
                                        .concat("/logs"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    String payload = message.getPayload(String.class);
                                    JsonObject dataJsonObject = new JsonParser().parse(payload).getAsJsonObject()
                                            .getAsJsonObject("data");
                                    if (!dataJsonObject.get("deploy").isJsonNull() && !dataJsonObject.getAsJsonObject("deploy").get("status").isJsonNull()) {
                                        deployStatus.set(dataJsonObject.getAsJsonObject("deploy").get("status").getAsString());
                                        if ("completed".equals(deployStatus.get())) {
                                            context.setVariable("isComponentBuildDeployCompleted", true);
                                        }
                                    }
                                    SleepUtil.sleep(sleepInterval);
                                })));
        if (!"completed".equals(deployStatus.get())) {
            throw new RuntimeException("Component build and deploy not completed.");
        } else {
            return deployStatus.get();
        }
    }

    public static KeyGenResponseDTO generateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keyGenRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(keyGenRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getKeyGenURL(projectId, componentId, environmentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyGenResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyGenResponseDTO.class);
                                        if (response.getClientId() == null || response.getClientSecret() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyGenResponseDTO.class);
    }

    public static KeyGenResponseDTO regenerateKeysets(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, String oAuthAppId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        String url = getKeyRegenerateURL(projectId, componentId, environmentId, oAuthAppId);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("project_id", projectId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyGenResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyGenResponseDTO.class);
                                        if (response.getClientId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyGenResponseDTO.class);
    }

    public static void addExternalIdpKeys(TestActionRunner runner, HttpClient client,
                    String projectId, String componentId, String environmentId,
                    HashMap<String, Object> keyMappingRequest, HttpStatus expectedStatus)
                    throws TokenRetrievalException, IOException, URISyntaxException {

            String requestBody = ObjectMapperUtil.mapToString(keyMappingRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getKeyMappingEndpointURL(projectId, componentId, environmentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(expectedStatus)
                                .message()));
    }

    public static void configureLocalDevelopmentForManagedAuthentication(TestActionRunner runner, HttpClient client,
                    String projectId, String componentId, String releaseId,
                    HashMap<String, Object> localDevelopmentConfigureRequest, HttpStatus expectedStatus)
                    throws TokenRetrievalException, IOException, URISyntaxException {

            String requestBody = ObjectMapperUtil.mapToString(localDevelopmentConfigureRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getToggleLocalDevelopmentURL(projectId, componentId, releaseId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(expectedStatus)
                                .message()));
    }

    private static String getKeyGenURL(String projectId, String componentId, String environmentId) {

        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/generate";
    }

    private static String getKeyRegenerateURL(String projectId, String componentId, String environmentId,
            String oAuthAppId) {

        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/" + oAuthAppId;
    }

    private static String getKeyMappingEndpointURL(String projectId, String componentId, String environmentId) {

        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/map";
    }

    private static String getKeyManagerCommonURL(String projectId, String componentId, String environmentId) {

        return CONTEXT + "/orgs/" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE)
                + "/projects/" + projectId + "/components/" + componentId + "/environments/" + environmentId
                + "/key-sets";
    }

    private static String getToggleLocalDevelopmentURL(String projectId, String componentId, String releaseId) {

        return CONTEXT + "/orgs/" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE)
                + "/projects/" + projectId + "/components/" + componentId + "/releases/" + releaseId
                + MANAGED_AUTH_ENABLE_LOCAL_DEVELOPMENT_ENDPOINT;        
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }
}
