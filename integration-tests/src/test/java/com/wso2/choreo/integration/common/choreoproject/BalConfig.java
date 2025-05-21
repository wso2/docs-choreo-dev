package com.wso2.choreo.integration.common.choreoproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class to represent a Ballerina configuration
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalConfig {
    private String configKeyName;
    private boolean isRequired;
    private String valueOrSource;
    private String valueType;

}
