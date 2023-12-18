/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Configuration {
    private static final Map<String, String> testConfigs = new HashMap<>();
    private static final Map<String, String> securityTestConfigs = new HashMap<>();

    public static void loadConfigs() throws IOException, URISyntaxException {
        if (!testConfigs.isEmpty()) {
            return;
        }

        String testConfig = System.getProperty("TestConfig");

        if (StringUtils.isEmpty(testConfig)) {
            testConfig = "dev-env-config.yaml";
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        ConfigYaml configYaml = mapper.readValue(new File(Objects.requireNonNull(Configuration.class.getClassLoader().
                getResource(testConfig)).toURI()), ConfigYaml.class);

        List<Map<String, String>> yamlConfigCollection = new ArrayList<>() {{
            add(configYaml.accountInfo);
            add(configYaml.authInfo);
            add(configYaml.common);
            add(configYaml.alerts);
            add(configYaml.insights);
            add(configYaml.themeManagement);
            add(configYaml.logs);
            add(configYaml.buildpacks);
        }};

        validateYamlConfigs(yamlConfigCollection);
        readTestConfigs(yamlConfigCollection);
    }

    public static void loadSecurityConfigs() throws IOException, URISyntaxException {
        if (!securityTestConfigs.isEmpty()) {
            return;
        }

        String securityTestConfig = System.getProperty("SecurityTestConfig");

        if (StringUtils.isEmpty(securityTestConfig)) {
            securityTestConfig = "dev-security-env-config.yaml";
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        SecurityConfigYaml securityConfigYaml = mapper.readValue(new File(Objects.requireNonNull(Configuration.class.getClassLoader().
                getResource(securityTestConfig)).toURI()), SecurityConfigYaml.class);

        List<Map<String, String>> yamlConfigCollection = new ArrayList<>() {{
            add(securityConfigYaml.accountInfo);
            add(securityConfigYaml.observability);
            add(securityConfigYaml.devOps);
            add(securityConfigYaml.devportal);
            add(securityConfigYaml.deliveryInsights);
            add(securityConfigYaml.componentManagement);
            add(securityConfigYaml.orgManagemnt);
            add(securityConfigYaml.configManagement);
            add(securityConfigYaml.billing);
            add(securityConfigYaml.integrationComponent);
            add(securityConfigYaml.apim);
        }};

        validateSecurityYamlConfigs(yamlConfigCollection);
        readSecurityTestConfigs(yamlConfigCollection);
    }

    public static String getConfig(ConfigDefinition config) {
        String value = testConfigs.get(config.name());

        if (value != null) {
            return value;
        }

        throw new IllegalStateException("Config '" + config.name() + "' has not been set");
    }

    public static Optional<String> getOptionalConfig(OptionalConfigDefinition config) {
        String value = testConfigs.get(config.name());

        if (value != null) {
            return Optional.of(value);
        }

        return Optional.empty();
    }

    public static String getSecurityConfig(SecurityConfigDefinition config) {
        String value = securityTestConfigs.get(config.name());

        if (value != null) {
            return value;
        }

        throw new IllegalStateException("Security config '" + config.name() + "' has not been set");
    }

    private static void validateYamlConfigs(List<Map<String, String>> yamlConfigCollection) {
        for (Map<String, String> configMap : yamlConfigCollection) {
            for (String key : configMap.keySet()) {
                boolean isConfigInEnum = false;
                for (ConfigDefinition configEnum : ConfigDefinition.values()) {
                    if (configEnum.name().equals(key)) {
                        isConfigInEnum = true;
                        break;
                    }
                }

                if (!isConfigInEnum) {
                    for (OptionalConfigDefinition configEnum : OptionalConfigDefinition.values()) {
                        if (configEnum.name().equals(key)) {
                            isConfigInEnum = true;
                            break;
                        }
                    }
                }

                if (!isConfigInEnum) {
                    throw new IllegalStateException("yaml contains config '" + key +
                            "' which is not defined in Config enum");
                }
            }
        }


    }

    private static void readTestConfigs(List<Map<String, String>> yamlConfigCollection) {
        for (ConfigDefinition config : getConfigDefinitions()) {
            final String configName = config.name();

            String envValue = System.getenv(configName);

            if (envValue != null) {
                testConfigs.put(configName, envValue);
            } else {
                Optional<String> yamlValue = readYamlConfigValue(yamlConfigCollection, configName);
                if (yamlValue.isPresent()) {
                    testConfigs.put(configName, yamlValue.get());
                } else {
                    throw new IllegalStateException("Config '" + configName + "' has not been set");
                }
            }
        }
    }

    private static void validateSecurityYamlConfigs(List<Map<String, String>> yamlConfigCollection) {
        for (Map<String, String> configMap : yamlConfigCollection) {
            for (String key : configMap.keySet()) {
                boolean isConfigInEnum = false;
                for (SecurityConfigDefinition configEnum : SecurityConfigDefinition.values()) {
                    if (configEnum.name().equals(key)) {
                        isConfigInEnum = true;
                        break;
                    }
                }

                if (!isConfigInEnum) {
                    throw new IllegalStateException("yaml contains config '" + key +
                            "' which is not defined in Config enum");
                }
            }
        }


    }

    private static void readSecurityTestConfigs(List<Map<String, String>> yamlConfigCollection) {
        for (SecurityConfigDefinition config : getSecurityConfigDefinitions()) {
            final String configName = config.name();

            String envValue = System.getenv(configName);

            if (envValue != null) {
                securityTestConfigs.put(configName, envValue);
            } else {
                Optional<String> yamlValue = readYamlConfigValue(yamlConfigCollection, configName);
                if (yamlValue.isPresent()) {
                    securityTestConfigs.put(configName, yamlValue.get());
                } else {
                    throw new IllegalStateException("Security config '" + configName + "' has not been set");
                }
            }
        }
    }

    private static Optional<String> readYamlConfigValue(List<Map<String, String>> yamlConfigCollection, String config) {
        for (Map<String, String> configMap : yamlConfigCollection) {
            String yamlValue = configMap.get(config);

            if (yamlValue != null) {
                return Optional.of(yamlValue);
            }
        }

        return Optional.empty();
    }

    private static List<ConfigDefinition> getConfigDefinitions() {
        String token = System.getProperty("Token");

        if (!StringUtils.isEmpty(token)) {
            String[] configsToIgnore = { "TEST_USER_EMAIL", "TEST_USER_PASSWORD", "STS_CLIENT_ID", "STS_CLIENT_SECRET",
                    "ASGARDEO_CLIENT_ID", "ASGARDEO_CLIENT_SECRET", "CP_APP_CLIENT_ID", "CP_APP_CLIENT_SECRET"};

            List<ConfigDefinition> configSubset = new ArrayList<>();

            for (ConfigDefinition config : ConfigDefinition.values()) {
                if (Arrays.stream(configsToIgnore).noneMatch(config.name()::equals)) {
                    configSubset.add(config);
                }
            }

            return configSubset;
        }

        return List.of(ConfigDefinition.values());
    }

    private static List<SecurityConfigDefinition> getSecurityConfigDefinitions() {
        return List.of(SecurityConfigDefinition.values());
    }
}
