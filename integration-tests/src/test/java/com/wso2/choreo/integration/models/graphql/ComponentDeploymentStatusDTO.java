package com.wso2.choreo.integration.models.graphql;

import lombok.Data;

@Data
public class ComponentDeploymentStatusDTO {

    private String deploymentStatus;
    private String deploymentStatusV2;
    private String apiId;
    private String invokeUrl;
    private String releaseId;
    private BuildDTO build;
    private ApiRevisionDTO apiRevision;
}
