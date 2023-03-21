package com.wso2.choreo.integration.apis.observability;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsDownloadStatusCheckException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.ObsRequestParam;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;

public class ObservabilityService extends ControlPlaneAPI {

    private static final String timestampRegexMatch = "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}Z|\\d{2}.\\d{2}Z|\\d{2}.\\d{3}Z|\\d{2}.\\d{4}Z|\\d{2}.\\d{5}Z|\\d{2}.\\d{6}Z|\\d{2}.\\d{7}Z)";

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

    private static ObsRequestParam getShorterTimeSpanParams(String releaseId, String namespace, Constant.region region,
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
                .region(region.name());

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

    private static ObsRequestParam getLongerTimeSpanParams(String releaseId, String namespace, Constant.region region,
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
                .region(region.name());

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
                                                     String releaseId, String namespace, Constant.region region,
                                                     Map<String, Object> validationMap) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyLogs(runner, client, accessToken, shorterTimeSpanParams, validationMap);
    }

    public static void verifyLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, Constant.region region,
                                                    Map<String, Object> validationMap) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyLogs(runner, client, accessToken, longerTimeSpanParams, validationMap);
    }

    public static void verifyZipLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, Constant.region region, String obsId) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.logsV2);

        verifyZipLogs(accessToken, longerTimeSpanParams, obsId);
    }

    public static void verifyGroupLogsOverShorterDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, Constant.region region) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.groupedlogsV2);

        verifyGroupLogs(runner, client, accessToken, shorterTimeSpanParams);
    }

    public static void verifyGroupLogsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, Constant.region region) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.groupedlogsV2);

        verifyGroupLogs(runner, client, accessToken, longerTimeSpanParams);
    }

    public static void verifyMetricsOverShorterDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                     String releaseId, String namespace, Constant.region region) throws Exception {

        ObsRequestParam shorterTimeSpanParams = getShorterTimeSpanParams(releaseId, namespace, region, Constant.logType.metricsV2);

        Map<String, Object> validationMap = new HashMap<>();
        validationMap.put("$.rows.size()", greaterThan(0));
        verifyMetrics(runner, client, accessToken, shorterTimeSpanParams, validationMap);
    }

    public static void verifyMetricsOverLongerDuration(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String releaseId, String namespace, Constant.region region) throws Exception {

        ObsRequestParam longerTimeSpanParams = getLongerTimeSpanParams(releaseId, namespace, region, Constant.logType.metricsV2);

        Map<String, Object> validationMap = new HashMap<>();
        validationMap.put("$.rows.size()", greaterThan(0));
        verifyMetrics(runner, client, accessToken, longerTimeSpanParams, validationMap);
    }
}
