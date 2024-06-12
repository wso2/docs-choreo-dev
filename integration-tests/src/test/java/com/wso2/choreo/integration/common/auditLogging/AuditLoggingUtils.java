/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common.auditLogging;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.observability.AuditLogsService;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.TimeRangeISO;
import com.wso2.choreo.integration.models.auditLogging.AuditLogList;
import com.wso2.choreo.integration.models.auditLogging.AuditLogRetrievalRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class AuditLoggingUtils {

    public static AuditLogList filterAuditLogsByOutcome(TestActionRunner runner,
                                                        Map<Endpoints, HttpClient> citrusClients, String orgUuid,
                                                        List<String> outcomes)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        TimeRangeISO timeRangeISO = getTimeRangeISO(600);

        AuditLogRetrievalRequest auditLogRetrievalRequest = new AuditLogRetrievalRequest();
        auditLogRetrievalRequest.setStartTime(timeRangeISO.getStartTime());
        auditLogRetrievalRequest.setEndTime(timeRangeISO.getEndTime());
        auditLogRetrievalRequest.setLimit(100);
        auditLogRetrievalRequest.setSort("desc");
        auditLogRetrievalRequest.setOutcomes(outcomes);

        return AuditLogsService.getAuditLogs(runner, choreoCPTestClient, orgUuid, auditLogRetrievalRequest);
    }

    public static AuditLogList filterAuditLogsByUser(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                     String orgUuid, List<String> userIdpIds)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        TimeRangeISO timeRangeISO = getTimeRangeISO(600);

        AuditLogRetrievalRequest auditLogRetrievalRequest = new AuditLogRetrievalRequest();
        auditLogRetrievalRequest.setStartTime(timeRangeISO.getStartTime());
        auditLogRetrievalRequest.setEndTime(timeRangeISO.getEndTime());
        auditLogRetrievalRequest.setLimit(100);
        auditLogRetrievalRequest.setSort("desc");
        auditLogRetrievalRequest.setUserIdpIds(userIdpIds);

        return AuditLogsService.getAuditLogs(runner, choreoCPTestClient, orgUuid, auditLogRetrievalRequest);
    }

    public static AuditLogList filterAuditLogsByProject(TestActionRunner runner,
                                                        Map<Endpoints, HttpClient> citrusClients, String orgUuid,
                                                        List<String> projectIds)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        TimeRangeISO timeRangeISO = getTimeRangeISO(600);

        AuditLogRetrievalRequest auditLogRetrievalRequest = new AuditLogRetrievalRequest();
        auditLogRetrievalRequest.setStartTime(timeRangeISO.getStartTime());
        auditLogRetrievalRequest.setEndTime(timeRangeISO.getEndTime());
        auditLogRetrievalRequest.setLimit(100);
        auditLogRetrievalRequest.setSort("desc");
        auditLogRetrievalRequest.setProjectIds(projectIds);

        return AuditLogsService.getAuditLogs(runner, choreoCPTestClient, orgUuid, auditLogRetrievalRequest);
    }

    public static AuditLogList filterAuditLogsByTime(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                     String orgUuid, TimeRangeISO timeRangeISO)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        AuditLogRetrievalRequest auditLogRetrievalRequest = new AuditLogRetrievalRequest();
        auditLogRetrievalRequest.setStartTime(timeRangeISO.getStartTime());
        auditLogRetrievalRequest.setEndTime(timeRangeISO.getEndTime());
        auditLogRetrievalRequest.setLimit(100);
        auditLogRetrievalRequest.setSort("desc");

        return AuditLogsService.getAuditLogs(runner, choreoCPTestClient, orgUuid, auditLogRetrievalRequest);
    }

    public static TimeRangeISO getTimeRangeISO(long durationInMinutes) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        String startTime = fmt.format(currentDateTime.minusMinutes(durationInMinutes));
        String endTime = fmt.format(currentDateTime);
        return TimeRangeISO.builder().startTime(startTime).endTime(endTime).build();
    }
}
