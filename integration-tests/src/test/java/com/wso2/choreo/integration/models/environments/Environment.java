package com.wso2.choreo.integration.models.environments;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Environment {
    private String organizationUuid;
    private String apiEnvName;
    private String dev;
    private String id;
    private String name;
    private boolean orgShared;
    private boolean description;
    private String choreoEnv;
    private String projectId;
    private String[] promoteFrom;
    private String namespace;
}
