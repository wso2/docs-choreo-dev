package com.wso2.choreo.integration.models.environments;


import lombok.Data;

@Data
public class Environment {
    private String apiEnvName;
    private String id;
    private String name;
    private String choreoEnv;
    private String[] promoteFrom;
    private String namespace;
    private String vhost;
    private boolean isMigrating;
    private String apimEnvId;
    private String sandboxVhost;
    private boolean critical;
    private String templateId;
    private boolean isPdp;
}
