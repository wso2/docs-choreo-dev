package com.wso2.choreo.integration.models.testconfigs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestConfigs {


    private String apiKey;
    private String invokeUrl;

    private Map<String, TestConfigs> configsMap = new HashMap<>();

    public void addConfig(String env, TestConfigs configs) {
        configsMap.put(env, configs);
    }

    public TestConfigs getConfig(String env) {
        return configsMap.get(env);
    }

}
