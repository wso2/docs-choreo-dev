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
public class GroupRoleMappingResponseDTO {
    private String orgUUID;
    private String roleUUID;
    private List<GroupAssociation> groupAssociations;

    @Data
    @NoArgsConstructor
    public static class GroupAssociation {
        private String groupHandle;
        private String groupUUID;
        private String groupDisplayName;
        private String mappingLevel;
        private String mappedResourceUUID;
    }
}
