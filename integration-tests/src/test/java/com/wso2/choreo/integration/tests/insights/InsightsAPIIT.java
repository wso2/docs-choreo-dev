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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_API_RESOURCE;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Insights API test cases.
 */
public class InsightsAPIIT extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUUID;
    private static String environmentId;


    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, GetCommitHistoryException,
            NoLatestCommitHashFoundException, AddConfigurationsException, NoLatestAppEnvIdFoundException,
            ComponentCreationStatusCheckException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException, ComponentCreationException, ComponentRetrieveException,
            ApiLifecycleChangeException, ComponentCreationTimeoutException, ComponentDeploymentTimeoutException,
            NoLatestApiVersionFoundException, ComponentDeploymentFailureException, TokenRetrievalException {

        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
        ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
        orgUUID = org.getOrgUUID();
    }

    @Test
    @CitrusTest
    public void testGetEnvironments() throws IOException {
        String graphQlQuery =
                "query($orgFilter: OrgFilter!) {" +
                "   listEnvironments(org: $orgFilter) {" +
                "       id" +
                "       name" +
                "       type" +
                "   }" +
                "}";

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/insights/graphql/getEnvironmentsVariables.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("orgId", orgUUID);
        mustache.execute(writer, queryParams).flush();
        String graphQlVariables = writer.toString();
        String requestBody = "{\"query\":\"" + graphQlQuery + "\",\"variables\":" + graphQlVariables + "}";

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(INSIGHTS_API_RESOURCE)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath().expression("$.data.listEnvironments", greaterThan(0)))
                .validate((message, context) -> {
                    JsonArray environments = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonArray("listEnvironments");
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Environment>>(){}.getType();
                    List<Environment> environmentList = gson.fromJson(environments.toString(), collectionType);
                    for (Environment env : environmentList) {
                        if (env.getType().equals("CHOREO") && env.getName().equals("Development-Choreo")) {
                            environmentId = env.getId();
                            break;
                        }
                    }
                }));
    }

    @Test(dependsOnMethods = { "testGetEnvironments" })
    @CitrusTest
    public void testUtilityOperations() throws IOException {
        String graphQlQuery =
                "query ($dataFilter: DataFilter!, $tenantDataFilter: TenantDataFilter!) {" +
                        "  listAllAPI(dataFilter: $dataFilter) {" +
                        "    id" +
                        "    name" +
                        "    version" +
                        "    provider" +
                        "  }" +
                        "  listApplications(dataFilter: $dataFilter) {" +
                        "    id" +
                        "    name" +
                        "    owner" +
                        "  }" +
                        "  listProviders(dataFilter: $dataFilter) {" +
                        "    name" +
                        "  }" +
                        "  listSubscribers(dataFilter: $dataFilter) {" +
                        "    name" +
                        "  }" +
                        "  listTenants(tenantDataFilter: $tenantDataFilter)" +
                        "}";

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/insights/graphql/utilQueryVariables.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("orgId", orgUUID);
        queryParams.put("environmentId", environmentId);
        queryParams.put("tenant", "carbon.super");
        mustache.execute(writer, queryParams).flush();
        String graphQlVariables = writer.toString();
        String requestBody = "{\"query\":\"" + graphQlQuery + "\",\"variables\":" + graphQlVariables + "}";

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(INSIGHTS_API_RESOURCE)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/insights/utilQuerySuccess.json"))
                .validate(json()
                        .ignore("$.data.listAllAPI")
                        .ignore("$.data.listApplications")
                        .ignore("$.data.listProviders")
                        .ignore("$.data.listSubscribers")
                        .ignore("$.data.listTenants")
                )
                .validate(jsonPath()
                        .expression("$.data.listAllAPI.size()",  greaterThan(0))
                        .expression("$.data.listApplications.size()",  greaterThan(0))
                        .expression("$.data.listProviders.size()",  greaterThan(0))
                        .expression("$.data.listSubscribers.size()",  greaterThan(0))
                        .expression("$.data.listTenants.size()",  greaterThan(0))
                )
        );
    }

    @Test(dependsOnMethods = { "testGetEnvironments" })
    @CitrusTest
    public void testOverviewOperations() throws IOException {
        String graphQlQuery =
                "query($dataFilter: DataFilter!, $timeFilter: TimeFilter!) {" +
                "    getTotalTraffic(filter: $timeFilter, dataFilter: $dataFilter)" +
                "    getTotalErrors(filter: $timeFilter, dataFilter: $dataFilter)" +
                "    getOverallLatency(filter: $timeFilter, dataFilter: $dataFilter)" +
                "}";

        OffsetDateTime currentDateTimeAtUTC = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime sixMonthsAgoDateTimeAtUTC = currentDateTimeAtUTC.minusMonths(6);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/insights/graphql/overviewQueryVariables.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("from", sixMonthsAgoDateTimeAtUTC.toString());
        queryParams.put("to", currentDateTimeAtUTC.toString());
        queryParams.put("orgId", orgUUID);
        queryParams.put("environmentId", environmentId);
        queryParams.put("tenant", "carbon.super");
        mustache.execute(writer, queryParams).flush();
        String graphQlVariables = writer.toString();
        String requestBody = "{\"query\":\"" + graphQlQuery + "\",\"variables\":" + graphQlVariables + "}";

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(INSIGHTS_API_RESOURCE)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/insights/overviewQuerySuccess.json"))
                .validate(json()
                        .ignore("$.data.getTotalTraffic")
                        .ignore("$.data.getTotalErrors")
                        .ignore("$.data.getOverallLatency")
                )
                .validate(jsonPath()
                        .expression("$.data.getTotalTraffic", greaterThan(0L))
                        .expression("$.data.getTotalErrors", greaterThan(-1L))
                        .expression("$.data.getOverallLatency", greaterThan(0.0))
                )
        );
    }
}
