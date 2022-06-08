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

package com.wso2.choreo.integration.common;

import com.wso2.choreo.integration.config.Configuration;
import org.testng.annotations.BeforeSuite;

/**
 * The TestContext manages shared state between integration tests.
 * It is also responsible for setting up preconditions in preparation
 * for running the tests.
 */
public class TestContext {

    private static final ChoreoOrganization testOrg = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
            String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);

    private static final TokenHandler testUserTokenHandler = new TokenHandler.Builder(Configuration.TEST_CHOREO_ORG_HANDLE,
            Configuration.TEST_USER_EMAIL, Configuration.TEST_USER_PASSWORD)
            .asgardeoClientId(Configuration.ASGARDEO_CLIENT_ID)
            .asgardeoClientSecret(Configuration.ASGARDEO_CLIENT_SECRET)
            .stsClientId(Configuration.STS_CLIENT_ID)
            .stsClientSecret(Configuration.STS_CLIENT_SECRET)
            .cpAppClientId(Configuration.CP_APP_CLIENT_ID)
            .cpAppClientSecret(Configuration.CP_APP_CLIENT_SECRET).build();

    private static final TokenHandler anomalyDetectionUserTokenHandler =
            new TokenHandler.Builder(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE,
                    Configuration.ANOMALY_DETECTION.TEST_USER_EMAIL, Configuration.ANOMALY_DETECTION.TEST_USER_PASSWORD)
            .asgardeoClientId(Configuration.ASGARDEO_CLIENT_ID)
            .asgardeoClientSecret(Configuration.ASGARDEO_CLIENT_SECRET)
            .stsClientId(Configuration.STS_CLIENT_ID)
            .stsClientSecret(Configuration.STS_CLIENT_SECRET)
            .cpAppClientId(Configuration.CP_APP_CLIENT_ID)
            .cpAppClientSecret(Configuration.CP_APP_CLIENT_SECRET).build();

    @BeforeSuite
    public void setup() throws Exception {
        DataCleaner.removeOldTestData(testOrg);
    }

    public static ChoreoOrganization getTestOrg() {
        return testOrg;
    }

    public static TokenHandler getTestUserTokenHandler() { return testUserTokenHandler; }

    public static TokenHandler getAnomalyDetectionUserTokenHandler() { return anomalyDetectionUserTokenHandler; }
}
