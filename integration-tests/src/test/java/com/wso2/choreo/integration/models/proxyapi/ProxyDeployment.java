package com.wso2.choreo.integration.models.proxyapi;

import com.wso2.choreo.integration.models.apimanager.KeyData;
import lombok.Data;

@Data
public class ProxyDeployment {
    private String invokeUrl;
    private String environment;
}
