package com.wso2.choreo.integration.common.oauth;

/**
 * Constants related to OAuth.
 */
public class OAuthConstants {
    
    public static final String DEFAULT_CHOREO_STS_DOMAIN = "choreosts.dev";
    public static final String DEFAULT_SCOPES = "openid profile";
    public static class STSEndpoints {
        public static final String TOKEN = "/oauth2/token";
        public static final String AUTHORIZE = "/oauth2/authorize";
        public static final String WELL_KNOWN = "/oauth2/token/.well-known/openid-configuration";
        public static final String LOGOUT = "/oidc/logout";
    }

    public static class Grants {
        public static final String AUTHORIZATION_CODE = "authorization_code";
        public static final String CLIENT_CREDENTIALS = "client_credentials";
        public static final Object REFRESH_TOKEN = "refresh_token";
    }

    public static class TokenParams {
        public static final String GRANT_TYPE = "grant_type";
        public static final String SCOPE = "scope";
        public static final String CODE = "code";
        public static final String REDIRECT_URI = "redirect_uri";
        public static final String CODE_VERIFIER = "code_verifier";
        public static final String CLIENT_ID = "client_id";
        public static final String REFRESH_TOKEN = "refresh_token";
    }
}
