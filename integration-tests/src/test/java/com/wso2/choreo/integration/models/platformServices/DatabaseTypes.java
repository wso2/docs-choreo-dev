package com.wso2.choreo.integration.models.platformServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DatabaseTypes {
    POSTGRES("postgres"),
    MYSQL("mysql"),
    REDIS("redis");

    private final String value;

    DatabaseTypes(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DatabaseTypes fromValue(String value) {
        for (DatabaseTypes provider : DatabaseTypes.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown database type: " + value);
    }

}
