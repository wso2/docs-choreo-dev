package com.wso2.choreo.integration.config;

public final class Constant {
    public static final String BASIC_PREFIX = "Basic ";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_ENDPOINT_SUFFIX = "/oauth2/token";
    public static final String OAUTH_GRANT_TYPE = "client_credentials";
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

    public static long COMPONENT_CREATE_TIMEOUT = 30000;
    public static long COMPONENT_DEPLOY_TIMEOUT = 120000;

    public enum displayType {
        restAPI,
        webhook
    }

    public enum apiLIifCycleState {
        Publish
    }
}
