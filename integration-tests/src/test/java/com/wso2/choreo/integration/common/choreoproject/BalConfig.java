package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent a Ballerina configuration
 */
public class BalConfig {
    private String configKeyName;
    private boolean isRequired;
    private String valueOrSource;
    private String valueType;

    public BalConfig(String configKeyName, boolean isRequired, String valueOrSource, String valueType) {
        this.configKeyName = configKeyName;
        this.isRequired = isRequired;
        this.valueOrSource = valueOrSource;
        this.valueType = valueType;
    }
    public String getConfigKeyName() {
        return configKeyName;
    }
    
    public boolean getIsRequired() {
        return isRequired;
    }
    
    public String getValueOrSource() {
        return valueOrSource;
    }
    
    public String getValueType() {
        return valueType;
    }
}
