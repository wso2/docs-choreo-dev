package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionSchemaEntry {
    private String name;
    private String type;
    private String description;
    private boolean isSensitive = false;
    private boolean isOptional = false;
}
