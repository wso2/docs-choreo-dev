package com.wso2.choreo.integration.models.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiRevisionDTO {
    private String id;
    private String displayName;
    private String proxyId;
    private String apiId;
    private String versionId;
    private String newRevisionId;
    private String oldRevisionId;
    private String orgUuid;
    private String description;
    private String buildId;
    private int revisionCount;
}
