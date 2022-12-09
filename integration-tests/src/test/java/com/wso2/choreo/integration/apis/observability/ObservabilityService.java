package com.wso2.choreo.integration.apis.observability;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.config.Constant;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ObservabilityService extends ControlPlaneAPI {

    public static void waitForObsLogs() {

    }


    public static String getObsUrl(String releaseId, String namespace, String obsId) throws URISyntaxException {

        String requestURI = CHOREO_CP_GW_ENDPOINT.concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX)
                .concat(obsId)
                .concat("/logsV2");
        URIBuilder builder = new URIBuilder(requestURI);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        builder.setParameter("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .setParameter("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .setParameter("releaseId", releaseId)
                .setParameter("namespace", namespace)
                .setParameter("sort", "desc")
                .setParameter("limit", "95");

        return builder.build().toString();
    }


}
