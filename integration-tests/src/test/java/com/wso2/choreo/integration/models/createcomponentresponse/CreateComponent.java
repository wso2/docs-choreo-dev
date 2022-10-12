package com.wso2.choreo.integration.models.createcomponentresponse;

import lombok.Data;

@Data
public class CreateComponent {
    private String id;
    private int orgId;
    private String projectId;
    private String handler;
}
