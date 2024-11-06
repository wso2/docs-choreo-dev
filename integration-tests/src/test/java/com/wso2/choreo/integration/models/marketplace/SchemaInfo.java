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
public class SchemaInfo {
    private String name;
    private String id;
    private String description;
    @JsonProperty("isDefault")
    private boolean isDefault;
    private ConnectionSchemaEntry[] entries;
}
