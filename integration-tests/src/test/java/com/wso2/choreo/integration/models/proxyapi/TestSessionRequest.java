package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class TestSessionRequest {
    private String endpointId;
    private String userIdpId;
    private String networkVisibility;
}
