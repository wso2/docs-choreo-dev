package com.wso2.choreo.integration.models.orgs;


import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PromoteConfigurations {

    private String moduleName;
    private String commitHash;
    private boolean applyNow;
    private int operation;
    private String sourceUuid;
    private BalConfig[] configs;
}
