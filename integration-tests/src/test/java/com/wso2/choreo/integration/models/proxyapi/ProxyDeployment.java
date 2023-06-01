package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class ProxyDeployment {
    private String invokeUrl;
    private String environment;
}
