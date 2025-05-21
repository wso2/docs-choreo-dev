package com.wso2.choreo.integration.models.marketplace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResource {
    private String name;
    private String version;
    private ResourceType resourceType;
    private String organizationId;
    private String projectId;
    private String summary;
    private String description;
    private List<String> tags;
    private List<String> categories;
    private List<ServiceVisibility> visibility;
    private Map<String, String> properties;
    private String resourceId;
    private String thumbnailUrl;
    private float averageRating;
    private int totalRatingCount;
    private String createdTime;
    private DatabaseRequest resourceDetails;
    private List<SchemaInfo> connectionSchemas;
    private String serviceId;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatabaseRequest {
        private String databaseServerId;
        private String databaseServerName;
        private String databaseType;
        private String status;
        @JsonProperty("isRestricted")
        private Boolean isRestricted = false;
        private String cloudProvider;
        private String cloudRegion;
        private String ca_certificate;
    }
}
