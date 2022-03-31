/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.tests.insights;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Random;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_LATENCY_ALERT_API_RESOURCE;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_TRAFFIC_ALERT_API_RESOURCE;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Alert API test cases.
 */
public class AlertAPIIT extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String organization;
    private static String trafficAlertConfigurationId;
    private static String latencyAlertConfigurationId;
    private static String apiName;
    private static final String tenant = "carbon.super";

    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, GetCommitHistoryException,
            NoLatestCommitHashFoundException, AddConfigurationsException, NoLatestAppEnvIdFoundException,
            ComponentCreationStatusCheckException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException,
            ComponentCreationException, ComponentRetrieveException, ApiLifecycleChangeException,
            ComponentCreationTimeoutException, ComponentDeploymentTimeoutException, NoLatestApiVersionFoundException,
            ComponentDeploymentFailureException, TokenRetrievalException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
        ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);

        organization = org.getOrgUUID();

        Random random = new Random();
        String generatedString = random.ints(97, 123)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        apiName = "TestInsightsAlertAPI" + generatedString;
    }

    @Test
    @CitrusTest
    public void testTrafficGet() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(INSIGHTS_TRAFFIC_ALERT_API_RESOURCE)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath().expression("$.alertConfiguration.size()", greaterThanOrEqualTo(0)))
        );
    }

    @Test
    @CitrusTest
    public void testTrafficPost() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(INSIGHTS_TRAFFIC_ALERT_API_RESOURCE)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "    \"alertConfiguration\": {\n" +
                        "        \"apiName\": \"" + apiName + "\",\n" +
                        "        \"apiVersion\": \"1.0.0\",\n" +
                        "        \"threshold\": 5,\n" +
                        "        \"emails\": [\"choreo-integration-test-user-dev@wso2.com\"],\n" +
                        "        \"metric\": \"HIT_COUNT\"\n" +
                        "    }\n" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(((message, testContext) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("alertConfiguration");
                    trafficAlertConfigurationId = component.get("id").getAsString();
                }))
        );
    }

    @Test(dependsOnMethods = { "testTrafficPost" })
    @CitrusTest
    public void testTrafficPut() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .put(INSIGHTS_TRAFFIC_ALERT_API_RESOURCE + "/" + trafficAlertConfigurationId)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "    \"alertConfiguration\": {\n" +
                        "        \"apiName\": \"" + apiName + "\",\n" +
                        "        \"apiVersion\": \"1.0.0\",\n" +
                        "        \"threshold\": 30,\n" +
                        "        \"emails\": [\"choreo-integration-test-user-dev@wso2.com\"],\n" +
                        "        \"metric\": \"HIT_COUNT\"\n" +
                        "    }\n" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/insights/alert/put_traffic_alert_success.json"))
                .validate(json()
                        .ignore("$.environment")
                        .ignore("$.organization")
                        .ignore("$.alertConfiguration.id")
                        .ignore("$.alertConfiguration.apiName")
                )
        );
    }

    @Test(dependsOnMethods = { "testTrafficPut" })
    @CitrusTest
    public void testTrafficDelete() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .delete(INSIGHTS_TRAFFIC_ALERT_API_RESOURCE + "/" + trafficAlertConfigurationId)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.PLAINTEXT)
                .body("Traffic alert configuration '" + trafficAlertConfigurationId + "' successfully deleted")
        );
    }

    @Test
    @CitrusTest
    public void testLatencyGet() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(INSIGHTS_LATENCY_ALERT_API_RESOURCE)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath().expression("$.alertConfiguration.size()", greaterThanOrEqualTo(0)))
        );
    }

    @Test
    @CitrusTest
    public void testLatencyPost() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(INSIGHTS_LATENCY_ALERT_API_RESOURCE)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "    \"alertConfiguration\": {\n" +
                        "        \"apiName\": \"" + apiName + "\",\n" +
                        "        \"apiVersion\": \"1.0.0\",\n" +
                        "        \"threshold\": 5,\n" +
                        "        \"emails\": [\"choreo-integration-test-user-dev@wso2.com\"],\n" +
                        "        \"metric\": \"RESPONSE_LATENCY\"\n" +
                        "    }\n" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(((message, testContext) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("alertConfiguration");
                    latencyAlertConfigurationId = component.get("id").getAsString();
                }))
        );
    }

    @Test(dependsOnMethods = { "testLatencyPost" })
    @CitrusTest
    public void testLatencyPut() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .put(INSIGHTS_LATENCY_ALERT_API_RESOURCE + "/" + latencyAlertConfigurationId)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "    \"alertConfiguration\": {\n" +
                        "        \"apiName\": \"" + apiName + "\",\n" +
                        "        \"apiVersion\": \"1.0.0\",\n" +
                        "        \"threshold\": 255,\n" +
                        "        \"emails\": [\"choreo-integration-test-user-dev@wso2.com\"],\n" +
                        "        \"category\": \"LATENCY\",\n" +
                        "        \"metric\": \"RESPONSE_LATENCY\"\n" +
                        "    }\n" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/insights/alert/put_latency_alert_success.json"))
                .validate(json()
                        .ignore("$.environment")
                        .ignore("$.organization")
                        .ignore("$.alertConfiguration.id")
                        .ignore("$.alertConfiguration.apiName")
                )
        );
    }

    @Test(dependsOnMethods = { "testLatencyPut" })
    @CitrusTest
    public void testLatencyDelete() {
        $(http()
                .client(choreoCPTestClient)
                .send()
                .delete(INSIGHTS_LATENCY_ALERT_API_RESOURCE + "/" + latencyAlertConfigurationId)
                .queryParam("organization", organization)
                .queryParam("environment", Configuration.INSIGHTS_ALERT_ENVIRONMENT)
                .queryParam("tenant", tenant)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.PLAINTEXT)
                .body("Latency alert configuration '" + latencyAlertConfigurationId + "' successfully deleted")
        );
    }
}
