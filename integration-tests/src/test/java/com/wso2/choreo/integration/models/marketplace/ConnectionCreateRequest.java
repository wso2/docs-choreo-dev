package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionCreateRequest {

    private String name;
    private String description;
    private String serviceId;
    private String schemaReference;
    private Environment[] environments;
    private Visibility[] visibilities;
    private String requestingServiceVisibility;
    private int orgIdInteger;
}
