package com.wso2.choreo.integration.models.platformServices;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseServerPutRequest {
    private String name;
    private String service_plan_id;
    private String cloud_provider;
    private String cloud_region;
    private Boolean is_vector_enabled;
    private Boolean display_on_marketplace;
}
