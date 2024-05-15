package com.wso2.choreo.integration.common.keymanager;

import java.util.List;

/**
 * Constants related to Key Manager operations.
 */
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
        public static final List<String> CALLBACK_URLS = List.of("https://sample-app.choreo-integration-tests.dev");
    }

    public static class DefaultChoreoEnvironments {

        public static final String DEV = "dev";
        public static final String PROD = "prod";
    }

    public static class TestIdpDefaults {

        public static final String CONSUMER_KEY_CLAIM = "azp";
        public static final String DESCRIPTION = "Test IdP for KeyManager tests";
        public static final boolean ENABLED = true;
        public static final String NAME = "Asgardeo-it";
        public static final String SCOPES_CLAIM = "scope";
        public static final String TOKEN_TYPE = "EXTERNAL";
        public static final String TYPE = "Custom";
        public static final String CERT_TYPE = "JWKS";
        public static final String ALIAS = "https://integration-tests.choreo.dev/oauth2/token";
        public static final String TOKEN_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/token";
        public static final String AUTHORIZE_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/authorize";
        public static final String ISSUER_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/token";
        public static final String LOGOUT_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oidc/logout";
        public static final String REVOKE_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/revoke";
        public static final String WELLKNOWN_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/token/.well-known/openid-configuration";
        public static final String JWKS_ENDPOINT = "https://integration-tests.choreo.dev/t/integration-tests/oauth2/jwks";
    }

    public static class ModifiedOAuthAppConfig {

        public static final int APP_TOKEN_EXPIRY = 1000;
        public static final int REFRESH_TOKEN_EXPIRY = 2000;
        public static final int USER_TOKEN_EXPIRY = 3000;
        public static final boolean IS_PUBLIC_CLIENT = true;
        public static final List<String> CALLBACK_URLS = List.of(
                "https://modified-sample-app-cb1.choreo-integration-tests.dev",
                "https://modified-sample-app-cb2.choreo-integration-tests.dev");
        public static final List<String> GRANT_TYPES = List.of("authorization_code", "refresh_token",
                "client_credentials");
    }
}
