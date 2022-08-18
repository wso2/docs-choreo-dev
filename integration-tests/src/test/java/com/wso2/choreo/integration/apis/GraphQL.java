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
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements GraphQL API calls and their response validations.
 */
public class GraphQL {
    public static void deployComponent(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", component.getId());
                put("latestVersionId", component.getLatestApiVersion().getId());
                put("devEnvIdToDeploy", component.getLatestAppEnvId("dev"));
                put("branch", component.getRepository().getBranch());
                put("latestCommitSha", latestCommitSha);
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/deployComponent.mustache",
                requestParams);

        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        // Deploy component
        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/graphql/responses/deployComponentSuccess.json"))
                .validate(json()));
    }

    public static void deploymentStatusByVersion(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", component.getId());
                put("latestVersionId", component.getLatestApiVersion().getId());
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/deploymentStatusByVersion.mustache",
                requestParams);

        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 36")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION,
                                        TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs())
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/graphql/responses/deploymentStatusByVersionSuccess.json"))
                                .validate(json())));
    }

    public static void componentDeployment(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        Map<String, String> requestParams = new HashMap<>() {
            {
                put("orgHandler", component.getOrgHandler());
                put("orgUuid", component.getOrganization().getOrgUUID());
                put("componentId", component.getId());
                put("versionId", component.getLatestApiVersion().getId());
                put("environmentId", component.getLatestAppEnvId("dev"));
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/componentDeployment.mustache", requestParams);
        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);

        Map<String, String> responseParams = new HashMap<>() {
            {
                put("environmentId", component.getLatestAppEnvId("dev"));
                put("sha", latestCommitSha);
                put("versionId", component.getLatestApiVersion().getId());
            }
        };

        String expectedResponse = ComponentUtils.generateStringFromTemplate(
                "templates/graphql/responses/componentDeploymentSuccess.mustache", responseParams);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 25")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(expectedResponse)));
    }
}
