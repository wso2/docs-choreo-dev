package com.wso2.choreo.integration.models.marketplace;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isSensitive")
    private boolean isSensitive = false;
    @JsonProperty("isOptional")
    private boolean isOptional = false;
}
