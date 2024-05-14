package com.wso2.choreo.integration.common.managedAuthentication;

import java.util.List;


public class ManagedAuthenticationConstants {

    public static class Project {

        public static final String REGION = "US";
    }
    
    public static class WebAppRepo {

        public static final String URL = "https://github.com/choreo-test-apps/choreo-samples";
        public static final String DOCKER_CONTEXT = "reading-list-app/reading-list-front-end-with-managed-auth";
    }

    public static class WebAppConfig {

        public static final String WEB_APP_TYPE = "React";
        public static final String BUILD_COMMAND = "npm install && npm run build";
        public static final String PACKAGE_MANAGER_VERSION = "18";
        public static final String OUTPUT_DIRECTORY = "dist";
    }

    public static class KeyGenRequestKeys {

        public static final String APP_TOKEN_EXPIRY = "appTokenExpiry";
        public static final String CALLBACK_URLS = "callbackUrls";
        public static final String GRANT_TYPES = "grantTypes";
        public static final String PKCE_MANDATORY = "pkceMandatory";
        public static final String REFRESH_TOKEN_EXPIRY = "refreshTokenExpiry";
        public static final String USER_TOKEN_EXPIRY = "userTokenExpiry";
        public static final String PUBLIC_CLIENT = "publicClient";
    }

    public static class DefaultKeyGenRequest {

        public static final int APP_TOKEN_EXPIRY = 1440;
        public static final int REFRESH_TOKEN_EXPIRY = 10080;
        public static final int USER_TOKEN_EXPIRY = 1440;
        public static final List<String> GRANT_TYPES = List.of("authorization_code", "refresh_token");
        public static final boolean PKCE_MANDATORY = false;
        public static final boolean PUBLIC_CLIENT = false;
    }

    public static class ConfigGroupNames {

        public static final String MANAGED_AUTH_CONFIG_GROUP_NAME = "app-gw-auth-configs";
        public static final String KEY_SET_CONFIG_GROUP_NAME = "app-gw-key-sets";
    }

    public static class ManagedAuthConfigKeys {

        public static final String POST_LOGIN_PATH = "postLoginPath";
        public static final String POST_LOGOUT_PATH = "postLogoutPath";
        public static final String SCOPES = "scopes";
        public static final String SESSION_EXPIRY_TIME = "sessionExpiryTime";
        public static final String IS_APP_GATEWAY_CONFIGURED = "isAppGatewayConfigured";
    }

    public static class DefaultManagedAuthConfigValues {

        public static final String POST_LOGIN_PATH = "/";
        public static final String POST_LOGOUT_PATH = "/";
        public static final String SCOPES = "[]";
        public static final String SESSION_EXPIRY_TIME = "10800";
        public static final String IS_APP_GATEWAY_CONFIGURED = "true";
    }

    public static class KeySetConfigKeys {

        public static final String CLIENT_ID = "clientId";
    }

    public static class CallbackUrlFormats {

        public static final String LOGIN_CALLBACK_URL = "https://%s.%s/auth/login/callback";
        public static final String LOGOUT_CALLBACK_URL = "https://%s.%s/auth/logout/callback";
    }

    public static class Subdomains {

        public static final String CHOREO_APPS = "choreoapps";
        public static final String CHOREO_APIS = "choreoapis";
    }
}
