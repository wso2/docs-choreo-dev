/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public HttpClient choreoTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.CHOREO_ENDPOINT)
                .build();
    }

    @Bean
    public HttpClient choreoTestClientForSTS() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.STS_ENDPOINT)
                .build();
    }
}
