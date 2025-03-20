package com.wso2.choreo.integration.models.marketplace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Config {
    private String[] environmentTemplateIds;
    private String name;

    private Value[] values;

    @Data
    @AllArgsConstructor
    public static class Value {
        private String key;
        private String value;
    }
}
