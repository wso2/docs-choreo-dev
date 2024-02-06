/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.buildpacks;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.contains;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

public class Buildpack extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgUuid;

        @Autowired
        private HttpClient choreoProjectsTestClient;

        @BeforeClass
        public void setup_Buildpack()
                        throws Exception {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        }

        @Test
        @CitrusTest
        public void retrieve_BuildpackMetadata() {
                String requestURL = Constant.DEVOPS_BUILDPACKS
                                .concat("?orgUuid=")
                                .concat(orgUuid)
                                .concat("&componentType=service");

                $(http()
                                .client(choreoProjectsTestClient)
                                .send()
                                .get(requestURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoProjectsTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                .expression("$[*].language", contains("java","python","nodejs","go","php","ruby","ballerina","docker","microintegrator","dotnet"))));
        }
    }
