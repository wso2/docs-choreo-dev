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

package com.wso2.choreo.integration.apis.APIMAppdev;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.apimAppdev.ConsumableScopesResponseDTO;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class APIMAppdevService {

    private static String APIM_APPDEV_BASE_PATH = "apim-appdev/v1.0";

    /**
     * Get consumable scopes.
     *
     * @param runner    Citrus test runner
     * @param client    Citrus http client
     * @param projectId Project ID
     * @return ConsumableScopesResponseDTO object
     * @throws URISyntaxException      If an error occurs while creating the URI
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     */
    public static ConsumableScopesResponseDTO getConsumableScopes(TestActionRunner runner, HttpClient client,
            String projectId) throws URISyntaxException, TokenRetrievalException, IOException {
        AtomicReference<String> responseDTO = new AtomicReference<>();

        String url = getConfigurableScopesListEndpoint(projectId);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("projectId", projectId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(uriBuilder.build().toString())
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
                                        ConsumableScopesResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ConsumableScopesResponseDTO.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ConsumableScopesResponseDTO.class);
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getConfigurableScopesListEndpoint(String projectId) {
        return APIM_APPDEV_BASE_PATH + "/projects/" + projectId + "/consumable-scopes";
    }
}
