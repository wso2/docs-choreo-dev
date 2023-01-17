package com.wso2.choreo.integration.apis.connectors;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.connectors.Connector;
import com.wso2.choreo.integration.models.response.Response;

import java.io.IOException;

public class ConnectorPublisher extends ControlPlaneAPI {


    public static boolean getConnectorStatus(String componentId, String accessToken) {
        String url = CHOREO_EP + "/user-connector/" + ORG_HANDLE + "/" + componentId + "/status";
        for (int i = 0; i < 20; i++) {
            Response response = HttpClientUtil.httpGET(url, accessToken, "");
            Status status = ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "");
            if (status.getStatus().equals("completed") && status.getConclusion() != null) {
               return  true;
            }
        }

        return  false;
    }

    public static Response publishConnector(String componentId, String apiId, String visibility, String version, String accessToken) throws IOException {
        String url = CHOREO_EP + "/user-connector/" + ORG_HANDLE + "/" + componentId + "/status";
        Connector connector = Connector.builder().connectorVersion(version).visibility(visibility).apiId(apiId).organizationId(ORG_UUID).build();
        return HttpClientUtil.httpPOST(url, ObjectMapperUtil.mapObjectToString(connector), accessToken, "");
    }
}
