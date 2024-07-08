/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 *  This software is the property of WSO2 LLC. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *  the WSO2 Commercial License available at http://wso2.com/licenses.
 *  For specific language governing the permissions and limitations under
 *  this license, please see the license as well as any agreement you’ve
 *  entered into with WSO2 governing the purchase of this software and any
 *  associated services.
 */

package com.wso2.choreo.integration.common;

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeSuite;

/**
 * The SecurityTestContext manages shared state between security tests.
 * It is also responsible for setting up preconditions in preparation
 * for running the security tests.
 */
public class SecurityTestContext {
    @Getter
    private static TokenHandler testUserTokenHandlerForSecurityTests;

    @BeforeSuite
    public void setup() throws Exception {
        Configuration.loadConfigs();
        Configuration.loadSecurityConfigs();
        setTestUserTokenHandlerForSecurityTests();
    }

    public static synchronized void setTestUserTokenHandlerForSecurityTests() {
        String config = System.getProperty("TestConfig");

        if (!StringUtils.isEmpty(config) && !config.equals("dev-env-config.yaml")) {
            return;
        }

        if (testUserTokenHandlerForSecurityTests == null) {
            testUserTokenHandlerForSecurityTests = new TokenHandler.Builder(
                    Configuration.getSecurityConfig(SecurityConfigDefinition.SECURITY_TEST_CHOREO_ORG_HANDLE),
                    Configuration.getSecurityConfig(SecurityConfigDefinition.LOW_PRIVILEGED_USER_EMAIL),
                    Configuration.getSecurityConfig(SecurityConfigDefinition.LOW_PRIVILEGED_USER_PASSWORD))
                    .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                    .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                    .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                    .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        }
    }

}
