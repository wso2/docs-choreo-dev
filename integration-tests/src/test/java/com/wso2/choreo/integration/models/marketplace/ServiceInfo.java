package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfo {
    private String name;
    private String version;
    private String organizationId;
    private String projectId;
    private String summary;
    private String description;
    private String[] tags;
    private String[] categories;
    private ServiceVisibility[] visibility;
    private String serviceId;
    private boolean isThirdParty;
    private SchemaInfo[] connectionSchemas;
    private ServiceStatus status;
    private ServiceType serviceType;
    private String thumbnailUrl;
    private String createdTime;
}
