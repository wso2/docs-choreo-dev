package com.wso2.choreo.integration.models.revision;

import lombok.Data;

@Data
public class DeploymentInfo {

    private String name;
    private String vhost;
    private String revisionUuid;
}
