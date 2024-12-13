/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.pat;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.pat.PATListResponseDTO;
import com.wso2.choreo.integration.models.pat.PATResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class PATManagement {

    private static final String PAT_SERVICE_BASE_PATH = "api-key-service/v1.0/pat";

    /**
     * Generate PAT for a given user in given organization with provided set of scopes.
     *
     * @param runner            Citrus test runner
     * @param client            Citrus http client
     * @param alias             Alias
     * @param scopes            Scopes
     *
     * @return Generated PAT
     */
    public static PATResponseDTO generatePAT(TestActionRunner runner, HttpClient client, String alias, String[] scopes,
                                             int validity) throws IOException, TokenRetrievalException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        HashMap<String, Object> requestBodyMap = new HashMap<>(){{
            put("alias", alias);
            put("allowedScopes", scopes);
            put("validity", validity);
        }};
        String requestBody = ObjectMapperUtil.mapToString(requestBodyMap);
        String url = PAT_SERVICE_BASE_PATH.concat("/generate");

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(url)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        PATResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        PATResponseDTO.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), PATResponseDTO.class);
    }

    /**
     * Delete PAT of a given ID.
     *
     * @param runner    Citrus test runner
     * @param client    Citrus http client
     * @param patId     PAT ID
     */
    public static void deletePAT(TestActionRunner runner, HttpClient client, String patId)
            throws IOException, TokenRetrievalException, URISyntaxException {

        String url = PAT_SERVICE_BASE_PATH.concat("/").concat(patId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .delete(url)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.NO_CONTENT)));
    }

    /**
     * List all the PATs of a given user.
     *
     * @param runner    Citrus test runner
     * @param client    Citrus http client
     *
     * @return PAT list
     */
    public static PATListResponseDTO listPATsOfUser(TestActionRunner runner, HttpClient client)
            throws IOException, TokenRetrievalException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(PAT_SERVICE_BASE_PATH)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        PATListResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        PATListResponseDTO.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), PATListResponseDTO.class);
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }
}
