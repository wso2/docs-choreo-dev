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
public class DatabaseConnectionCreateRequest {
    private String name;
    private String description;
    private String schemaReference;
    private Visibility[] visibilities;
    private String serviceId;
    private String componentType;
    private Map<String, ResourceReference> envMapping;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResourceReference {
        String resourceId;
        String parameterReference;
    }

}
