package com.wso2.choreo.integration.models.proxyapi;


import lombok.Data;

@Data
public class Build {

    private String apiId;

    private String buildId;

    private String commitHash;

    private String componentId;

    private String componentName;

    private String finishedAt;

    private boolean mediationAttached;

    private String mediationComponentId;

    private String mediationComponentVersionId;

    private String revisionId;

    private String startedAt;

    private String status;

}
