package com.wso2.choreo.integration.models.platformServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CloudRegion {
    US("us"),
    EU("eu"),
    AFRICA("africa"),
    AUS("aus");

    private final String value;

    CloudRegion(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CloudRegion fromValue(String value) {
        for (CloudRegion provider : CloudRegion.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown cloud region: " + value);
    }
}
