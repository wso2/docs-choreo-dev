package com.wso2.choreo.integration.apis.observability;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.choreoproject.AppEnvVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsDownloadStatusCheckException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.ObsRequestParam;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
import com.wso2.choreo.integration.models.observability.SyntaxTree;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringRegularExpression;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;

public class ObservabilityService extends ControlPlaneAPI {

    private static final String timestampRegexMatch = "[\\x00-\\x7F]+";

    public static String getObsUrl(ObsRequestParam obsRequestParam, Constant.logType logType) throws URISyntaxException {

        String requestURI = CHOREO_CP_GW_ENDPOINT.concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX).concat("/").concat(logType.name());
        URIBuilder builder = new URIBuilder(requestURI);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        builder.setParameter("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .setParameter("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .setParameter("releaseId", obsRequestParam.getReleaseId())
                .setParameter("namespace", obsRequestParam.getNamespace())
                .setParameter("sort", obsRequestParam.getSort())
                .setParameter("limit", obsRequestParam.getLimit());

        return builder.build().toString();
    }

    private static String appendQueryParams(String url, ObsRequestParam obsRequestParam) throws URISyntaxException,
            IllegalAccessException {
        URIBuilder builder = new URIBuilder(url);

        List<NameValuePair> params = new ArrayList<>();

        for (Field field : obsRequestParam.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String value = (String) field.get(obsRequestParam);

            if (value != null && !value.isEmpty()) {
                NameValuePair nameValuePair = new BasicNameValuePair(field.getName(), value);
                params.add(nameValuePair);
            }
        }

        builder.setParameters(params);

        return builder.build().toString();
    }

    private static ObsRequestParam getShorterTimeSpanParams(String releaseId, String namespace, String region,
                                                           Constant.logType logType) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        String startTime = fmt.format(currentDateTime.minusDays(1));
        String endTime = fmt.format(currentDateTime.plusMinutes(10));

        ObsRequestParam.ObsRequestParamBuilder builder = ObsRequestParam.builder()
                .startTime(startTime)
                .endTime(endTime)
                .namespace(namespace)
                .releaseId(releaseId)
                .region(region);

        switch (logType) {
            case metricsV2:
                return builder.interval("14").build();
            case groupedlogsV2:
                return builder.limit("5").bin("288").build();
            case logsV2:
                return builder.limit("54").sort("desc").build();
            default:
                throw new IllegalStateException("Unhandled log type `" + logType.name() + "` detected`");
        }
    }

    private static ObsRequestParam getLongerTimeSpanParams(String releaseId, String namespace, String region,
                                                          Constant.logType logType) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        String startTime = fmt.format(currentDateTime.minusMonths(1).minusDays(2));
        String endTime = fmt.format(currentDateTime.plusMinutes(10));

        ObsRequestParam.ObsRequestParamBuilder builder = ObsRequestParam.builder()
                .startTime(startTime)
                .endTime(endTime)
                .namespace(namespace)
                .releaseId(releaseId)
                .region(region);

        switch (logType) {
            case metricsV2:
                return builder.interval("432").build();
            case groupedlogsV2:
                return builder.limit("5").bin("8640").build();
            case logsV2:
                return builder.limit("54").sort("desc").build();
            default:
                throw new IllegalStateException("Unhandled log type `" + logType.name() + "` detected`");
        }
    }

    public static ObservabilityLogs getGroupLogs(String releaseId, String namespace, String accessToken) throws IOException, URISyntaxException {
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);
        ObsRequestParam orp = ObsRequestParam.builder().namespace(namespace).releaseId(releaseId).bin("10").limit("5").build();
        String url = ObservabilityService.getObsUrl(orp,Constant.logType.groupedlogsV2);
        Response res = HttpClientUtil.httpGET(url, accessToken, "");
        System.out.println(res.getRes());
        return ObjectMapperUtil.mapStringToObject(ObservabilityLogs.class, res.getRes(), "");
    }


    private static void verifyMetrics(TestActionRunner runner, HttpClient client, String accessToken,
                                  ObsRequestParam obsRequestParam, Map<String, Object> validationMap) throws Exception {
        String resource = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX + Constant.OBSERVABILITY_METRICS;
        String resourceWithParams = appendQueryParams(resource, obsRequestParam);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                            .client(client)
                            .send()
                            .get(resourceWithParams)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                            .receive()
                            .response(HttpStatus.OK)
                            .message()
                            .body(new ClassPathResource(
                                    "templates/observability/responses/metricsResponseSuccess.json"))
                            .validate(jsonPath().expressions(validationMap))
                        )
                );
    }

    private static void verifyLogs(TestActionRunner runner, HttpClient client, String accessToken,
                                  ObsRequestParam obsRequestParam, Map<String, Object> validationMap) throws Exception {
        String resource = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX + Constant.OBSERVABILITY_LOGS;
        String resourceWithParams = appendQueryParams(resource, obsRequestParam);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                            .client(client)
                            .send()
                            .get(resourceWithParams)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                            .receive()
                            .response(HttpStatus.OK)
                            .message()
                            .body(new ClassPathResource(
                                    "templates/observability/responses/logsResponseSuccess.json"))
                            .validate(jsonPath().expressions(validationMap))
                        )
                );
    }

    private static void verifyZipLogs(String accessToken, ObsRequestParam obsRequestParam, String obsId) throws Exception {
        String requestPath = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT)
                .concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX).concat(Constant.OBSERVABILITY_ZIP_LOGS);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        HttpGet request = new HttpGet(requestPath);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusDays(7)))
                .addParameter("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .addParameter("releaseId", obsRequestParam.getReleaseId())
                .addParameter("namespace", obsRequestParam.getNamespace())
                .build();
        request.setURI(uri);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.OK.value()) {
                throw new ObservabilityLogsDownloadStatusCheckException(statusCode, EntityUtils.toString(response.getEntity()));
            }
            byte[] zipFile = EntityUtils.toByteArray(response.getEntity());
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile))) {
                String initialFileName = "logs-" + obsRequestParam.getReleaseId() + "-1.txt";
                Map<String, String> entries = readZipEntries(zis);
                MatcherAssert.assertThat(entries.size(), greaterThan(0));
                MatcherAssert.assertThat(entries.keySet(), hasItems(initialFileName));
                String content = entries.get(initialFileName);
                MatcherAssert.assertThat(content, containsStringIgnoringCase(obsId));
            }
        }
    }

    private static void verifyGroupLogs(TestActionRunner runner, HttpClient client, String accessToken,
                                  ObsRequestParam obsRequestParam) throws Exception {
        String resource = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX + Constant.OBSERVABILITY_GROUP_LOGS;
        String resourceWithParams = appendQueryParams(resource, obsRequestParam);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(resourceWithParams)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/observability/responses/groupLogsResponseSuccess.json"))
                                .validate(jsonPath()
                                        .expression("$.rows.size()", greaterThan(0))
                                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex(timestampRegexMatch)))
                                )
                        )
                );
    }

    private static Map<String, String> readZipEntries(ZipInputStream zis) throws IOException {
        Map<String, String> entries = new HashMap<>();
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            String filename = zipEntry.getName();
            StringBuilder sb = new StringBuilder();
            byte[] data = new byte[1024];
            int count;
            while ((count = (zis.read(data, 0, 1024))) != -1) {
                sb.append(new String(data, 0, count, StandardCharsets.UTF_8));
            }
            entries.put(filename, sb.toString());
            zis.closeEntry();
        }
        return entries;
    }



    public static void verifyLogsOverShorterDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, String region,
                                                     Map<String, Object> validationMap) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyLogs(runner, client, accessToken, shorterTimeSpanParams, validationMap);
    }

    public static void verifyLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, String region,
                                                    Map<String, Object> validationMap) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyLogs(runner, client, accessToken, longerTimeSpanParams, validationMap);
    }

    public static void verifyZipLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, String region, String obsId) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyZipLogs(accessToken, longerTimeSpanParams, obsId);
    }

    public static void verifyGroupLogsOverShorterDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, String region) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.groupedlogsV2);

        verifyGroupLogs(runner, client, accessToken, shorterTimeSpanParams);
    }

    public static void verifyGroupLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, String region) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.groupedlogsV2);

        verifyGroupLogs(runner, client, accessToken, longerTimeSpanParams);
    }

    public static void verifyMetricsOverShorterDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, String region) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.metricsV2);

        Map<String, Object> validationMap = new HashMap<>();
        validationMap.put("$.rows.size()", greaterThan(0));
        verifyMetrics(runner, client, accessToken, shorterTimeSpanParams, validationMap);
    }

    public static void verifyMetricsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, String region) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.metricsV2);

        Map<String, Object> validationMap = new HashMap<>();
        validationMap.put("$.rows.size()", greaterThan(0));
        verifyMetrics(runner, client, accessToken, longerTimeSpanParams, validationMap);
    }

    public static SyntaxTree verifyObservabilityAST(TestActionRunner runner, HttpClient client, String accessToken,
                                                    List<ObservabilityIdInformation> observabilityIds,
                                                    ChoreoComponent component) throws Exception {
        ObservabilityIdInformation obsInfo = selectObsId(observabilityIds, component);

        Map<String, String> params = new HashMap<>();
        params.put("obsId", obsInfo.getObsId());
        params.put("version", obsInfo.getVerzion());
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForAst.mustache", params);

        AtomicReference<SyntaxTree> syntaxTree = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                                .message()
                                .body(body)
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                        .expression("$.data.ast.__typename", "ast")
                                        .expression("$.data.ast.keySet()", hasItems("__typename", "ast"))
                                        .expression("$.data.ast.ast", stringContainsInOrder(Arrays.asList("packageOrg", "packageName", "packageVersion")))
                                )
                )
        );

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .body(body)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response()
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                    if (code != HttpStatus.OK.value()) {
                            throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                    }
                    JsonParser parser = new JsonParser();
                    String astString = parser.parse(message.getPayload(String.class)).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("ast").get("ast").getAsString();

                    syntaxTree.set(ObjectMapperUtil.mapStringToObject(SyntaxTree.class, astString, ""));
                }));

        return syntaxTree.get();
    }


    public static void verifyObservabilityMetricDensity(TestActionRunner runner, HttpClient client, String accessToken,
                                                    ObservabilityIdInformation observabilityIdInformation) throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("observeId", observabilityIdInformation.getObsId());
        params.put("version", observabilityIdInformation.getVerzion());
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForMetricDensity.mustache", params);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                            .client(client)
                            .send()
                            .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(body)
                            .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                            .client(client)
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
        ));
    }

    public static void verifyObservabilityMetricDensityHistrogram(TestActionRunner runner, HttpClient client, String accessToken,
                                                                  List<ObservabilityIdInformation> observabilityIds,
                                                                  ChoreoComponent component) throws Exception {
        ObservabilityIdInformation obsInfo = selectObsId(observabilityIds, component);

        Map<String, String> params = new HashMap<>();
        params.put("observeId", obsInfo.getObsId());
        params.put("version", obsInfo.getVerzion());
        params.put("from", Instant.now().minusSeconds(60 * 60).toString());
        params.put("to", Instant.now().toString());
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForMetricDensityHistogram.mustache", params);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(body)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
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
                ));
    }

    public static void verifyObservabilityAPI(TestActionRunner runner, HttpClient client, String accessToken,
                                              List<ObservabilityIdInformation> observabilityIds,
                                              ChoreoComponent component, SyntaxTree syntaxTree) throws Exception {
        ObservabilityIdInformation obsInfo = selectObsId(observabilityIds, component);

        String moduleId = syntaxTree.getPackageOrg() + "/" + syntaxTree.getPackageName() + ":" +
              syntaxTree.getPackageVersion();

        Map<String, String> params = new HashMap<>();
        params.put("observeId", obsInfo.getObsId());
        params.put("version", obsInfo.getVerzion());
        params.put("moduleId", moduleId);
        params.put("from", Instant.now().minusSeconds(60 * 60 * 24).toString());
        params.put("to", Instant.now().toString());

        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForObservabilityStats.mustache", params);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(body)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                        .expression("$.data.invocationMetrics.__typename", "invocationMetrics")
                                        .expression("$.data.invocationMetrics.keySet()",
                                                hasItems("__typename", "failedInvocationCounts", "meanInvocationTimes", "successfulInvocationCounts"))
                                        .expression("$.data.invocationMetrics.meanInvocationTimes.size()", greaterThan(0))
                                        .expression("$.data.invocationMetrics.successfulInvocationCounts.size()", greaterThan(0))
                                )
                ));

    }

    public static String verifyObservabilityTraceList(TestActionRunner runner, HttpClient client, String accessToken,
                                                      List<ObservabilityIdInformation> observabilityIds,
                                                      ChoreoComponent component, SyntaxTree syntaxTree,
                                                      int requestCount, String entryPointSvcName,
                                                      String entryPointFuncName) throws Exception {
        ObservabilityIdInformation obsInfo = selectObsId(observabilityIds, component);

        String moduleId = syntaxTree.getPackageOrg() + "/" + syntaxTree.getPackageName() + ":" +
                syntaxTree.getPackageVersion();

        Map<String, String> params = new HashMap<>();
        params.put("observeId",  obsInfo.getObsId());
        params.put("version",  obsInfo.getVerzion());
        params.put("moduleId", moduleId);
        params.put("entryPointFuncModule", moduleId);
        params.put("entryPointSvcName", entryPointSvcName);
        params.put("entryPointFuncName", entryPointFuncName);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        params.put("from", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        params.put("to", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)));

        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForTraceList.mustache", params);

        AtomicReference<String> traceId = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(body)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                        .expression("$.data.requestTraceGroup.keySet()", hasItems("__typename", "traces", "totalCount"))
                                        .expression("$.data.requestTraceGroup.traces[0].keySet()",
                                                hasItems("__typename", "duration", "errorStatus", "httpStatusCode", "startTime", "traceId"))
                                        .expression("$.data.requestTraceGroup.traces.size()", greaterThan(1))
                                        .expression("$.data.requestTraceGroup.totalCount", requestCount)
                                        .expression("$.data.requestTraceGroup.traces[*].httpStatusCode", everyItem(containsString("200")))
                                        .expression("$.data.requestTraceGroup.traces[*].errorStatus", everyItem(containsString("false")))
                                        .expression("$.data.requestTraceGroup.traces[*].traceId", everyItem(is(not(emptyString()))))
                                        .expression("$.data.requestTraceGroup.traces[*].duration", everyItem(greaterThan(1L)))
                                )

                ));

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                .message()
                .body(body)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response()
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                    if (code != HttpStatus.OK.value()) {
                            throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                    }
                    traceId.set(new JsonParser().parse(message.getPayload(String.class))
                            .getAsJsonObject().getAsJsonObject("data")
                            .getAsJsonObject("requestTraceGroup")
                            .getAsJsonArray("traces").get(0).getAsJsonObject()
                            .get("traceId").getAsString());
                }));

        return traceId.get();
    }

    public static void verifyObservabilityTraceInformation(TestActionRunner runner, HttpClient client, String accessToken,
                                                           List<ObservabilityIdInformation> observabilityIds,
                                                           ChoreoComponent component, SyntaxTree syntaxTree,
                                                           String traceId) throws Exception {
        ObservabilityIdInformation obsInfo = selectObsId(observabilityIds, component);

        String moduleId = syntaxTree.getPackageOrg() + "/" + syntaxTree.getPackageName() + ":" +
                syntaxTree.getPackageVersion();

        Map<String, String> params = new HashMap<>();
        params.put("observeId", obsInfo.getObsId());
        params.put("version", obsInfo.getVerzion());
        params.put("moduleId", moduleId);
        params.put("traceId", traceId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        params.put("from", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        params.put("to", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)));

        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForTraceInformation.mustache", params);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(body)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                        .expression("$.data.traceById.__typename", "traceById")
                                        .expression("$.data.traceById.keySet()", hasItems("__typename", "spans"))
                                        .expression("$.data.traceById.spans.size()", greaterThan(0))
                                        .expression("$.data.traceById.spans[0].checkpoints[0].keySet()",
                                                hasItems("__typename", "moduleId", "positionId", "timestamp"))
                                        .expression("$.data.traceById.spans[0].keySet()",
                                                hasItems("__typename", "checkpoints", "duration", "errorMsg", "errorStatus", "httpStatusCode", "position"))
                                        .expression("$.data.traceById.spans[0].checkpoints[*].__typename", everyItem(containsString("checkpoint")))
                                        .expression("$.data.traceById.spans[0].checkpoints[*].moduleId", everyItem(containsString(moduleId)))
                                        .expression("$.data.traceById.spans[0].checkpoints[*].positionId", allOf(is(not(emptyString()))))
                                )
                ));
    }

    private static ObservabilityIdInformation selectObsId(List<ObservabilityIdInformation> observabilityIds, ChoreoComponent component)
            throws NoLatestApiVersionFoundException {
        List<AppEnvVersion> appEnvVersions = component.getLatestApiVersion().getAppEnvVersions();

        Optional<AppEnvVersion> appEnvVersion = appEnvVersions.stream().filter(AppEnvVersion::isDev).findFirst();
        Optional<ObservabilityIdInformation> obsInfo;

        if (appEnvVersion.isPresent()) {
            obsInfo = observabilityIds.stream().
                    filter(o -> o.getReleaseId().equals(appEnvVersion.get().getReleaseId())).findFirst();
        } else {
            throw new IllegalStateException("Prod env does not exist");
        }

        if (obsInfo.isEmpty()) {
            throw new IllegalStateException("Prod env with releaseId " + appEnvVersion.get().getReleaseId() + " does not contain Obs info");
        }

        return obsInfo.get();
    }

}
