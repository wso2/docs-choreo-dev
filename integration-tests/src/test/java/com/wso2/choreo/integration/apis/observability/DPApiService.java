/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.observability;

import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.DataPlaneSystemAPI;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.TimeRangeISO;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.container.RepeatUntilTrue.Builder.repeat;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.contains;

public class DPApiService extends DataPlaneSystemAPI {

    private static TimeRangeISO getTimeRangeISO() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        String startTime = fmt.format(currentDateTime.minusMinutes(100));
        String endTime = fmt.format(currentDateTime);
        return TimeRangeISO.builder().startTime(startTime).endTime(endTime).build();
    }

    private static HashMap<String, Object> projectLogsRequestBody(Environment environment, ChoreoProject choreoProject,
            ChoreoComponent choreoComponent) {
        TimeRangeISO timeRangeISO = getTimeRangeISO();
        String[] componentList = new String[] { choreoComponent.getId() };
        return new HashMap<>() {
            {
                put("environmentId", environment.getId());
                put("projectId", choreoProject.getId());
                put("namespace", environment.getNamespace());
                put("startTime", timeRangeISO.getStartTime());
                put("componentIdList", componentList);
                put("endTime", timeRangeISO.getEndTime());
                put("limit", 100);
                put("sort", "desc");
            }
        };
    }

    public static void getProjectLogs(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, 
        String accessToken, ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, 
        Boolean enableLive) throws Exception {
        HashMap<String, Object> requestBodyMap = projectLogsRequestBody(environment, choreoProject, choreoComponent);
        HttpClient client = citrusClients.get(Endpoints.CHOREO_EU_DP_URL);
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            client = citrusClients.get(Endpoints.CHOREO_US_DP_URL);
        }

        String path = Constant.DP_LOGS_SUFFIX + "/project/application?live=" + enableLive.toString();
        final String requestBody = ObjectMapperUtil.mapToString(requestBodyMap);
        AtomicInteger successiveFailureCount = new AtomicInteger(0);
        runner.variable("isProjectRetrievalSuccess", false);
        runner.$(repeat()
            .until("(i = 5) or ( ${isProjectRetrievalSuccess} = true )")
            .index("i")
            .actions(
                http()
                    .client(client)
                    .send()
                    .post(path)
                    .message()
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                http().client(client)
                    .receive()
                    .response()
                    .message()
                    .type(MessageType.JSON)
                    .validate((message, context) -> {
                        int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                        String expectedLog = "This is a test log";
                        if (code == HttpStatus.OK.value() && message.getPayload(String.class)
                            .contains(expectedLog)) {
                            successiveFailureCount.set(0);
                            context.setVariable("isProjectRetrievalSuccess", true);
                        } else {
                            if (5 < successiveFailureCount.incrementAndGet()) {
                                throw new ValidationException("Too many successive calls with response code != 200");
                            }
                            SleepUtil.sleep(30);
                        }
                    }
                )
            )
        );
    }

    private static HashMap<String, Object> componentLogsRequestBody(Environment environment,
            ChoreoComponent component) {
        TimeRangeISO timeRangeISO = getTimeRangeISO();
        return new HashMap<>() {
            {
                put("environmentId", environment.getId());
                put("componentId", component.getId());
                put("namespace", environment.getNamespace());
                put("searchPhrase", "");
                put("startTime", timeRangeISO.getStartTime());
                put("endTime", timeRangeISO.getEndTime());
                put("limit", 100);
                put("sort", "desc");
            }
        };
    }

    public static void getSystemMetrics(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, 
        String accessToken, ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, 
        Boolean enableLive) throws IOException, NoLatestApiVersionFoundException {
         HttpClient client = citrusClients.get(Endpoints.CHOREO_US_DP_URL);
        if (Constant.region.EU.toString().equals(choreoProject.getRegion().toString())) {
            client = citrusClients.get(Endpoints.CHOREO_EU_DP_URL);
        }
         String releaseId = choreoComponent.getReleaseIdForEnvironment(environment);
            String namespace = environment.getNamespace();
        

            String requestPath = Constant.SYSTEM_OBS_SUFFIX;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            runner.$(repeatOnError()
                    .until("i = 4")
                    .index("i")
                    .autoSleep(30000)
                    .actions(
                            http()
                                    .client(client)
                                    .send()
                                    .get(requestPath)
                                    .message()
                                    .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC)
                                            .truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                                    .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC)
                                            .truncatedTo(ChronoUnit.SECONDS).plusSeconds(10 * 60)))
                                    .queryParam("interval", "14")
                                    .queryParam("region", "US")
                                    .queryParam("releaseId", releaseId)
                                    .queryParam("namespace", namespace)
                                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                            http()
                                    .client(client)
                                    .receive()
                                    .response(HttpStatus.OK)
                                    .message()
                                    .type(MessageType.JSON)
                                    .validate(jsonPath()
                                    .expression("$.columns[*].name", 
                                        contains("cpu", "memory", "cpuPercentage", "memoryPercentage", "TimeGenerated"))
                                   )
                    )
            );
    }
    public static void getComponentLogs(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, 
        String accessToken,ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, 
        Boolean enableLive) throws Exception {
        HashMap<String, Object> requestBodyMap = componentLogsRequestBody(environment, choreoComponent);
        HttpClient client = citrusClients.get(Endpoints.CHOREO_EU_DP_URL);
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            client = citrusClients.get(Endpoints.CHOREO_US_DP_URL);
        }

        String path = Constant.DP_LOGS_SUFFIX + "/component/application?live=" + enableLive.toString();
        final String requestBody = ObjectMapperUtil.mapToString(requestBodyMap);
        AtomicInteger successiveFailureCount = new AtomicInteger(0);
        runner.variable("isComponentLogsRetrievalSuccess", false);
        runner.$(repeatOnError()
            .until("(i = 5) or ( ${isComponentLogsRetrievalSuccess} = true )")
            .index("i")
            .actions(
                http()
                    .client(client)
                    .send()
                    .post(path)
                    .message()
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                http().client(client)
                    .receive()
                    .response()
                    .message()
                    .type(MessageType.JSON)
                    .validate((message, context) -> {
                        int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                        String expectedLog = "This is a test log";
                        if (code == HttpStatus.OK.value() && message.getPayload(String.class)
                            .contains(expectedLog)) {
                            successiveFailureCount.set(0);
                            context.setVariable("isComponentLogsRetrievalSuccess", true);
                        } else {
                            if (5 < successiveFailureCount.incrementAndGet()) {
                                throw new ValidationException("Too many successive calls with response code != 200");
                            }
                            SleepUtil.sleep(30);
                        }
                    }
                )
            )
        );
    }

    private static HashMap<String, Object> getGatewayLogsRequestBody(Environment environment,
            ChoreoComponent component) {
        TimeRangeISO timeRangeISO = getTimeRangeISO();
        return new HashMap<>() {
            {
                put("environmentId", environment.getId());
                put("componentId", component.getId());
                put("namespace", environment.getNamespace());
                put("searchPhrase", "");
                put("startTime", timeRangeISO.getStartTime());
                put("endTime", timeRangeISO.getEndTime());
                put("logType", "singleLine");
                put("limit", 100);
                put("sort", "desc");
            }
        };
    }

    public static void getGatewayLogs(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, 
        String accessToken, ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, 
        Boolean enableLive) throws Exception {
        HashMap<String, Object> requestBodyMap = getGatewayLogsRequestBody(environment, choreoComponent);
        HttpClient client = citrusClients.get(Endpoints.CHOREO_EU_DP_URL);
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            client = citrusClients.get(Endpoints.CHOREO_US_DP_URL);
        }

        String path = Constant.DP_LOGS_SUFFIX + "/component/gateway?live=" + enableLive.toString();
        final String requestBody = ObjectMapperUtil.mapToString(requestBodyMap);
        runner.variable("isGatewayLogsRetrievalSuccess", false);
        AtomicInteger successiveFailureCount = new AtomicInteger(0);
        runner.$(repeat()
            .until("(i = 5) or ( ${isGatewayLogsRetrievalSuccess} = true )")
            .index("i")
            .actions(
                http()
                    .client(client)
                    .send()
                    .post(path)
                    .message()
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                http().client(client)
                    .receive()
                    .response()
                    .message()
                    .type(MessageType.JSON)
                    .validate((message, context) -> {
                        int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                        if (code == HttpStatus.OK.value()) {
                            successiveFailureCount.set(0);
                            context.setVariable("isGatewayLogsRetrievalSuccess", true);
                        } else {
                            if (5 < successiveFailureCount.incrementAndGet()) {
                                throw new ValidationException("Too many successive calls with response code != 200");
                            }
                            SleepUtil.sleep(30);
                        }
                    }
                )
            )
        );
    }

    private static HashMap<String, String> getProjectMetricsBody(Environment environment, ChoreoProject choreoProject) {
        TimeRangeISO timeRangeISO = getTimeRangeISO();
        
        return new HashMap<>() {
            {
                put("environmentId", environment.getId());
                put("projectId", choreoProject.getId());
                put("fromTime", timeRangeISO.getStartTime());
                put("toTime", timeRangeISO.getEndTime());
            }
        };
    }
    public static void getProjectMetrics(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, 
        String accessToken, ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, 
        Boolean enableLive) throws IOException {
        HashMap<String, String> params = getProjectMetricsBody(environment, choreoProject);
        String body = MessageUtils.
        generateStringFromTemplate("templates/observability/graphql/queryForProjectDiagram.mustache", params);
         HttpClient client = citrusClients.get(Endpoints.CHOREO_US_DP_URL);
        if (Constant.region.EU.toString().equals(choreoProject.getRegion().toString())) {
            client = citrusClients.get(Endpoints.CHOREO_EU_DP_URL);
        }
        runner.variable("isMetricsRecievedSuccess", false);
        AtomicInteger successiveFailureCount = new AtomicInteger(0);
        runner.$(repeat().until("(i = 5) or ( ${isMetricsRecievedSuccess} = true )")
        .index("i")
        .actions(
                http()
                        .client(client)
                        .send()
                        .post(Constant.DP_OBSERVABILITY_ENDPOINT_SUFFIX)
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
                        .validate(((message, context) -> {
                            JsonArray linkList = new JsonParser().parse((String) message.getPayload())
                                    .getAsJsonObject()
                                    .getAsJsonObject("data").getAsJsonObject("hubbleProjectDiagram").getAsJsonArray("linkList");
                            JsonArray nodeList = new JsonParser().parse((String) message.getPayload())
                                    .getAsJsonObject()
                                    .getAsJsonObject("data").getAsJsonObject("hubbleProjectDiagram").getAsJsonArray("nodeList");
                             if(linkList.size() > 0 && nodeList.size() > 0) {
                                    successiveFailureCount.set(0);
                                    context.setVariable("isMetricsRecievedSuccess", true);
                                } else {
                                    if (5 < successiveFailureCount.incrementAndGet()) {
                                        throw new ValidationException("Did not recived the metrics data");
                                    }
                                    SleepUtil.sleep(30);
                                }
                                })
                               
                               
                        )
        ));

    }
}
