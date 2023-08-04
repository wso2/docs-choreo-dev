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
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.wso2.choreo.integration.apis.insights.InsightRequest;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Insights.InsightDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.wso2.choreo.integration.config.Constant.INSIGHTS_API_RESOURCE;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_LATENCY_ALERT_API_RESOURCE;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_TRAFFIC_ALERT_API_RESOURCE;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Alert API test cases.
 */
public class InsightsAlertAPIIT extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String trafficAlertConfigurationId;
    private static String latencyAlertConfigurationId;
    private static String apiName;
    private static String externalEnvId;
    private static final String tenant = "carbon.super";
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        Random random = new Random();
        String generatedString = random.ints(97, 123)
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        apiName = "TestInsightsAlertAPI" + generatedString;

        getEnvironmentIds(orgUuid, accessToken);
    }

    public static void getEnvironmentIds(String orgUUID, String accessToken) throws IOException, InterruptedException {
        String requestURI =
                Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT) + "/" + INSIGHTS_API_RESOURCE;
        InsightDTO dto = InsightDTO.builder().orgId(orgUUID).build();
        String queryString = ObjectMapperUtil
                .mapObjectToString("templates/insights/graphql/getEnvironmentsVariables.mustache", dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(queryString))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray environments = new JsonParser().parse((String) response.body()).getAsJsonObject()
                .getAsJsonObject("data")
                .getAsJsonArray("listEnvironments");
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Environment>>(){}.getType();
        List<Environment> environmentList = gson.fromJson(environments.toString(), collectionType);
        for (Environment env : environmentList) {
            if (env.getType().equals("CHOREO") && env.getName().equals("Development")) {
                externalEnvId = env.getExternalEnvId();
                break;
            }
        }
    }

    @Test
    @CitrusTest
    public void testTrafficGet() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .build();
            InsightRequest.getInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto,
                            "$.alertConfiguration.size()", greaterThanOrEqualTo(0),
                            INSIGHTS_TRAFFIC_ALERT_API_RESOURCE);
    }

    @Test
    @CitrusTest
    public void testTrafficPost() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .apiName(apiName)
                            .email("choreo-integration-test-user-dev@wso2.com")
                            .apiVersion("1.0.0")
                            .threshold(5)
                            .metric("HIT_COUNT")
                            .build();
            trafficAlertConfigurationId = InsightRequest.postInsightTrafficOrLatency(this, choreoCPTestClient,
                            accessToken, dto, INSIGHTS_TRAFFIC_ALERT_API_RESOURCE);
    }

    @Test(dependsOnMethods = { "testTrafficPost" })
    @CitrusTest
    public void testTrafficPut() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .apiName(apiName)
                            .email("choreo-integration-test-user-dev@wso2.com")
                            .apiVersion("1.0.0")
                            .threshold(30)
                            .metric("HIT_COUNT")
                            .build();
            InsightRequest.putInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto,
                            INSIGHTS_TRAFFIC_ALERT_API_RESOURCE + "/" + trafficAlertConfigurationId);
    }

    @Test(dependsOnMethods = { "testTrafficPut" })
    @CitrusTest
    public void testTrafficDelete() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .build();
            String path = INSIGHTS_TRAFFIC_ALERT_API_RESOURCE + "/" + trafficAlertConfigurationId;
            InsightRequest.deleteInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto, path);
    }

    @Test
    @CitrusTest
    public void testLatencyGet() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .build();
            InsightRequest.getInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto,
                            "$.alertConfiguration.size()", greaterThanOrEqualTo(0),
                            INSIGHTS_LATENCY_ALERT_API_RESOURCE);
    }

    @Test
    @CitrusTest
    public void testLatencyPost() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .metric("RESPONSE_LATENCY")
                            .apiName(apiName)
                            .email("choreo-integration-test-user-dev@wso2.com")
                            .apiVersion("1.0.0")
                            .threshold(5)
                            .build();
            latencyAlertConfigurationId = InsightRequest.postInsightTrafficOrLatency(this, choreoCPTestClient,
                            accessToken,
                            dto, INSIGHTS_LATENCY_ALERT_API_RESOURCE);
    }

    @Test(dependsOnMethods = { "testLatencyPost" })
    @CitrusTest
    public void testLatencyPut() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .apiName(apiName)
                            .email("choreo-integration-test-user-dev@wso2.com")
                            .apiVersion("1.0.0")
                            .threshold(255)
                            .metric("RESPONSE_LATENCY")
                            .isLatency(true)
                            .build();
            InsightRequest.putInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto,
                            INSIGHTS_LATENCY_ALERT_API_RESOURCE + "/" + latencyAlertConfigurationId);
    }

    @Test(dependsOnMethods = { "testLatencyPut" })
    @CitrusTest
    public void testLatencyDelete() throws IOException {
            InsightDTO dto = InsightDTO.builder().environmentId(externalEnvId).organization(orgUuid).tenant(tenant)
                            .build();
            String path = INSIGHTS_LATENCY_ALERT_API_RESOURCE + "/" + latencyAlertConfigurationId;
            InsightRequest.deleteInsightTrafficOrLatency(this, choreoCPTestClient, accessToken, dto, path);
    }
}
