/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common.devPortalApiKey;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.common.Endpoints;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DevPortalApiKeyUtils {

    public enum KeySetType {
        PRODUCTION,
        SANDBOX
    }

    public static final String DEFAULT_API_KEY_HEADER = "Api-Key";

    public static void enableAPIKeySecurityForAPI(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                  String apiId, String accessToken) {

        HttpClient httpClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        JsonObject apiInfo = ApiManager.getApi(runner,httpClient,accessToken,apiId);
        if (apiInfo.has("securityScheme")) {
            apiInfo.remove("securityScheme");
        }
        JsonArray securityScheme = new JsonArray();
        securityScheme.add("api_key");
        apiInfo.add("securityScheme",securityScheme);
        ApiManager.updateApi(runner,httpClient,accessToken,apiId,apiInfo);
    }

    public static void changeApiKeyHeader(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                          String apiId, String headerName, String accessToken) {

        HttpClient httpClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        JsonObject apiInfo = ApiManager.getApi(runner,httpClient,accessToken,apiId);
        apiInfo.remove("apiKeyHeader");
        apiInfo.addProperty("apiKeyHeader",headerName);
        ApiManager.updateApi(runner,httpClient,accessToken,apiId,apiInfo);
    }

    public static void invokeApiGET(TestActionRunner runner, String apiKeyHeaderName, String apiKey, String invokeUrl, String testSessionId ,String resource,
                                    HttpStatus expectedResponseCode) throws Exception {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions((http()
                                .client(invokeUrl)
                                .send()
                                .get(resource)
                                .message()
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header(apiKeyHeaderName, apiKey))
                                .header("x-choreo-test-session-id", testSessionId),
                        http()
                                .client(invokeUrl)
                                .receive()
                                .response(expectedResponseCode)
                                .message()
                                .type(MessageType.PLAINTEXT)));

        TimeUnit.SECONDS.sleep(2);
    }

}
