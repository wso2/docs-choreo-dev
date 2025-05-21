package com.wso2.choreo.integration.models.configmapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Config {

    private String componentId;
    private  String versionId;
    private String branch;
    private String sha;
}
