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
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.proxyapi.DeleteTestSessionRequest;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.proxyapi.TestSessionRequest;
import com.wso2.choreo.integration.models.proxyapi.TestSessionResponse;
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
                        .response()
                        .message()
                        .type(MessageType.JSON)
                        .validate((message, context) -> {
                            int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                            if (code != HttpStatus.OK.value()) {
                                throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                            }
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

    /**
     * generateTestSession generates a test session for the given internal endpoint per given user.
     *
     * @param runner - TestActionRunner runner
     * @param client - HttpClient client
     * @param accessToken - Access Token for accessing choreo control plane services
     * @param componentId - Component ID
     * @param environmentId - Environment ID
     * @param userIdpId - User IDP ID
     * @param endpointId - Endpoint ID
     * @param networkVisibility - Network visibility (Organization)
     * @return TestSessionResponse
     */
    public static TestSessionResponse generateTestSession(TestActionRunner runner, HttpClient client, String accessToken,
                                                          String componentId, String environmentId, String userIdpId,
                                                          String endpointId, String networkVisibility) {
        String resource = PROXY_RESOURCE + componentId + "/environment/" + environmentId + "/test-session";

        TestSessionRequest testSessionRequest = new TestSessionRequest();
        testSessionRequest.setEndpointId(endpointId);
        testSessionRequest.setUserIdpId(userIdpId);
        testSessionRequest.setNetworkVisibility(networkVisibility);
        String requestBody = ObjectMapperUtil.mapObjectToString(testSessionRequest);


        AtomicReference<TestSessionResponse> build = new AtomicReference<>();

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
                                .body(requestBody)
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    int responseCode = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (responseCode != HttpStatus.OK.value()) {
                                        throw new ValidationException(String.format("Too many successive calls with response code %s," +
                                                " expected response code %s", responseCode, HttpStatus.OK.value()));
                                    }
                                    build.set(ObjectMapperUtil.mapStringToObject(TestSessionResponse.class,
                                            message.getPayload(String.class), ""));
                                })));
        return build.get();
    }

    /**
     * deleteTestSession deletes the test session for a given sessionId.
     *
     * @param runner - TestActionRunner runner
     * @param client - HttpClient client
     * @param accessToken - Access Token for accessing choreo control plane services
     * @param componentId - Component ID
     * @param environmentId - Environment ID
     * @param userIdpId - User IDP ID
     * @param endpointId - Endpoint ID
     * @param sessionId - Test session ID
     */
    public static void deleteTestSession(TestActionRunner runner, HttpClient client, String accessToken,
                                         String componentId, String environmentId, String userIdpId, String endpointId,
                                         String sessionId) {
        String resource = PROXY_RESOURCE + componentId + "/environment/" + environmentId + "/test-session";

        DeleteTestSessionRequest deleteTestSessionRequest = new DeleteTestSessionRequest();
        deleteTestSessionRequest.setEndpointId(endpointId);
        deleteTestSessionRequest.setSessionId(sessionId);
        deleteTestSessionRequest.setUserIdpId(userIdpId);
        String requestBody = ObjectMapperUtil.mapObjectToString(deleteTestSessionRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .delete(resource)
                                .message()
                                .body(requestBody)
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    int responseCode = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (responseCode != HttpStatus.CREATED.value()) {
                                        throw new ValidationException(String.format("Too many successive calls with response code %s," +
                                                " expected response code %s", responseCode, HttpStatus.CREATED.value()));
                                    }
                                })));
    }
}
