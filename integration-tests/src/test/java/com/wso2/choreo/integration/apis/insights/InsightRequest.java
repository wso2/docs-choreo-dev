/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.insights;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.Insights.InsightDTO;
import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

public class InsightRequest extends ControlPlaneAPI {
    private static String configurationId;

    public static void getInsightTrafficOrLatency(TestActionRunner runner, HttpClient client, String accessToken,
            InsightDTO insightDTO, String pathExpression, Matcher matcher, String path) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .get(path)
                .queryParam("organization", insightDTO.getOrganization())
                .queryParam("environment", insightDTO.getEnvironmentId())
                .queryParam("tenant", insightDTO.getTenant())
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath().expression(pathExpression, matcher))
        );
    }

    public static void deleteInsightTrafficOrLatency(TestActionRunner runner, HttpClient client, String accessToken,
            InsightDTO insightDTO, String path) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .delete(path)
                .queryParam("organization", insightDTO.getOrganization())
                .queryParam("environment", insightDTO.getEnvironmentId())
                .queryParam("tenant", insightDTO.getTenant())
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.PLAINTEXT)
        );
    }

    public static String postInsightTrafficOrLatency(TestActionRunner runner, HttpClient client, String accessToken,
            InsightDTO insightDTO, String path) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/insights/traffic_payload.mustache", insightDTO);
        runner.$(http()
                .client(client)
                .send()
                .post(path)
                .queryParam("organization", insightDTO.getOrganization())
                .queryParam("environment", insightDTO.getEnvironmentId())
                .queryParam("tenant", insightDTO.getTenant())
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(queryString)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(((message, testContext) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("alertConfiguration");
                    configurationId = component.get("id").getAsString();
                }))
        );
        return configurationId;
    }

    public static void putInsightTrafficOrLatency(TestActionRunner runner, HttpClient client, String accessToken,
            InsightDTO insightDTO, String path) throws IOException {
        String body = ObjectMapperUtil.mapObjectToString(
                "templates/insights/traffic_payload.mustache", insightDTO);
        String templatePath = "templates/insights/alert/put_traffic_alert_success.json";
        if (insightDTO.isLatency()) {
                templatePath = "templates/insights/alert/put_latency_alert_success.json";
        }

        runner.$(http()
                .client(client)
                .send()
                .put(path)
                .queryParam("organization", insightDTO.getOrganization())
                .queryParam("environment", insightDTO.getEnvironmentId())
                .queryParam("tenant", insightDTO.getTenant())
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource(templatePath))
                .validate(json()
                        .ignore("$.environment")
                        .ignore("$.organization")
                        .ignore("$.alertConfiguration.id")
                        .ignore("$.alertConfiguration.apiName")
                )
        );
    }
}
