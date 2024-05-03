package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleAssociation {
    private String roleHandle;
    private String roleUUID;
    private String roleDisplayName;
    private String mappingLevel;
    private String mappedResourceUUID;
}
