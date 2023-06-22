package com.wso2.choreo.integration.apis.balregistry;

import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.balregistry.Package;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;

/**
 * API for the Ballerina Registry.
 */
public class BallerinaRegistry extends ControlPlaneAPI {

    private static final Logger log = LogManager.getLogger(BallerinaRegistry.class);
    private static final int hourInMilliseconds = 60 * 60 * 1000;

    public static void deleteOldConnectors(String accessToken) {
        String url = BAL_REGISTRY_URL + "/packages/" + ORG_HANDLE;

        Response response = HttpClientUtil.httpGET(url, accessToken, "");
        Package[] packkages = ObjectMapperUtil.mapStringToObject(Package[].class,
                response.getRes(), "");

        log.info("Total number of connectors: " + packkages.length);

        int numberOfConnectorsDeleted = 0;
        for (Package aPackage : packkages) {
            String moduleName = aPackage.getName();

            if (shouldConnectorBeDeleted(moduleName)) {
                if (deleteConnector(accessToken, aPackage)) {
                    ++numberOfConnectorsDeleted;
                }
            }
        }

        log.info("Total number of connectors deleted: " + numberOfConnectorsDeleted);
    }

    private static boolean deleteConnector(String accessToken, Package aPackage) {
        String url = BAL_REGISTRY_URL + "/packages/" + aPackage.getOrganization() + "/" + aPackage.getName() + "/" +
                aPackage.getVersion() + "?force=true";

        Response response = HttpClientUtil.httpDELETE(url, accessToken, "");

        if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            return true;
        }

        return false;
    }

    private static boolean shouldConnectorBeDeleted(String moduleName) {
        if (!moduleName.startsWith(Constant.TEST_COMPONENT_NAME)) {
            return false;
        }

        String timeComponent = moduleName.split(Constant.TEST_COMPONENT_NAME)[1];

        timeComponent = timeComponent.replaceAll("\\D", "");

        long createdDateTime = Long.parseLong(timeComponent);
        long currentDateTime = new Date().getTime();

        return currentDateTime - createdDateTime > hourInMilliseconds;
    }


}
