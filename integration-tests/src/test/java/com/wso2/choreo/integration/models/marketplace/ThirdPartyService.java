package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ThirdPartyService {
    private String name;
    private String version;
    private String summary;
    private SchemaInfo[] connectionSchemas;
    private String organizationId;
    private String projectId;
    private ServiceType serviceType;
    private String[] tags;
    private String[] categories;
    private String[] visibility;
    private boolean isThirdParty;
    private Idl idl;
    private ServiceStatus status;
    private ResourceType resourceType;
    private Map<String, String> endpointRefs;
}
