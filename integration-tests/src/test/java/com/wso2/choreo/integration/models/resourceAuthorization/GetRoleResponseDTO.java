package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRoleResponseDTO {
    private String id;
    private String description;
    private String displayName;
    private String handle;
    private boolean defaultRole;
    private List<Tag> tags;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
    private String uuid;

    @Data
    @NoArgsConstructor
    public static class Tag {
        private String handle;
        private String createdAt;
        private String updatedAt;
    }
}
