package com.wso2.choreo.integration.models.devopsportalapi;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ThirdPartyContainerRegistryDTO {
    private String created_at;
    private String host;
    private String id;
    private String metadata;
    private String name;
    private int organization_id;
    private String organization_uuid;
    private String provider;
    private String reference_token;
    private String scope;
    private String type;
}
