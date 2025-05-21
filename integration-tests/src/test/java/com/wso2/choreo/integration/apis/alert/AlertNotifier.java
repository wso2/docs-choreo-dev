package com.wso2.choreo.integration.apis.alert;

import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.alert.Alert;
import com.wso2.choreo.integration.models.alert.AlertResponse;
import com.wso2.choreo.integration.models.alert.MetaData;
import com.wso2.choreo.integration.models.alert.Properties;


import java.io.IOException;
import java.time.Instant;



public class AlertNotifier {
    private static final String CP_ENDPOINT = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT) + "/" + Constant.ALERT.NOTIFICATION_SERVICE_RESOURCE;


    public static AlertResponse triggerImmediateAlert(String appName ,String accessToken) throws IOException {

        MetaData metaData = MetaData.builder().
                componentName(appName).
                envName(Constant.ALERT.ENV_ID).
                containerId(Constant.ALERT.CONTAINER_ID).
                releaseId(Configuration.getConfig(ConfigDefinition.ALERT_RELEASE_ID)).
                alertType("Out Of Memory error").build();

        Alert alert = Alert.builder().
                orgId(Configuration.getConfig(ConfigDefinition.ALERT_ORG_UUID)).
                envId(Constant.ALERT.ENV_ID).
                publisher("Critical alert detector").
                time(Instant.now().toString()).
                severity("High").
                metaData(metaData).
                properties(Properties.builder().build()).build();

        String request = ObjectMapperUtil.mapObjectToString(alert);
        Response response = HttpClientUtil.httpPOST(CP_ENDPOINT, request, accessToken, "");

        return ObjectMapperUtil.mapStringToObject(AlertResponse.class, response.getRes(), "");
    }


}
