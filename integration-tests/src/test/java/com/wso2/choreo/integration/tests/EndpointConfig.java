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
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;

import com.wso2.choreo.integration.common.Endpoints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EndpointConfig {

    @PostConstruct
    public void loadEndpointConfig() throws Exception {
        com.wso2.choreo.integration.config.Configuration.loadConfigs();
        com.wso2.choreo.integration.config.Configuration.loadSecurityConfigs();
        TestContext.setTestOrg();
        TestContext.setTestUserTokenHandler();
        SecurityTestContext.setTestUserTokenHandlerForSecurityTests();
    }

    @Bean
    public HttpClient choreoTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoProjectsTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_NEW_APP_SERVICE_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoTestClientForSTS() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(ConfigDefinition.STS_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoCPTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_CP_GW_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoInsightsTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.INSIGHTS_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoTestClientForGithub() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.GITHUB_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoTestClientForTheme() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.THEME_ENDPOINT))
                .build();
    }

    @Bean
    public HttpClient choreoTestClientForCDNTheme() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CDN_THEME_ENDPOINT))
                .build();
    }

    @Bean
    public Map<Endpoints, HttpClient> citrusClients() {
        Map<Endpoints, HttpClient> endpoints = new HashMap<>();

        endpoints.put(Endpoints.CHOREO_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_ENDPOINT)).build());

        endpoints.put(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_NEW_APP_SERVICE_ENDPOINT)).build());

        endpoints.put(Endpoints.STS_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.STS_ENDPOINT)).build());

        endpoints.put(Endpoints.CHOREO_CP_GW_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CHOREO_CP_GW_ENDPOINT)).build());

        endpoints.put(Endpoints.INSIGHTS_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.INSIGHTS_ENDPOINT)).build());

        endpoints.put(Endpoints.GITHUB_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.GITHUB_ENDPOINT)).build());

        endpoints.put(Endpoints.THEME_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.THEME_ENDPOINT)).build());

        endpoints.put(Endpoints.CDN_THEME_ENDPOINT,
                CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.getConfig(
                        ConfigDefinition.CDN_THEME_ENDPOINT)).build());

        return endpoints;
    }


}
