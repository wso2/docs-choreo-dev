package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThirdPartyServiceConnectionResponse {
    private String groupUuid;
    private String name;
    private String serviceName;
    private String schemaName;
    private boolean isPartiallyCreated;
    private ResourceType resourceType;
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
    public static class ConfigEntry
    {
        private String key;
        private String value;
        private boolean isSensitive;
        private boolean isFile;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Configuration
    {
        private String environmentUuid;
        private boolean isCritical;
        private Map<String, ConfigEntry> entries;
    }
}
