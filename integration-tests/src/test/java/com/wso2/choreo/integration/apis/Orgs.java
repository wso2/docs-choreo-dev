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
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Implements Orgs API calls and their response validations.
 */
public class Orgs {
    public static void addConfiguration(HttpClient client, TestActionRunner runner,
                                        ChoreoComponent component, String envName) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = component.getId();
        String envIdToDeploy = component.getLatestAppEnvId(envName);
        String latestVersionId = component.getLatestApiVersion().getId();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
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
                put("configs", "");
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap);

        // Update configurations
        runner.$(http()
                .client(client)
                .send()
                .post(configurationsUpdateRequestURI)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                .body(configurationsRequestBody));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK));
    }
}
