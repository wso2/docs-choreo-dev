package com.wso2.choreo.integration.models.marketplace;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ConfigurationEntry {
    private String key;
    private String keyUuid;
    private String value;
    private boolean isSensitive;
    private boolean isFile;
}
