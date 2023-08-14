package com.wso2.choreo.integration.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SecurityConfigYaml {
    @JsonProperty("observability")
    Map<String, String> observability;
    @JsonProperty("devOps")
    Map<String, String> devOps;

    @JsonProperty("devportal")
    Map<String, String> devportal;
}
