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

package com.wso2.choreo.integration.common.oauth;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wso2.choreo.integration.apis.oauth.OAuthService;
import com.wso2.choreo.integration.models.oauth.ClientCredentialsResponseDTO;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * Utility class for OAuth related operations.
 */
public class OAuthUtils {
    
    /**
     * Invokes the client credentials grant type flow.
     *
     * @param runner           Citrus test runner
     * @param tokenEndpointURL Token endpoint URL
     * @param clientId         Client ID
     * @param clientSecret     Client secret
     * @param oAuthRequest     OAuth request
     * @return ClientCredentialsResponseDTO
     * @throws JsonMappingException   JsonMappingException
     * @throws JsonProcessingException JsonProcessingException
     */
    public static ClientCredentialsResponseDTO invokeClientCredentialsAuthFlow(TestActionRunner runner,
            String tokenEndpointURL, String clientId, String clientSecret,
            HashMap<String, Object> oAuthRequest) throws JsonMappingException, JsonProcessingException {

        return OAuthService.invokeClientCredentialsAuthFlow(runner,
                createHttpClientForClientCredentialsFlow(tokenEndpointURL), tokenEndpointURL, clientId, clientSecret,
                oAuthRequest);
    }

    private static HttpClient createHttpClientForClientCredentialsFlow(String tokenEndpointURL) {
        return CitrusEndpoints
                .http()
                .client()
                .restTemplate(restTemplate())
                .requestUrl(tokenEndpointURL)
                .build();
    }

    private static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(300000);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(300000);
        return restTemplate;
    }
}
