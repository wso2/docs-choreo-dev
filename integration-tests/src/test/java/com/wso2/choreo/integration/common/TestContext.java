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

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeSuite;

/**
 * The TestContext manages shared state between integration tests.
 * It is also responsible for setting up preconditions in preparation
 * for running the tests.
 */
public class TestContext {

    @Getter
    private static ChoreoOrganization testOrg;

    @Getter
    private static TokenHandler testUserTokenHandler;

    @BeforeSuite
    public void setup() throws Exception {
        Configuration.loadConfigs();
        setTestOrg();
        setTestUserTokenHandler();
        DataCleaner.removeOldTestData(testOrg);
    }

    public static synchronized void setTestOrg() {
        if (testOrg == null) {
            testOrg = new ChoreoOrganization(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE),
                    Integer.parseInt( Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID)),
                    Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        }
    }

    public static synchronized void setTestUserTokenHandler() {
        if (testUserTokenHandler == null) {
            String token = System.getProperty("Token");

            if (!StringUtils.isEmpty(token)) {
                testUserTokenHandler = new TokenHandler(token);
            } else {
                testUserTokenHandler = new TokenHandler.Builder(
                        Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE),
                        Configuration.getConfig(ConfigDefinition.TEST_USER_EMAIL),
                        Configuration.getConfig(ConfigDefinition.TEST_USER_PASSWORD))
                        .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                        .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                        .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                        .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
            }
        }
    }

}
