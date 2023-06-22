/*
 *  Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *  the WSO2 Commercial License available at http://wso2.com/licenses.
 *  For specific language governing the permissions and limitations under
 *  this license, please see the license as well as any agreement you’ve
 *  entered into with WSO2 governing the purchase of this software and any
 *  associated services.
 */

package com.wso2.choreo.integration.apis.proxydeployer;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

public class ProxyDeployer extends ControlPlaneAPI {

    private static final String PROXY_RESOURCE = "/proxy/deployer/v1/components/";

    public static void initiateDeployment(TestActionRunner runner, HttpClient client, String accessToken, String componentId, String versionId, String envId) {
        String resource = PROXY_RESOURCE + componentId + "/versions/" + versionId + "/initiate-deployment?environmentId=" + envId + "&accessMode=external";

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                        .client(client)
                        .send()
                        .post(resource)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                        .client(client)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)));
    }

    public static ProxyAPIBuild getApiBuilds(TestActionRunner runner, HttpClient client, String accessToken, String componentId, String versionId) {
        String resource = PROXY_RESOURCE + componentId + "/versions/" + versionId + "/builds";

        AtomicReference<ProxyAPIBuild> build = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                        .client(client)
                        .send()
                        .get(resource)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                        .client(client)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .validate((message, context) -> {
                            build.set(ObjectMapperUtil.mapStringToObject(ProxyAPIBuild.class, message.getPayload(String.class), ""));
                        })));

        return build.get();
    }

    public static void deployProxyAPI(TestActionRunner runner, HttpClient client, String accessToken,
                                                       String componentId, String versionId, String buildId, String envId) {
        String resource = PROXY_RESOURCE + componentId + "/versions/" + versionId + "/deploy-service?buildId=" +
                buildId + "&environmentId=" + envId;

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                        .client(client)
                        .send()
                        .post(resource)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                        .client(client)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)));
    }

    public static void promoteProxyAPI(TestActionRunner runner, HttpClient client, String accessToken,
                                         String componentId, String versionId, String fromEnv, String targetEnv, String buildId) {
        String resource = PROXY_RESOURCE + componentId + "/versions/" + versionId + "/promote?fromEnv=" +
                fromEnv+"&targetEnv="+targetEnv +  "&buildId=" + buildId;

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                        .client(client)
                        .send()
                        .post(resource)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                        .client(client)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)));
    }

    public static void getProxyAPIDeploymentStatus(TestActionRunner runner, HttpClient client, String accessToken,
                                      String componentId, String versionId, String requestId) {
        String resource = PROXY_RESOURCE + componentId + "/versions/" + versionId + "/deployment-status" + "?requestId=" + requestId;

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath().expression("$.status", "completed")
                                )));
    }
}
