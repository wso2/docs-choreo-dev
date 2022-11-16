package com.wso2.choreo.integration.models.deploymentstatus;

import lombok.Data;

@Data
public class ComponentDeploymentStatus {

    private String deploymentStatus;
    private String deploymentStatusV2;
    private String apiId;
    private String invokeUrl;
}
