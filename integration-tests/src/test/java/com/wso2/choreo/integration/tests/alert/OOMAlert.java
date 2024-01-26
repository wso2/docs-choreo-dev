/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.tests.alert;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;

import com.wso2.choreo.integration.apis.alert.AlertNotifier;

import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.email.RestAPIBasedEmailUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.alert.AlertResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * OOM alert test cases.
 */
public class OOMAlert extends TestNGCitrusSpringSupport {
     private static String accessToken;
    private RestAPIBasedEmailUtils restAPIBasedEmailUtils;

    @BeforeClass
    public void setup_OOMAlert() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        restAPIBasedEmailUtils = new RestAPIBasedEmailUtils(Configuration.getConfig(ConfigDefinition.GMAIL_API_CK),
                Configuration.getConfig(ConfigDefinition.GMAIL_API_CS),
                Configuration.getConfig(ConfigDefinition.GMAIL_API_REFRESH_TOKEN));
    }

    @Test
    @CitrusTest
    public void immediateAlert_OOMAlert() throws Exception {
        String appName = UUID.randomUUID().toString();
        AlertResponse resData = AlertNotifier.triggerImmediateAlert(appName,accessToken);

        boolean isMailReceived = restAPIBasedEmailUtils.reTrySearch(appName);
        Assert.assertEquals(resData.getMessage(), "Alert published to the Event Hub.");
        Assert.assertTrue(isMailReceived);
    }
}
