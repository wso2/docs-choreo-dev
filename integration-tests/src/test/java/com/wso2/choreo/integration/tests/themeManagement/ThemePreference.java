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

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.wso2.choreo.integration.config.Configuration.TEST_CHOREO_ORG_UUID;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.ApiCreationException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;

public class ThemePreference extends TestNGCitrusSpringSupport {
        private static String accessToken;

        @Autowired
        private HttpClient choreoTestClientForTheme;

        @BeforeClass
        public void beforeClass()
                        throws TokenRetrievalException, IOException, InterruptedException, ProjectCreationException {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        }

        @Test
        @CitrusTest
        public void testUpdateAssets() throws IOException, InterruptedException, ApiCreationException {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                                .concat(TEST_CHOREO_ORG_UUID)
                                .concat("/themes/default/assets");

                $(http()
                                .client(choreoTestClientForTheme)
                                .send()
                                .put(requestURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body("{\"logoUrl\":{\"header\":\"https://devportal.preview-dv.choreo.dev/themes/default/images/logo-black.svg\""
                                                +
                                                ",\"footer\":\"https://devportal.preview-dv.choreo.dev/themes/default/images/wso2-logo.svg\"}}")
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClientForTheme)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/themeManagement/put_update_assets_success.json")));
        }

        @Test
        @CitrusTest
        public void testUpdateTypography() throws IOException, InterruptedException, ApiCreationException {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                                .concat(TEST_CHOREO_ORG_UUID)
                                .concat("/themes/default/typography");

                $(http()
                                .client(choreoTestClientForTheme)
                                .send()
                                .put(requestURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body("{\"headings\":{\"fontFamily\":\"Lato\"},\"body\":{\"fontFamily\":\"Roboto\"},\"p\":{\"fontFamily\":\"Arial\"}}")
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClientForTheme)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/themeManagement/put_update_typography_success.json")));
        }

        @Test
        @CitrusTest
        public void testUpdateColorPalette() throws IOException, InterruptedException, ApiCreationException {
                String requestURL = Constant.THEME_ENDPOINT_SUFFIX
                                .concat(TEST_CHOREO_ORG_UUID)
                                .concat("/themes/default/palette");

                $(http()
                                .client(choreoTestClientForTheme)
                                .send()
                                .put(requestURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body("{\"type\":\"light\",\"background\":{\"primary\":{\"light\":\"#f7f8fb\",\"dark\":"
                                                +
                                                "\"#9A97EB\"},\"secondary\":{\"light\":\"#ffffff\",\"dark\":\"#542C3A\"}},\"text\""
                                                +
                                                ":{\"primary\":{\"light\":\"#000000\",\"dark\":\"#493AA5\"},\"secondary\":{\"light\":\"#000000\",\"dark\":\"#f50057\"}}}")
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

                $(http()
                                .client(choreoTestClientForTheme)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource(
                                                "templates/themeManagement/put_update_palette_success.json")));
        }
}
