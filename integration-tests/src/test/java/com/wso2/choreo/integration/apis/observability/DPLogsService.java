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

import com.consol.citrus.TestActionRunner;
import com.wso2.choreo.integration.apis.DataPlaneSystemAPI;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.TimeRangeISO;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.response.Response;
import static org.hamcrest.Matchers.containsString;
import org.hamcrest.MatcherAssert;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class DPLogsService extends DataPlaneSystemAPI {

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

    public static void getProjectLogs(TestActionRunner runner, String accessToken,
            ChoreoProject choreoProject, ChoreoComponent choreoComponent, Environment environment, Boolean enableLive)
            throws Exception {

        HashMap<String, Object> requestBodyMap = projectLogsRequestBody(environment, choreoProject, choreoComponent);
        String dpHostUrl = CHOREO_EU_DP_URL;
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            dpHostUrl = CHOREO_US_DP_URL;
        }
        Response res = HttpClientUtil.httpPOST(
                dpHostUrl + Constant.DP_LOGS_SUFFIX + "/project/application?live=" + enableLive.toString(),
                ObjectMapperUtil.mapToString(requestBodyMap), accessToken, "");
        MatcherAssert.assertThat(res.toString(), containsString("This is a test log"));
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

    public static void getComponentLogs(TestActionRunner runner, String accessToken, ChoreoProject choreoProject,
            ChoreoComponent component, Environment environment, Boolean enableLive) throws Exception {
        HashMap<String, Object> requestBodyMap = componentLogsRequestBody(environment, component);
        String dpHostUrl;
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            dpHostUrl = CHOREO_US_DP_URL;
        } else {
            dpHostUrl = CHOREO_EU_DP_URL;
        }
        Response res = HttpClientUtil.httpPOST(
                dpHostUrl + Constant.DP_LOGS_SUFFIX + "/component/application?live=" + enableLive.toString(),
                ObjectMapperUtil.mapToString(requestBodyMap), accessToken, "");
        MatcherAssert.assertThat(res.toString(), containsString("This is a test log"));
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

    public static void getGatewayLogs(TestActionRunner runner, String accessToken,
            ChoreoProject choreoProject, ChoreoComponent component, Environment environment, Boolean enableLive)
            throws Exception {

        HashMap<String, Object> requestBodyMap = getGatewayLogsRequestBody(environment, component);
        String dpHostUrl = CHOREO_EU_DP_URL;
        if (Constant.region.US.toString().equals(choreoProject.getRegion().toString())) {
            dpHostUrl = CHOREO_US_DP_URL;
        }
        Response res = HttpClientUtil.httpPOST(
                dpHostUrl + Constant.DP_LOGS_SUFFIX + "/component/gateway?live=" + enableLive.toString(),
                ObjectMapperUtil.mapToString(requestBodyMap), accessToken, "");
        MatcherAssert.assertThat(res.toString(), containsString("200"));
    }

}