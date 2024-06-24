package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class TestSessionResponse {
    private String message;
    private TestSessionResponsePayload data;
}
