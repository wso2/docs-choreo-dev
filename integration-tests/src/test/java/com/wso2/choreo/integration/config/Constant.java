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

    public static final String GRAPHQL_ENDPOINT_SUFFIX = "/projects/1.0.0/graphql";
    public static final String TEST_PROJECT_NAME_PREFIX = "automationtestproject";
    public static final String TEST_OLD_PROJECT_NAME_PREFIX = "testproject"; // Prefix used to create tests earlier before update the format to TEST_PROJECT_NAME_PREFIX

    public static final String TEST_OLD_COMPONENT_NAME = "automationtestcomponent";
    public static final String TEST_COMPONENT_NAME = "autotest";
    public static final String TEST_CONNECTION_NAME = "autotestconnection";
    public static final String TEST_CONFIG_GROUP_NAME = "autotestconfiggroup";

    public static final String TEST_PROJECT_DESCRIPTION = "test project description";
    public static final String TEST_REPO_NAME_PREFIX = "test-repo-";
    public static final String GITHUB_AUTH_HEADER_PREFIX = "token ";

    public static final String DEV_ENVIRONMENT = "dev";
    public static final String PROD_ENVIRONMENT = "prod";

    public static final String INVOKE_URL = "invokeUrl";

    public static final String API_ID = "apiId";

    public static final String USER_CONNECTORS_ENDPOINT_SUFFIX = "/user-connectors";
    public static final String GITHUB_URL = "https://github.com/";

    public static final String X_CORRELATION_UUID = UUID.randomUUID().toString();
    public static final String TEST_CONNECTOR_VISIBILITY = "private";
    public static final String TEST_CONNECTOR_VERSION = "1.0.0";
    // API Proxy related constants
    public static final String DEFAULT_API_NAME = "DefaultAPI";
    public static final String DEFAULT_VERSION = "v1.0";
    public static final String DEFAULT_ENDPOINT = "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/users/endpoint-9090-803/v1.0";
    public static final String APPLICATION_JSON = "application/json";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static final String ID = "id";
    public static final String API_VALIDATE_ENDPOINT = "/api/am/publisher/v2/apis/validate";
    public static final String APIS_ENDPOINT = "/api/am/publisher/v2/apis";
    public static final String GENERATE_KEY_ENDPOINT_SUFFIX = "/generate-key";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String QUERY = "query";
    public static long COMPONENT_CREATE_TIMEOUT = 30000;
    public static long COMPONENT_DEPLOY_TIMEOUT_SECONDS = 180;

    public static final String INSIGHTS_API_RESOURCE = "/insights/1.0.0/query-api";
    public static final String INSIGHTS_AUTH_API_RESOURCE = "/auth/v1/token";

    public static final String INSIGHTS_TRAFFIC_ALERT_API_RESOURCE = "/insightsalert/1.0.0/trafficConfigs";
    public static final String INSIGHTS_LATENCY_ALERT_API_RESOURCE = "/insightsalert/1.0.0/latencyConfigs";
    public static final int MAX_API_REVISIONS_LIMIT_DEPLOYMENTS = 20;
    public static final int MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE = 19;
    public static final int REVISION_COUNT_BEFORE_DELETION = 19;
    public static final int REVISION_COUNT_AFTER_DELETION = 18;
    public static final int REVISION_COUNT_AFTER_BACKUP_DELETION = 19;
    public static final int INDEX_OF_REVISION_TO_DELETE = 0;
    public static final int INDEX_OF_DEPLOYED_REVISION = MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE - 1;


    public static final String NON_EMPTY_REPO_TYPE = "UserManagedNonEmpty";


    public enum region{

        EU,
        US
    }



    public enum  logType{
        groupedlogsV2,
        logsV2,
        metricsV2
    }
    public enum displayType {
        restAPI,
        byocService,
        proxy,
        webhook,
        graphql,
        ballerinaService,
        manualTrigger,
        buildpackService,
        byoiService,
        byoiWebApp,
        prismMockService,
        externalConsumer
    }
    public enum apiLIifCycleState {
        Publish
    }

    public enum Environment {
        Development,
        Production
    }

    public enum AppType{
        MI_API_SERVICE("miApiService"),
        MI_EVENT_HANDLER("miEventHandler");

        public final String value;
        AppType(String value) {
            this.value = value;
        }
    }

    public enum EndpointVisibility{
        PUBLIC("Public"),
        Project("Project"),
        ORGANIZATION("Organization");

        public final String value;
        EndpointVisibility(String value) {
            this.value = value;
        }
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

    // Observability related constants
    public static final String OBSERVABILITY_LOGS_ENDPOINT_SUFFIX = "/observability/logging/0.1.0/applications/loggingAPI";
    public static final String OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX = "/observability/system/0.1.0/applications/sysObsAPI";
    public static final String OBSERVABILITY_OBS_ENDPOINT_SUFFIX = "/observability/application/0.1.0";

    public static final String DP_OBSERVABILITY_ENDPOINT_SUFFIX  = "/choreoobsapi/0.2.0";

    public static final String OBSERVABILITY_METRICS = "/metricsV2";

    public static final String OBSERVABILITY_LOGS = "/logsV2";

    public static final String OBSERVABILITY_ZIP_LOGS = "/logsV2/zip";

    public static final String OBSERVABILITY_GROUP_LOGS = "/groupedlogsV2";

    // Theme management constants
    public static final String THEME_ENDPOINT_SUFFIX = "/org-mgt/1.0.0/orgs/";

    // Observability related constants
    public static final String OBSERVABILITY_ORG_LOGS = "/orgLogs";

    public static final String OBSERVABILITY_PROJECT_LOGS = "/projectLogs";

    public static final String OBSERVABILITY_AUDIT_LOGS = "/audit-logging/v1.0";

    public static final String OBSERVABILITY_GATEWAY_ACCESS_LOGS = "/gatewayAccessLogs";

    // DevOps related constants
    private static final String DEVOPS_API_CONTEXT = "/devops/1.0.0/api/v1";
    public static final String DEVOPS_CI = DEVOPS_API_CONTEXT + "/ci";
    public static final String DEVOPS_CLUSTERS = DEVOPS_API_CONTEXT + "/clusters";
    public static final String DEVOPS_INTEGRATION = DEVOPS_API_CONTEXT + "/components/integration";
    public static final String DEVOPS_ENVIRONMENTS = DEVOPS_API_CONTEXT + "/environments/";
    public static final String DEVOPS_METRICS = DEVOPS_API_CONTEXT + "/metrics/";
    public static final String DEVOPS_ORGANIZATIONS = DEVOPS_API_CONTEXT + "/organizations/";
    public static final String DEVOPS_VOLUME = DEVOPS_API_CONTEXT + "/volume/";
    public static final String DEVOPS_COMPONENTS_API = DEVOPS_API_CONTEXT + "/components/";
    public static final String DEVOPS_BUILDPACKS = DEVOPS_API_CONTEXT + "/buildpacks";

    // Devportal related constants
    public static final String DEVPORTAL_ENDPOINT_SUFFIX = "/api/am/devportal/v2";
    public static final String DEVPORTAL_APPLICATIONS = "/applications";

    // Delivery Insights related constants
    public static final String CIO_INCIDENT_CONFIGURATOR = "/cio-incident-configurator/1.0.0";
    public static final String CIO_QUERY_API = "/cio-query-api/1.0.0/query";

    // Component Management related constants
    public static final String COMPONENT_MGT_SUFFIX = "/projects/1.0.0/graphql";

    // Org Managemnt related constants
    public static final String ORG_MGT_SUFFIX = "/org-mgt/1.0.0/orgs/";
    public static final String DEFAULT_THEME = "/themes/default";
    public static final String ORG_API_SUFFIX = "/orgs/1.0.0/orgs/";
    public static final String USER_MGT_SUFFIX = "/user-mgt/1.0.0/orgs/";

    // Config management related constants
    public static final String CONF_MGT_SUFFIX = "/config-mgt/1.0.0/orgs/";

    // Billing related constants
    public static final String X_CLOUD_TYPE = "X-Cloud-Type";

    // Insights related constants
    public static final String INSIGHTS_SUFFIX = "/insights/1.0.0/query-api";
    public static final String PROJECTS_GRAPHQL = "/projects/1.0.0/graphql";

    // APIM related constants
    public static final String PUBLISHER_URL = "/api/am/publisher/v2";
    public static final String PROXY_DEPLOYER_URL = "/proxy/deployer/v1";

    // Data plane Logs
    public static final String DP_LOGS_SUFFIX = "/choreologgingapi/0.2.0/logs";
    public static final String SYSTEM_OBS_SUFFIX = "/choreosysobsapi/0.2.0/system-metrics/component/application";

    // Platform Services Manager related constants
    public static final String PSM_SUFFIX = "platform-services/v1.0/db-servers";
    public static final String MYSQL_SERVICE_PLAN_ID = "84e044ae-81bc-429c-badf-a0945a606124";
    public static final String REDIS_SERVICE_PLAN_ID = "fdeee256-09ac-425c-9250-883508f8dfcb";

}
