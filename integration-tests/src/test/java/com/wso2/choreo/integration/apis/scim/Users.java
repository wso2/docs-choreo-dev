/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.scim;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.scim.UserCreateRequest;
import com.wso2.choreo.integration.models.scim.UserCreateResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for SCIM2 User management related operations.
 */
public class Users extends ControlPlaneAPI {

    public static UserCreateResponse createUser(TestActionRunner runner, HttpClient client, String accessToken,
                                                String orgHandle, UserCreateRequest userCreateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapObjectToString(userCreateRequest);

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getScim2UserCreateEndpoint(orgHandle))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, Constant.BEARER_PREFIX.concat(accessToken))
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        UserCreateResponse response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        UserCreateResponse.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), UserCreateResponse.class);
    }

    public static void deleteUser(TestActionRunner runner, HttpClient client, String accessToken, String orgHandle,
                                  String userIdpId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getScim2UserDeleteEndpoint(orgHandle, userIdpId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, Constant.BEARER_PREFIX.concat(accessToken))
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.NO_CONTENT)));
    }

    private static String getScim2UserCreateEndpoint(String orgHandle) {

        return "/t/" + orgHandle + "/scim2/Users";
    }

    private static String getScim2UserDeleteEndpoint(String orgHandle, String userIdpId) {

        return "/t/" + orgHandle + "/scim2/Users/" + userIdpId;
    }
}
