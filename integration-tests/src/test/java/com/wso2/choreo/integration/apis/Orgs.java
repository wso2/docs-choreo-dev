/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.apis;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.configmapping.Config;
import com.wso2.choreo.integration.models.configmapping.ConfigMapping;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.orgs.PromoteConfigurations;
import com.wso2.choreo.integration.models.response.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements Orgs API calls and their response validations.
 */
@Log4j2
public class Orgs extends ControlPlaneAPI {

    public static Response addConfiguration(ChoreoComponent component, String envName, String accessToken, BalConfig... balconfigs) throws Exception {
        String componentId = component.getId();
        String envIdToDeploy = component.getLatestAppEnvId(envName);
        String latestVersionId = component.getLatestApiVersion().getId();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configurationsUpdateRequestURI = CHOREO_EP + "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");
        PromoteConfigurations promoteConfigurations = PromoteConfigurations.builder().configs(balconfigs).sourceUuid("").
                commitHash(latestCommitSha).moduleName(component.getName()).operation(0).applyNow(false).build();
        String configurationsRequestBody = ObjectMapperUtil.mapObjectToString(promoteConfigurations).replace("required", "isRequired");
        log.info(configurationsUpdateRequestURI);
        return HttpClientUtil.httpPOST(configurationsUpdateRequestURI, configurationsRequestBody, accessToken, "");

    }

    public static void getConfigurationMapping(ChoreoComponent component, String accessToken) throws Exception {
        String componentId = component.getId();
        String latestVersionId = component.getLatestApiVersion().getId();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();
        String configurationsUpdateRequestURI = CHOREO_EP + "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/versions/")
                .concat(latestVersionId).concat("/commits/")
                .concat(latestCommitSha).concat("/configurable-commit-mapping");

        log.info(configurationsUpdateRequestURI);


        for (int i = 0; i < 20; i++) {
            Response response = HttpClientUtil.httpGET(configurationsUpdateRequestURI, accessToken, "");
            log.info(Integer.toString(response.getStatusCode()));
            log.info(response.getRes());
            ConfigMapping configMapping = ObjectMapperUtil.mapStringToObject(ConfigMapping.class, response.getRes(), "");
            if (response.getStatusCode() != 200) {
                Config config = Config.builder().sha(latestCommitSha).versionId(latestVersionId).branch("main").componentId(componentId).build();
                String configPayload = ObjectMapperUtil.mapObjectToString(config);
                String configGenerationUrl = CHOREO_EP + "/orgs/".concat(orgHandle).concat("/projects/")+projectId + "/triggers/configurable-generation";
                Response configRes = HttpClientUtil.httpPOST(configGenerationUrl, configPayload, accessToken, "");
            }
            if (configMapping.isSuccess() && configMapping.getData().getWorkflowStatus().equals("completed") && configMapping.getData().getRunStatus() != null) {
                break;

            } else {
                SleepUtil.sleep(30);
            }
        }

    }

    public static void addConfigurationForNewVersion(HttpClient client, TestActionRunner runner,
            ChoreoComponent component, String latestCommitSha, String envIdToDeploy,
            BalConfig... balconfigs) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = component.getId();
        String latestVersionId = component.getLatestApiVersion().getId();

        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");

        Map<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("moduleName", component.getName());
                put("commitHash", latestCommitSha);
                put("applyNow", false);
                put("operation", 0);
                put("sourceUuid", "");
                put("configs", balconfigs);
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap).replace("required", "isRequired");

        // Update configurations
        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(configurationsUpdateRequestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(configurationsRequestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));
    }

    public static void addConfiguration(TestActionRunner runner, HttpClient client,
                                        ChoreoComponent component, List<Commit> commitHistory, Environment environment,
                                        BalConfig... balconfigs) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = component.getId();
        String envIdToDeploy = environment.getId();
        String latestVersionId = component.getLatestApiVersion().getId();
        String latestCommitSha = component.getLatestCommitHash(commitHistory.toArray(Commit[]::new));
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");

        Map<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("moduleName", component.getName());
                put("commitHash", latestCommitSha);
                put("applyNow", false);
                put("operation", 0);
                put("sourceUuid", "");
                put("configs", balconfigs);
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap).replace("required", "isRequired");

        // Update configurations
        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(configurationsUpdateRequestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(configurationsRequestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));
    }

    public static void addConfigurationForNewVersion(HttpClient client, TestActionRunner runner,
            ChoreoComponent componentV2, Commit[] commitHistory, String envName, ChoreoComponent componentV1,
            BalConfig... balconfigs) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = componentV2.getId();
        String envIdToDeploy = componentV1.getAppEnvIdForVersion(componentV1.getApiVersions().get(0),envName);
        String latestVersionId = componentV2.getLatestApiVersion().getId();
        String latestCommitSha = componentV1.getLatestCommitHash(commitHistory);
        String orgHandle = componentV2.getOrgHandler();
        String projectId = componentV2.getProjectId();

        String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");

        Map<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("moduleName", componentV2.getName());
                put("commitHash", latestCommitSha);
                put("applyNow", false);
                put("operation", 0);
                put("sourceUuid", "");
                put("configs", balconfigs);
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap).replace("required", "isRequired");

        // Update configurations
        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(configurationsUpdateRequestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(configurationsRequestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));
    }

    public static Status createdComponentStatus(String projectId, String componentId, String accessToken) throws UnexpectedResponseException {
        String url = CHOREO_EP + "/orgs/" + ORG_HANDLE + "/projects/" + projectId + "/components/" + componentId + "/init/status";
        Response res = null;

        for (int i = 0; i < 25; i++) {
            res = HttpClientUtil.httpGET(url, accessToken, "");
            Status status = ObjectMapperUtil.mapStringToObject(Status.class, res.getRes(), "");
            if (status.isSuccess()) {
                return status;
            }
            SleepUtil.sleep(5);
        }
        throw new UnexpectedResponseException(res.getStatusCode(), "Expected Status as " + true + " but found " + false);
    }


    public static void waitForComponentCreationSuccess(TestActionRunner runner, HttpClient client, String accessToken,
                                                       String projectId,
                                                       String componentId) {
        runner.$(repeatOnError()
                .until("i = 50")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get("/orgs/"
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
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/createComponent/get_create_status_success.json"))
                                .validate(json()
                                        .ignore("$.message"))));
    }

    public static String getDeploymentLogs(String accessToken, String choreoOrgHandle, String projectId,
            String componentId, String runId) {
        String choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        String requestURI = choreoEndpoint.concat(
                "/orgs/" + choreoOrgHandle + "/projects/" + projectId + "/components/" + componentId + "/runs/" + runId + "/logs");

        HttpGet request = new HttpGet(requestURI);
        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, accessToken);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == org.apache.http.HttpStatus.SC_OK) {
                return responseBody;
            }
            throw new RuntimeException("Invalid response code received: " + statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void triggerConfigurableGeneration(TestActionRunner runner, HttpClient client,
                    ChoreoComponent component, List<Commit> commitHistory, String branchName) throws Exception {
            String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
            String componentId = component.getId();
            String latestVersionId = component.getLatestApiVersion().getId();
            String latestCommitSha = component.getLatestCommitHash(commitHistory.toArray(Commit[]::new));
            String orgHandle = component.getOrgHandler();
            String projectId = component.getProjectId();

            String configGenerationTriggerURI = "/orgs/".concat(orgHandle).concat("/projects/")
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
                                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                        .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                        .body(configurationsRequestBody),
                                http()
                                        .client(client)
                                        .receive()
                                        .response(HttpStatus.OK)));
    }
}
