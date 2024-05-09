package com.wso2.choreo.integration.common.keymanager;

import java.util.List;

public class KeyManagerConstants {

    public static String ASGARDEO_KM_TYPE = "Asgardeo";
    public static String DEFAULT_KM_NAME_PREFIX = "_internal_key_manager_";

    public static class AppGwKeysetConfigNames {

        public static final String CLIENT_ID = "clientId";
        public static final String CLIENT_SECRET = "clientSecret";
        public static final String IDP_ID = "idpId";
    }

    public static class DefaultConfigGroups {

        public static final String APP_GW_KEYSETS = "app-gw-key-sets";
        public static final String APP_GW_AUTH_CONFIGS = "app-gw-auth-configs";
    }

    public static class TestProjectData {

        public static final String PROJECT_NAME_BASE = "keysetstestproject_";
        public static final String PROJECT_DESCRIPTION = "Test Project Description";
        public static final String REGION = "US";
    }

    public static class TestKeyGenRequestData {

        public static final int APP_TOKEN_EXPIRY = 1440;
        public static final int REFRESH_TOKEN_EXPIRY = 10080;
        public static final int USER_TOKEN_EXPIRY = 1440;
        public static final List<String> GRANT_TYPES = List.of("authorization_code", "refresh_token");
        public static final boolean PKCE_MANDATORY = false;
        public static final boolean IS_PUBLIC_CLIENT = false;
    }

    public static class DEFAULT_CHOREO_ENVIRONMENTS{

        public static final String DEV = "dev";
        public static final String PROD = "prod";
    }
}
