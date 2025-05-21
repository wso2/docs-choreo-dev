package com.wso2.choreo.integration.models.revision;

import lombok.Data;

import java.util.List;

@Data
public class Revision {

    private String displayName;
    private String id;
    private List<DeploymentInfo> deploymentInfo;
}
