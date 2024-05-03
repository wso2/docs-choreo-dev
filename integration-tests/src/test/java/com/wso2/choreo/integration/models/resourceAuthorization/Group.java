package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private String id;
    private String orgName;
    private String orgUuid;
    private String description;
    private String displayName;
    private String handle;
    private String createdAt;
    private String updatedAt;
    private String uuid;
    private String assignedRoleCount;
}
