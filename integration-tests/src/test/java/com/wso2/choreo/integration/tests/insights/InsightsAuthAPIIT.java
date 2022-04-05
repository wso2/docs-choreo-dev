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
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static com.wso2.choreo.integration.config.Constant.INSIGHTS_AUTH_API_RESOURCE;

/**
 * Insights Auth API test cases.
 */
public class InsightsAuthAPIIT extends TestNGCitrusSpringSupport {
    private static String accessToken;


    @Autowired
    private HttpClient choreoInsightsTestClient;

    @BeforeClass
    public void beforeClass() throws Exception {
        accessToken = Constant.BEARER_PREFIX.concat(Configuration.INSIGHTS_ONPREM_KEY);
    }

    @Test
    @CitrusTest
    public void testGetToken() {
        $(http()
                .client(choreoInsightsTestClient)
                .send()
                .get(INSIGHTS_AUTH_API_RESOURCE)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON))
        );

        $(http()
                .client(choreoInsightsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/insights/getTokenSuccess.json"))
                .validate(json().ignore("$.token"))
                .validate(jsonPath().expression("$.token", "@startsWith('SharedAccessSignature')@"))
        );
    }
}
