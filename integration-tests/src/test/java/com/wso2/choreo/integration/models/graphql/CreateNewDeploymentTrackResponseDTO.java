package com.wso2.choreo.integration.models.graphql;

import lombok.Data;

@Data
public class CreateNewDeploymentTrackResponseDTO {
        private String id;
        private String apiVersion;
        private String branch;
        private boolean latest;
        private String versionStrategy;
}
