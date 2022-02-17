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
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.email.EmailUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.UUID;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * OOM alert test cases.
 */
public class OOMAlertIT extends TestNGCitrusSpringSupport {
    @Autowired
    private HttpClient choreoAlertTestClient;
    private static String accessToken;

    @BeforeClass
    public void beforeClass() throws Exception, TokenRetrievalException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
    }

    @Test
    @CitrusTest
    public void testImmediateAlert() throws Exception {
        String appName = UUID.randomUUID().toString();
        String body = "{\n"
                + "\t\"orgId\": \"" + Configuration.ALERT.ORG_UUID + "\",\n"
                + "\t\"envId\": \"" + Constant.ALERT.ENV_ID + "\",\n"
                + "\t\"publisher\": \"Critical alert detector\",\n"
                + "\t\"time\": \"" + Instant.now().toString() + "\",\n"
                + "    \"severity\": \"High\",\n"
                + "\t\"metaData\": {\n"
                + "        \"componentName\": \"" + appName + "\",\n"
                + "        \"envName\": \"" + Constant.ALERT.ENV_ID + "\",\n"
                + "        \"containerId\": \"" + Constant.ALERT.CONTAINER_ID + "\",\n"
                + "        \"releaseId\": \"" + Configuration.ALERT.RELEASE_ID + "\",\n"
                + "        \"alertType\": \"Out Of Memory error\"\n"
                + "\t},\n"
                + "\t\"properties\": {\n"
                + "\t}\n"
                + "}";

        System.out.println(body);

        $(http()
                .client(choreoAlertTestClient)
                .send()
                .post(Constant.ALERT.NOTIFICATION_SERVICE_RESOURCE)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoAlertTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/alert/post_alert_suceess.json")));

        boolean isMailReceived = EmailUtils.checkForMail(appName);
        Assert.assertTrue(isMailReceived);
    }
}
