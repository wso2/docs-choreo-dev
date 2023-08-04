package com.wso2.choreo.integration.models.connectors;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Connector {

    private String apiId;
    private String connectorVersion;
    private String version;
    private String organizationId;
    private String orgUuid;
    private String visibility;
    private String orgHandler;
    private String componentId;

}
