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

package com.wso2.choreo.integration.apis.usermgt;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.usermgt.RegisterEnterpriseUserRequest;
import com.wso2.choreo.integration.models.usermgt.RegisterEnterpriseUserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for User management related operations.
 */
public class UserManagement {

    private static String USER_MGT_BASE_PATH = "/user-mgt/1.0.0";

    public static RegisterEnterpriseUserResponse addEnterpriseUser(TestActionRunner runner, HttpClient client,
                                                                   String accessToken,
                                                                   RegisterEnterpriseUserRequest regEntprUserRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapObjectToString(regEntprUserRequest);

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getAddEnterpriseUserEndpoint())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, Constant.BEARER_PREFIX.concat(accessToken))
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
                                        RegisterEnterpriseUserResponse response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        RegisterEnterpriseUserResponse.class);
                                        if (response.getIdpId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        System.out.println("Deserialized Response: " + response);
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), RegisterEnterpriseUserResponse.class);
    }

    public static void validateUser(TestActionRunner runner, HttpClient client, String accessToken, HttpStatus status)
            throws TokenRetrievalException, IOException, URISyntaxException {

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(getValidateUserEndpoint())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, Constant.BEARER_PREFIX.concat(accessToken))
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(status)));
    }

    private static String getAddEnterpriseUserEndpoint() {

        return USER_MGT_BASE_PATH + "/add-enterprise-user";
    }

    private static String getValidateUserEndpoint() {

        return USER_MGT_BASE_PATH + "/validate/user";
    }
}
