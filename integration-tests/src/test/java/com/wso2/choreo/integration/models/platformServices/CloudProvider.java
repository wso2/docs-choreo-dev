package com.wso2.choreo.integration.models.platformServices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CloudProvider {
    AWS("aws"),
    GCP("gcp"),
    AZURE("azure"),
    DIGITALOCEAN("digitalocean");

    private final String value;

    CloudProvider(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CloudProvider fromValue(String value) {
        for (CloudProvider provider : CloudProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown cloud provider: " + value);
    }

}
