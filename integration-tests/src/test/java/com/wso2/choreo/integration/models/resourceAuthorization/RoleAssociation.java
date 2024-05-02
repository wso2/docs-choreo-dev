package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleAssociation {
    private String roleHandle;
    private String roleUUID;
    private String roleDisplayName;
    private String mappingLevel;
    private String mappedResourceUUID;
}
