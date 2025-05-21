package com.wso2.choreo.integration.models.proxyapi;

import lombok.Data;

@Data
public class DeploymentStatus {

    private String process;
    private String environment_Id;
    private String current_Action;
    private String next_Action;
    private String status;
}
