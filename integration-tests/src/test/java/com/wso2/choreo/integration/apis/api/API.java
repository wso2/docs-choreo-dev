package com.wso2.choreo.integration.apis.api;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;

import javax.xml.transform.sax.SAXResult;
import java.io.IOException;

public class API extends ControlPlaneAPI {

    public static Response changeLifeCycle(String apiId, String action, String accessToken) throws IOException {
        String url = STS_ENDPOINT + "api/am/publisher/v2/apis/change-lifecycle?organizationId=" + ORG_UUID + "&apiId=" + apiId + "&action=" + action;
        return HttpClientUtil.httpPOST(url, "", accessToken, "");
    }

    public static RevisionWrapper getApiRevision(String apiId, String accessToken) {

        String requestURI = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat("/api/am/publisher/v2/apis/")
                .concat(apiId).concat("/revisions?organizationId=")
                .concat(ORG_UUID);

        Response res = HttpClientUtil.httpGET(requestURI, accessToken, "");
        return ObjectMapperUtil.mapStringToObject(RevisionWrapper.class, res.getRes(), "");

    }

}
