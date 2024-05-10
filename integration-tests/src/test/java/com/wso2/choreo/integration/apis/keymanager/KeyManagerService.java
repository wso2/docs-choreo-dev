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

package com.wso2.choreo.integration.apis.keymanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.keymanager.ConfigUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddRequestDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpDiscoveryResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManagerListAdminResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManagerListPublisherResponseDTO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class KeyManagerService {

    private static String APIM_APPDEV_BASE_PATH = "apim-appdev/v1.0/sts";
    private static String KEY_MANAGER_PUBLISHER_BASE_PATH = "api/am/publisher/v3/key-managers";
    private static String KEY_MANAGER_ADMIN_BASE_PATH = "api/am/admin/v2/key-managers";

    public static ConfigUpdateResponseDTO updateKeysetConfigurations(TestActionRunner runner, HttpClient client,
            String oAuthAppId, HashMap<String, Object> configUpdateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(configUpdateRequest);

        String url = getConfigUpdateURL(oAuthAppId);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
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
                                        ConfigUpdateResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ConfigUpdateResponseDTO.class);
                                        if (response.getClientId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ConfigUpdateResponseDTO.class);
    }

    public static KeyManagerListAdminResponseDTO getKeyManagersAsAdmin(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        URIBuilder uriBuilder = new URIBuilder(KEY_MANAGER_ADMIN_BASE_PATH);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("environmentId", environmentId);

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
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyManagerListAdminResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyManagerListAdminResponseDTO.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyManagerListAdminResponseDTO.class);
    }

    public static KeyManagerListPublisherResponseDTO getKeyManagersAsPublisher(TestActionRunner runner,
            HttpClient client, String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        URIBuilder uriBuilder = new URIBuilder(KEY_MANAGER_PUBLISHER_BASE_PATH);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("environmentId", environmentId);

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
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyManagerListPublisherResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyManagerListPublisherResponseDTO.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyManagerListPublisherResponseDTO.class);
    }

    /**
     * These non Citrus based implementation is to be used in cases where the Citrus
     * framework is yet to be initialized, such as in the BeforeSuite
     */

    public static IdpDiscoveryResponseDTO getDiscoveryInfo(String wellKnownURL, String type)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpPost request = new HttpPost(getStsEndpoint().concat(getDiscoveryEndpointURL()));

        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        request.setURI(uriBuilder.build());

        HashMap<String, String> requestPayload = new HashMap<>() {
            {
                put("url", wellKnownURL);
                put("type", type);
            }
        };

        String requestBody = new ObjectMapper().writeValueAsString(requestPayload);

        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, getAccessToken());
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.MULTIPART_FORM_DATA);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new RuntimeException(responseBody);
            }

            return new ObjectMapper().readValue(responseBody, IdpDiscoveryResponseDTO.class);
        }
    }

    public static IdpAddResponseDTO addExternalIdp(IdpAddRequestDTO requestPayload)
            throws URISyntaxException, TokenRetrievalException, IOException {

        HttpPost request = new HttpPost(getStsEndpoint().concat(KEY_MANAGER_ADMIN_BASE_PATH));

        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        request.setURI(uriBuilder.build());

        String requestBody = new ObjectMapper().writeValueAsString(requestPayload);

        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, getAccessToken());
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != org.apache.http.HttpStatus.SC_CREATED) {
                throw new RuntimeException(responseBody);
            }

            return new ObjectMapper().readValue(responseBody, IdpAddResponseDTO.class);
        }
    }

    public static KeyManagerListAdminResponseDTO getKeyManagersListAsAdmin()
            throws URISyntaxException, IOException, TokenRetrievalException {

        URIBuilder uriBuilder = new URIBuilder(KEY_MANAGER_ADMIN_BASE_PATH);
        uriBuilder.addParameter(
                "organizationId",
                Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

        HttpGet request = new HttpGet(getStsEndpoint().concat(uriBuilder.build().toString()));

        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, getAccessToken());

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new RuntimeException(responseBody);
            }

            return new ObjectMapper().readValue(responseBody, KeyManagerListAdminResponseDTO.class);
        }
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getDiscoveryEndpointURL() {
        return KEY_MANAGER_ADMIN_BASE_PATH + "/discover";
    }

    private static String getConfigUpdateURL(String oAuthAppId) {

        return APIM_APPDEV_BASE_PATH + "/oauth-applications/" + oAuthAppId;
    }

    private static String getStsEndpoint() {

        return Configuration.getConfig(ConfigDefinition.STS_ENDPOINT) + "/";
    }

}