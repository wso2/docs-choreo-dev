package com.wso2.choreo.integration.models.platformServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DBServerPowerAction {
    ON("power_on"),
    OFF("power_off");

    private final String value;

    DBServerPowerAction(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DBServerPowerAction fromValue(String value) {
        for (DBServerPowerAction provider : DBServerPowerAction.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown power action: " + value);
    }
}
