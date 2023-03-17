package com.wso2.choreo.integration.apis.observability;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.ObsRequestParam;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ObservabilityService extends ControlPlaneAPI {

    public static void waitForObsLogs() {

    }

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


    public static ObservabilityLogs getGroupLogs(String releaseId, String namespace, String accessToken) throws IOException, URISyntaxException {
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);
        ObsRequestParam orp = ObsRequestParam.builder().namespace(namespace).releaseId(releaseId).bin("10").limit("5").build();
        String url = ObservabilityService.getObsUrl(orp,Constant.logType.groupedlogsV2);
        Response res = HttpClientUtil.httpGET(url, accessToken, "");
        System.out.println(res.getRes());
        return ObjectMapperUtil.mapStringToObject(ObservabilityLogs.class, res.getRes(), "");
    }


}
