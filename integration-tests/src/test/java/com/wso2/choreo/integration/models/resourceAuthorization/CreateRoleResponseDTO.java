package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleResponseDTO {
    public String id;
    public String description;
    public String displayName;
    public String handle;
    public String createdBy;
    public String updatedBy;
    public Date createdAt;
    public Date updatedAt;
    public String uuid;
}