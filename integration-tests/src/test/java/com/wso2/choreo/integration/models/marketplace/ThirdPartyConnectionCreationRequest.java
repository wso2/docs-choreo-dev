package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartyConnectionCreationRequest {
    private String name;
    private String description;
    private String schemaReference;
    private Visibility[] visibilities;
    private String serviceId;
    private String componentType = "non-component";
    private Map<String, ResourceReference> envMapping;
    private Map<String, Configuration> configurations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResourceReference {
        String resourceId;
        String parameterReference;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfigEntry {
        private String key;
        private String value;
        private boolean isSensitive = false;
        private boolean isFile = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Configuration {
        private String environmentUuid;
        private boolean isCritical;
        private Map<String, ConfigEntry> entries;
    }
}
