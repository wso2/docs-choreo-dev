package com.wso2.choreo.integration.models.testconfigs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestConfigs {

    private String apiKey;
    private String invokeUrl;
}
