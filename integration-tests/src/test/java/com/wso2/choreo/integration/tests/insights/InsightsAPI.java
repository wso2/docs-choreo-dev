/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.tests.insights;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.insights.InsightRequest;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.Insights.InsightDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_API_RESOURCE;

/**
 * Insights API test cases.
 */
public class InsightsAPI extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUUID;
    private InsightDTO dto;

    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
    }

    @Test
    @CitrusTest
    public void testGetEnvironments() throws IOException {
        dto = InsightDTO.builder().orgId(orgUUID).build();
        InsightRequest.getEnvironments(this, choreoCPTestClient, accessToken, dto, INSIGHTS_API_RESOURCE);
    }      

    @Test(dependsOnMethods = { "testGetEnvironments" })
    @CitrusTest
    public void testUtilityOperations() throws IOException {
        dto.setTenant("carbon.super");
        InsightRequest.getUtilityOperations(this, choreoCPTestClient, accessToken, dto, INSIGHTS_API_RESOURCE);
    }

    @Test(dependsOnMethods = { "testGetEnvironments" })
    @CitrusTest
    public void testOverviewOperations() throws IOException {
        OffsetDateTime currentDateTimeAtUTC = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime sixMonthsAgoDateTimeAtUTC = currentDateTimeAtUTC.minusMonths(6);
        dto.setFromTime(sixMonthsAgoDateTimeAtUTC.toString());
        dto.setToTime(currentDateTimeAtUTC.toString());
        dto.setTenant("carbon.super");
        InsightRequest.getOverviewOperations(this, choreoCPTestClient, accessToken, dto, INSIGHTS_API_RESOURCE);
    }
}
