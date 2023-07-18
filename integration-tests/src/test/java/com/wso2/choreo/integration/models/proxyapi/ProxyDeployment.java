package com.wso2.choreo.integration.models.proxyapi;

import com.wso2.choreo.integration.models.environments.ProxyEnvironment;
import lombok.Data;

@Data
public class ProxyDeployment {
    private String invokeUrl;
    private ProxyEnvironment environment;
}
