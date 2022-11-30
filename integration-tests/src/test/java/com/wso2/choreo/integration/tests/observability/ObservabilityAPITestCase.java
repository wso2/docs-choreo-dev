/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ObservabilityIdInformation;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class ObservabilityAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static final Map<String, JsonObject> envSyntaxTrees = new HashMap<>(2);
    private static RestApiChoreoComponent restApiComponent;
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    @Autowired
    private HttpClient choreoCPTestClient;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][] {{Constant.DEV_ENVIRONMENT}, {Constant.PROD_ENVIRONMENT}};
    }

    @BeforeClass
    public void beforeClass()
            throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        ChoreoOrganization org = new ChoreoOrganization(orgHandle,orgId,orgUuid);
        ChoreoProject project = GraphQL.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
        restApiComponent =
                (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
        restApiComponent.setProject(project);
        restApiComponent.setOrganization(org);

        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.DEV_ENVIRONMENT);
        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 4);

        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.PROD_ENVIRONMENT);
        restApiComponent.promote(accessToken, Constant.DEV_ENVIRONMENT, Constant.PROD_ENVIRONMENT);
        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Production", 4);

        restApiComponent.waitForMetricsData(accessToken, Constant.DEV_ENVIRONMENT);
        restApiComponent.waitForTraceData(accessToken, Constant.DEV_ENVIRONMENT);
        restApiComponent.waitForMetricsData(accessToken, Constant.PROD_ENVIRONMENT);
        restApiComponent.waitForTraceData(accessToken, Constant.PROD_ENVIRONMENT);
    }

    @Test(dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityAST(String env) throws IOException, ObservabilityIdNotFoundException,
            ObservabilityIdCheckException, InterruptedException, ReleaseIdNotFoundException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForAst.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("obsId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.data.ast.__typename", "ast")
                        .expression("$.data.ast.keySet()", hasItems("__typename", "ast"))
                )
                .validate((message, context) -> {
                    JsonParser parser = new JsonParser();
                    String astString = parser.parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("ast").get("ast").getAsString();
                    envSyntaxTrees.put(env, parser.parse(astString).getAsJsonObject());
                })
        );
    }

    @Test(dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityMetricDensity(String env) throws IOException, ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForMetricDensity.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.data.metricDensity.__typename", "metricDensity")
                        .expression("$.data.metricDensity.keySet()", hasItems("__typename", "metricCounts"))
                        .expression("$.data.metricDensity.metricCounts.size()", 4)
                        .expression("$.data.metricDensity.metricCounts[0].keySet()", hasItems("__typename", "count", "range"))
                )
        );
    }

    @Test(dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityMetricDensityHistogram(String env) throws IOException, ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForMetricDensityHistogram.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        queryParams.put("from", Instant.now().minusSeconds(60 * 60 * 24).toString());
        queryParams.put("to", Instant.now().toString());
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.data.metricDensityHistogram.__typename", "metricDensityHistogram")
                        .expression("$.data.metricDensityHistogram.keySet()", hasItems("__typename", "metricDensityHistogram"))
                        .expression("$.data.metricDensityHistogram.metricDensityHistogram.size()", greaterThan(1))
                        .expression("$.data.metricDensityHistogram.metricDensityHistogram[0].keySet()", hasItems("__typename", "time", "value"))

                )
        );
    }

    @Test(dependsOnMethods = {"testObservabilityAST"}, dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityStats(String env) throws IOException, ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        JsonObject ast = envSyntaxTrees.get(env);
        String moduleId = ast.get("packageOrg").getAsString() + "/" + ast.get("packageName").getAsString() + ":" +
                ast.get("packageVersion").getAsString();

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForObservabilityStats.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        queryParams.put("moduleId", moduleId);
        queryParams.put("from", Instant.now().minusSeconds(60 * 60 * 24).toString());
        queryParams.put("to", Instant.now().toString());
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                        .client(choreoCPTestClient)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .validate(jsonPath()
                                        .expression("$.data.invocationMetrics.__typename", "invocationMetrics")
                                        .expression("$.data.invocationMetrics.keySet()", hasItems("__typename", "failedInvocationCounts", "meanInvocationTimes", "successfulInvocationCounts"))
                                        .expression("$.data.invocationMetrics.meanInvocationTimes.size()", greaterThan(0))
                                        .expression("$.data.invocationMetrics.failedInvocationCounts.size()", greaterThan(0))
                                        .expression("$.data.invocationMetrics.successfulInvocationCounts.size()", greaterThan(0))
                        )
        );
    }

    @Test(dependsOnMethods = {"testObservabilityAST"}, dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityTraceList(String env) throws IOException, ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        JsonObject ast = envSyntaxTrees.get(env);
        String moduleId = ast.get("packageOrg").getAsString() + "/" + ast.get("packageName").getAsString() + ":" +
                ast.get("packageVersion").getAsString();

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForTraceList.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        queryParams.put("moduleId", moduleId);
        queryParams.put("entryPointFuncModule", moduleId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        queryParams.put("from", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        queryParams.put("to", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS)));
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();

        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.data.requestTraceGroup.keySet()", hasItems("__typename", "traces", "totalCount"))
                        .expression("$.data.requestTraceGroup.traces[0].keySet()", hasItems("__typename", "duration", "errorStatus", "httpStatusCode", "startTime", "traceId"))
                        .expression("$.data.requestTraceGroup.traces.size()", greaterThan(1))
                        .expression("$.data.requestTraceGroup.totalCount", 4)
                        .expression("$.data.requestTraceGroup.traces[*].httpStatusCode", everyItem(containsString("200")))
                        .expression("$.data.requestTraceGroup.traces[*].errorStatus", everyItem(containsString("false")))
                        .expression("$.data.requestTraceGroup.traces[*].traceId", everyItem(is(not(emptyString()))))
                        .expression("$.data.requestTraceGroup.traces[*].duration", everyItem(greaterThan(1L)))
                )
        );
    }

    @Test(dependsOnMethods = {"testObservabilityTraceList"}, dataProvider = "env-provider")
    @CitrusTest
    public void testObservabilityTraceInformation(String env) throws IOException, ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        JsonObject ast = envSyntaxTrees.get(env);
        String moduleId = ast.get("packageOrg").getAsString() + "/" + ast.get("packageName").getAsString() + ":" +
                ast.get("packageVersion").getAsString();

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache queryTraceList = mf.compile("templates/observability/graphql/queryForTraceList.mustache");
        Writer traceListWriter = new StringWriter();
        Map<String, String> traceListQueryParams = new HashMap<>();
        traceListQueryParams.put("observeId", observabilityIdInformation.getObsId());
        traceListQueryParams.put("version", observabilityIdInformation.getVerzion());
        traceListQueryParams.put("moduleId", moduleId);
        traceListQueryParams.put("entryPointFuncModule", moduleId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        traceListQueryParams.put("from", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        traceListQueryParams.put("to", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS)));
        queryTraceList.execute(traceListWriter, traceListQueryParams).flush();
        String body = traceListWriter.toString();

        // Getting trace list
        String requestURI = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT)
                .concat(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String traceId =  new JsonParser().parse(response.body()).getAsJsonObject().getAsJsonObject("data")
                .getAsJsonObject("requestTraceGroup").getAsJsonArray("traces").get(0).getAsJsonObject().get("traceId").getAsString();

        // Getting specific traceInformation
        Mustache queryTraceInformation = mf.compile("templates/observability/graphql/queryForTraceInformation.mustache");
        Map<String, String> traceInformationQueryParams = new HashMap<>();
        traceInformationQueryParams.put("observeId", observabilityIdInformation.getObsId());
        traceInformationQueryParams.put("version", observabilityIdInformation.getVerzion());
        traceInformationQueryParams.put("moduleId", moduleId);
        traceInformationQueryParams.put("traceId", traceId);
        traceInformationQueryParams.put("from", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        traceInformationQueryParams.put("to", fmt.format(OffsetDateTime.now( ZoneOffset.UTC ).truncatedTo(ChronoUnit.SECONDS)));
        Writer traceInformationWriter = new StringWriter();
        queryTraceInformation.execute(traceInformationWriter, traceInformationQueryParams).flush();
        body = traceInformationWriter.toString();
        $(http()
                .client(choreoCPTestClient)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.data.traceById.__typename", "traceById")
                        .expression("$.data.traceById.keySet()", hasItems("__typename", "spans"))
                        .expression("$.data.traceById.spans.size()", greaterThan(0))
                        .expression("$.data.traceById.spans[0].checkpoints[0].keySet()", hasItems("__typename", "moduleId", "positionId", "timestamp"))
                        .expression("$.data.traceById.spans[0].keySet()", hasItems("__typename", "checkpoints", "duration", "errorMsg", "errorStatus", "httpStatusCode", "position"))
                        .expression("$.data.traceById.spans[0].checkpoints[*].__typename", everyItem(containsString("checkpoint")))
                        .expression("$.data.traceById.spans[0].checkpoints[*].moduleId", everyItem(containsString(moduleId)))
                        .expression("$.data.traceById.spans[0].checkpoints[*].positionId", allOf(is(not(emptyString()))))
                )
        );
    }
}
