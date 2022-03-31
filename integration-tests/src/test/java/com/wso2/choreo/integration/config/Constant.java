/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.config;

import java.util.UUID;

public final class Constant {
    public static final String BASIC_PREFIX = "Basic ";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_ENDPOINT_SUFFIX = "/oauth2/token";
    public static final String OAUTH_PASSWORD_GRANT_TYPE = "password";
    public static final String OAUTH_TOKEN_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String SUBJECT_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";
    public static final String REQUESTED_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";
    public static final String OAUTH_SCOPES = "apim:api_manage apim:subscription_manage apim:tier_manage apim:admin " +
            "apim:publisher_settings environments:view_prod environments:view_dev";

    public static final String GRAPHQL_ENDPOINT_SUFFIX = "/graphql";
    public static final String TEST_PROJECT_NAME_PREFIX = "testproject";
    public static final String TEST_COMPONENT_NAME = "testcomponent";
    public static final String TEST_PROJECT_DESCRIPTION = "test project description";

    public static final String DEV_ENVIRONMENT = "dev";

    public static final String USER_CONNECTORS_ENDPOINT_SUFFIX = "/user-connectors";
    public static final String TEST_CONNECTOR_VISIBILITY = "public";
    public static final String TEST_CONNECTOR_VERSION = "1.0.0";
    // API Proxy related constants
    public static final String DEFAULT_API_NAME = "DefaultAPI";
    public static final String DEFAULT_VERSION = "1.0.0";
    public static final String DEFAULT_ENDPOINT = "http://run.mocky.io/v2/5185415ba171ea3a00704eed";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ID = "id";
    public static final String API_VALIDATE_ENDPOINT = "/api/am/publisher/v2/apis/validate";
    public static final String APIS_ENDPOINT = "/api/am/publisher/v2/apis";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String QUERY = "query";
    public static long COMPONENT_CREATE_TIMEOUT = 30000;
    public static long COMPONENT_DEPLOY_TIMEOUT = 120000;

    public static final String INSIGHTS_API_RESOURCE = "/insights/1.0.0/query-api";
    public static final String INSIGHTS_TRAFFIC_ALERT_API_RESOURCE = "/insightsalert/1.0.0/trafficConfigs";
    public static final String INSIGHTS_LATENCY_ALERT_API_RESOURCE = "/insightsalert/1.0.0/latencyConfigs";
    public enum displayType {
        restAPI,
        proxy,
        webhook
    }
    public enum apiLIifCycleState {
        Publish
    }

    // Alert related const
    public static final class ALERT {
        public static final String NOTIFICATION_SERVICE_RESOURCE = "/notification-service/1.0.0/publishAlerts";
        public static final String ENV_ID = UUID.randomUUID().toString();
        public static final String CONTAINER_ID = UUID.randomUUID().toString();
        public static final String MAIL_IMAP_HOST = "imap.gmail.com";
        public static final int MAIL_IMAP_PORT = 993;
        public static final String MAIL_IMAP_USER = "choreoalert@gmail.com";
    }
}
