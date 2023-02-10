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
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class ObservabilityAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static final Map<String, JsonObject> envSyntaxTrees = new HashMap<>(2);

    ChoreoProject project;
    String repoName;
    String projectId;
    String devInvokeURL;
    String prodInvokeURL;
    Environment[] en;
    String apiKey;
    ChoreoComponent choreoComponent;
    ChoreoOrganization org;
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    @Autowired
    private HttpClient choreoCPTestClient;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{"dev"}, {"prod"}};
    }


    @BeforeClass
    public void setup_ObservabilityAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        projectId = project.getId();
    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_ObservabilityAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).
                triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/rest-api").
                projectId(projectId).
                displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = GraphQL.createUserManagedComponent(project, dto, accessToken);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponent_ObservabilityAPITestCase"})
    @CitrusTest
    public void createdComponentStatus_ObservabilityAPITestCase() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, choreoComponent.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_ObservabilityAPITestCase"})
    @CitrusTest
    public void componentRetrieval_ObservabilityAPITestCase() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandler(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_ObservabilityAPITestCase"})
    @CitrusTest
    public void addDeploymentConfiguration_ObservabilityAPITestCase() throws Exception {
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Response res = Orgs.addConfiguration(choreoComponent, "dev", accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"addDeploymentConfiguration_ObservabilityAPITestCase"})
    @CitrusTest
    public void deploy_ObservabilityAPITestCase() throws Exception {
        GraphQL.deployComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"deploy_ObservabilityAPITestCase"})
    @CitrusTest
    public void deploymentStatusByVersion_ObservabilityAPITestCase() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_ObservabilityAPITestCase"})
    @CitrusTest
    public void componentDevDeploymentStatus_ObservabilityAPITestCase() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, "dev", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ObservabilityAPITestCase"})
    @CitrusTest
    public void addPromoteConfiguration_ObservabilityAPITestCase() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, "prod", accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_ObservabilityAPITestCase"})
    @CitrusTest
    public void promote_ObservabilityAPITestCase() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_ObservabilityAPITestCase"})
    @CitrusTest
    public void componentProdDeploymentStatus_ObservabilityAPITestCase() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_ObservabilityAPITestCase"})
    @CitrusTest
    public void invokeEP_ObservabilityAPITestCase() throws IOException {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        TestHelper.invokeEP(devInvokeURL,apiKey);
        TestHelper.invokeEP(prodInvokeURL,apiKey);
    }

    @Test(dependsOnMethods = {"invokeEP_ObservabilityAPITestCase"})
    @CitrusTest
    public void waitForObservabilityLogs_ObservabilityAPITestCase() throws Exception {

        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);

        String devReleaseId =  choreoComponent.getReleaseIdForEnvironment(devEnv.getChoreoEnv());
        String prodReleaseId =  choreoComponent.getReleaseIdForEnvironment(prodEnv.getChoreoEnv());
    }


    @Test(dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityAST_ObservabilityAPITestCase(String env) throws IOException,
             ReleaseIdNotFoundException {
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);

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

    @Test(dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityMetricDensity_ObservabilityAPITestCase(String env) throws IOException, ReleaseIdNotFoundException
             {
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);

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

    @Test(dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityMetricDensityHistogram_ObservabilityAPITestCase(String env) throws IOException, ReleaseIdNotFoundException{
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForMetricDensityHistogram.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        queryParams.put("from", Instant.now().minusSeconds(60 * 60).toString());
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

    @Test( dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityStats_ObservabilityAPITestCase(String env) throws IOException, ReleaseIdNotFoundException{
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);
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

    @Test( dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityTraceList_ObservabilityAPITestCase(String env) throws IOException, ReleaseIdNotFoundException {
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);
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

    @Test( dataProvider = "env-provider",dependsOnMethods = {"waitForObservabilityLogs_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityTraceInformation_ObservabilityAPITestCase(String env) throws IOException, ReleaseIdNotFoundException, InterruptedException {
        String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation =
                GraphQL.getComponentObservabilityIdForReleaseId(releaseId,accessToken);
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
