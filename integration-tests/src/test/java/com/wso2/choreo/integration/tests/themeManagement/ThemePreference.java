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

package com.wso2.choreo.integration.tests.themeManagement;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.api.client.http.HttpStatusCodes;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
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

public class ThemePreference extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgUuid;
        private String orgHandle;

        @Autowired
        private HttpClient choreoTestClientForTheme;
        @Autowired
        private HttpClient choreoTestClientForCDNTheme;

        @BeforeClass
        public void setup_ThemePreference()
                        throws Exception {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);

        }

        @Test
        @CitrusTest
        public void updateThemeConfig_ThemePreference() {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                                .concat(orgUuid)
                                .concat("/themes/default");

                $(http()
                                .client(choreoTestClientForTheme)
                                .send()
                                .post(requestURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(new ClassPathResource(
                                                "templates/themeManagement/post_update_theme_success.json"))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClientForTheme)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/themeManagement/post_update_theme_success.json")));
        }

        @Test(dependsOnMethods = {"updateThemeConfig_ThemePreference"})
        @CitrusTest
        public void changeLive_ThemePreference()  {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                        .concat(orgUuid)
                        .concat("/themes/default/change-live?action=upload");

                $(http()
                        .client(choreoTestClientForTheme)
                        .send()
                        .post(requestURL)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        );

                $(http()
                        .client(choreoTestClientForTheme)
                        .receive()
                        .response(HttpStatus.OK));
        }


        @Test(dependsOnMethods = {"changeLive_ThemePreference"})
        @CitrusTest
        public void verifyDevPortal_ThemePreference()  {
                String requestURL = orgHandle.concat("/default.json");

                $(http()
                        .client(choreoTestClientForCDNTheme)
                        .send()
                        .get(requestURL)
                        .message()
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                        .client(choreoTestClientForCDNTheme)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .body(new ClassPathResource(
                                "templates/themeManagement/post_update_theme_success.json")));
        }

        @Test(dependsOnMethods = {"verifyDevPortal_ThemePreference"})
        @CitrusTest
        public void resetTheme_ThemePreference() {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                        .concat(orgUuid)
                        .concat("/themes/default");

                $(http()
                        .client(choreoTestClientForTheme)
                        .send()
                        .delete(requestURL)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                );

                $(http()
                        .client(choreoTestClientForTheme)
                        .receive()
                        .response(HttpStatus.valueOf(HttpStatusCodes.STATUS_CODE_NO_CONTENT)));
        }


        @Test(dependsOnMethods = {"resetTheme_ThemePreference"})
        @CitrusTest
        public void verifyNoThemeInDevPortal_ThemePreference()  {
                String requestURL = orgHandle.concat("/default.json");

                $(http()
                        .client(choreoTestClientForCDNTheme)
                        .send()
                        .get(requestURL)
                        .message()
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)));
                $(http()
                        .client(choreoTestClientForCDNTheme)
                        .receive()
                        .response(HttpStatus.NOT_FOUND)
                );
        }

}
