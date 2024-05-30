package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class TestSessionResponsePayload {
    private String message;
    private String sessionId;
    private int ttl;
    private APIMVHhosts vhosts;
}
