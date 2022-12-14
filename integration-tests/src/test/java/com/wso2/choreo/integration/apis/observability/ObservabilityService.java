package com.wso2.choreo.integration.apis.observability;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.ObsRequestParam;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ObservabilityService extends ControlPlaneAPI {

    public static void waitForObsLogs() {

    }

    public static String getObsUrl(ObsRequestParam obsRequestParam, String obsId, Constant.logType logType) throws URISyntaxException {

        String requestURI = CHOREO_CP_GW_ENDPOINT.concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX).concat(obsId).concat("/").concat(logType.name());
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


}
