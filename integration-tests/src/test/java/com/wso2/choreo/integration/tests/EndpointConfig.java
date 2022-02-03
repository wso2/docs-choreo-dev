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
