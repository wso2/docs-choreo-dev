package com.wso2.choreo.integration.models.devopsportalapi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ByoiEndpointDTO {
    private String main;
    private ApiSchema[] apiSchemas;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ApiSchema {
        private String filename;
        private String content;
    }
}
