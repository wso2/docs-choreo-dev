package com.wso2.choreo.integration.models.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {

    private String componentName;
    private String envName;
    private String containerId;
    private String releaseId;
    private String alertType;
}
