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
public class RoleGroupMappingResponseDTO {
    private String orgUUID;
    private String groupUUID;
    private List<RoleAssociation> roleAssociations;

    @Data
    @NoArgsConstructor
    public static class RoleAssociation {
        private String roleHandle;
        private String roleUUID;
        private String roleDisplayName;
        private String mappingLevel;
        private String mappedResourceUUID;
    }
}
