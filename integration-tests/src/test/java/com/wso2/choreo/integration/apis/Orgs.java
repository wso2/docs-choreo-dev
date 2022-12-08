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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.orgs.PromoteConfigurations;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Implements Orgs API calls and their response validations.
 */
@Slf4j
public class Orgs extends ControlPlaneAPI{



    public static Response addConfiguration(ChoreoComponent component, String envName, String accessToken, BalConfig... balconfigs) throws Exception {
        String componentId = component.getId();
        String envIdToDeploy = component.getLatestAppEnvId(envName);
        String latestVersionId = component.getLatestApiVersion().getId();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configurationsUpdateRequestURI =CHOREO_EP+ "/orgs/".concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");
        PromoteConfigurations promoteConfigurations = PromoteConfigurations.builder().configs(balconfigs).sourceUuid("").
                commitHash(latestCommitSha).moduleName(component.getName()).operation(0).applyNow(false).build();
        String configurationsRequestBody = ObjectMapperUtil.mapObjectToString(promoteConfigurations).replace("required", "isRequired");
       return HttpClientUtil.httpPOST(configurationsUpdateRequestURI, configurationsRequestBody, accessToken, "");

    }

    public static void addConfiguration(HttpClient client, TestActionRunner runner,
                                        ChoreoComponent component, Commit[] commitHistory, String envName,
                                        BalConfig... balconfigs) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = component.getId();
        String envIdToDeploy = component.getLatestAppEnvId(envName);
        String latestVersionId = component.getLatestApiVersion().getId();
        String latestCommitSha = component.getLatestCommitHash(commitHistory);
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


    public static void waitForComponentCreationSuccess(String accessToken, String choreoOrgHandle, String projectId,
                                                       String componentId)            throws ComponentCreationStatusCheckException,
            ComponentCreationTimeoutException {
        String choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        String requestURI = choreoEndpoint.concat("/orgs/" + choreoOrgHandle + "/projects/" + projectId + "/components/" + componentId + "/init/status");


        HttpGet request = new HttpGet(requestURI);

        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, accessToken);


        for (int i = 0; i < 15; ++i) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == org.apache.http.HttpStatus.SC_OK) {
                    JsonObject dataJsonObject = new JsonParser().parse(responseBody).getAsJsonObject().
                            getAsJsonObject("data");

                    String creationStatus =
                            dataJsonObject.get("status").isJsonNull() ? "" : dataJsonObject.get("status").getAsString();
                    if (creationStatus.equals("completed")) {
                        return;
                    }
                }
            } catch (IOException e) {
                throw new ComponentCreationStatusCheckException(e);
            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new ComponentCreationStatusCheckException(e);
            }
        }

        throw new ComponentCreationTimeoutException();
    }
}
