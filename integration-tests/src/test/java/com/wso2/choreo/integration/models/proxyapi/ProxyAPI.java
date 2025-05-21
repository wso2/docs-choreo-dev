package com.wso2.choreo.integration.models.proxyapi;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyAPI {

    private String id;
    private String name;
    private String lifeCycleStatus;
    private String description;
    private String context;
}
