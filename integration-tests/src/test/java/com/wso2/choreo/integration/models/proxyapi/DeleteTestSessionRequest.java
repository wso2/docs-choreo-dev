package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class DeleteTestSessionRequest {
    private String endpointId;
    private String sessionId;
    private String userIdpId;
}
