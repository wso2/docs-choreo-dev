package com.wso2.choreo.integration.tests;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.config.Constant;
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
    public HttpClient choreoAlertTestClient() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl(Constant.ALERT.CHOREO_GW_HOST)
                .build();
    }
}
