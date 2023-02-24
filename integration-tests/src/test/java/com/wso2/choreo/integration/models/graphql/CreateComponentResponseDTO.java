package com.wso2.choreo.integration.models.graphql;

import lombok.Data;

@Data
public class CreateComponentResponseDTO {
    private String id;
    private String orgId;
    private String projectId;
    private String handler;
}
