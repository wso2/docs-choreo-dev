package com.wso2.choreo.integration.models.componentstatusbyversion;


import lombok.Data;


@Data
public class ComponentStatusByVersion {


    private String id;
    private String sha;
    private String completed_at;
    private String started_at;
    private String name;
    private String status;
    private String conclusion;
}
