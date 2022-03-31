package com.wso2.choreo.integration.config;


public class Configuration {
    public static final String CHOREO_ENDPOINT = System.getenv("CHOREO_ENDPOINT");
    public static final String STS_ENDPOINT = System.getenv("STS_ENDPOINT");
    public static final String ASGARDEO_ENDPOINT = System.getenv("ASGARDEO_ENDPOINT");
    public static final String CHOREO_CP_GW_ENDPOINT = System.getenv("CHOREO_CP_GW_ENDPOINT");

    public static final String TEST_USER_EMAIL = System.getenv("TEST_USER_EMAIL");
    public static final String TEST_USER_PASSWORD = System.getenv("TEST_USER_PASSWORD");

    public static final String ASGARDEO_CLIENT_ID = System.getenv("ASGARDEO_CLIENT_ID");
    public static final String ASGARDEO_CLIENT_SECRET = System.getenv("ASGARDEO_CLIENT_SECRET");

    public static final String STS_CLIENT_ID = System.getenv("STS_CLIENT_ID");
    public static final String STS_CLIENT_SECRET = System.getenv("STS_CLIENT_SECRET");

    public static final String CP_APP_CLIENT_ID = System.getenv("CP_APP_CLIENT_ID");
    public static final String CP_APP_CLIENT_SECRET = System.getenv("CP_APP_CLIENT_SECRET");

    public static final String TEST_CHOREO_ORG_HANDLE = System.getenv("TEST_CHOREO_ORG_HANDLE");
    public static final int TEST_CHOREO_ORG_ID = Integer.parseInt(System.getenv("TEST_CHOREO_ORG_ID"));
    public static final String TEST_CHOREO_ORG_UUID = System.getenv("TEST_CHOREO_ORG_UUID");

    public static final class ALERT {
        public static final String MAIL_IMAP_PASS = System.getenv("ALERT_MAIL_IMAP_PASS");
        public static final String ORG_UUID = System.getenv("ALERT_ORG_UUID");
        public static final String RELEASE_ID = System.getenv("ALERT_RELEASE_ID");
    }
}
