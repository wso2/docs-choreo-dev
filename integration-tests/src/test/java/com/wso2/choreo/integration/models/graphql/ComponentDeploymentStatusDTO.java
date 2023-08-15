package com.wso2.choreo.integration.models.graphql;

import lombok.Data;

@Data
public class ComponentDeploymentStatusDTO {

    private String deploymentStatus;
    private String deploymentStatusV2;
    private String environmentId;
    private String apiId;
    private String invokeUrl;
    private String versionId;
    private String releaseId;
    private BuildDTO build;
    private ApiRevisionDTO apiRevision;
}
