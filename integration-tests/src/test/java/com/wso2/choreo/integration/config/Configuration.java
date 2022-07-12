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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Configuration {
    private static final Map<String, String> testConfigs = new HashMap<>();

    public static final String CHOREO_ENDPOINT = System.getenv("CHOREO_ENDPOINT");
    public static final String CHOREO_CP_PROJECTS_ENDPOINT = System.getenv("CHOREO_CP_PROJECTS_ENDPOINT");
    public static final String STS_ENDPOINT = System.getenv("STS_ENDPOINT");
    public static final String ASGARDEO_ENDPOINT = System.getenv("ASGARDEO_ENDPOINT");
    public static final String CHOREO_CP_GW_ENDPOINT = System.getenv("CHOREO_CP_GW_ENDPOINT");

    public static final String TEST_USER_EMAIL = System.getenv("TEST_USER_EMAIL");
    public static final String TEST_USER_PASSWORD = System.getenv("TEST_USER_PASSWORD");

    public static final String ASGARDEO_CLIENT_ID = System.getenv("ASGARDEO_CLIENT_ID");
    public static final String ASGARDEO_CLIENT_SECRET = System.getenv("ASGARDEO_CLIENT_SECRET");

    public static final String STS_CLIENT_ID = System.getenv("STS_CLIENT_ID");
    public static final String STS_CLIENT_SECRET = System.getenv("STS_CLIENT_SECRET");

    public static final String CP_APP_CLIENT_ID = System.getenv("CP_APP_CLIENT_ID");
    public static final String CP_APP_CLIENT_SECRET = System.getenv("CP_APP_CLIENT_SECRET");

    public static final String TEST_CHOREO_ORG_HANDLE = System.getenv("TEST_CHOREO_ORG_HANDLE");
    public static final int TEST_CHOREO_ORG_ID = Integer.parseInt(System.getenv("TEST_CHOREO_ORG_ID"));
    public static final String TEST_CHOREO_ORG_UUID = System.getenv("TEST_CHOREO_ORG_UUID");
    public static final String GITHUB_ENDPOINT = System.getenv("GITHUB_ENDPOINT");
    public static final String GITHUB_ORG = System.getenv("GITHUB_ORG");
    public static final String GITHUB_PAT = System.getenv("GITHUB_PAT");

    public static final String INSIGHTS_ENDPOINT = System.getenv("INSIGHTS_ENDPOINT");
    public static final String INSIGHTS_ONPREM_KEY = System.getenv("INSIGHTS_ONPREM_KEY");

    public static final class ALERT {
        public static final String MAIL_IMAP_PASS = System.getenv("ALERT_MAIL_IMAP_PASS");
        public static final String ORG_UUID = System.getenv("ALERT_ORG_UUID");
        public static final String RELEASE_ID = System.getenv("ALERT_RELEASE_ID");
        public static final String GMAIL_API_CK = System.getenv("GMAIL_API_CK");
        public static final String GMAIL_API_CS = System.getenv("GMAIL_API_CS");
        public static final String GMAIL_API_REFRESH_TOKEN = System.getenv("GMAIL_API_REFRESH_TOKEN");
    }

    public static final class ANOMALY_DETECTION {
        public static final String MAIL_IMAP_PASS = System.getenv("ANOMALY_DETECTION_MAIL_IMAP_PASS");
        public static final String PASSTHROUGH_CLIENT_ID = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_CLIENT_ID");
        public static final String PASSTHROUGH_CLIENT_SECRET = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_CLIENT_SECRET");
        public static final String PASSTHROUGH_COMPONENT_ID = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_COMPONENT_ID");
        public static final String PASSTHROUGH_COMPONENT_NAME = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_COMPONENT_NAME");
        public static final String PASSTHROUGH_RELEASE_ID = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_RELEASE_ID");
        public static final String PASSTHROUGH_INVOKE_URL = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_INVOKE_URL");
        public static final String PASSTHROUGH_VERSION_ID = System.getenv("ANOMALY_DETECTION_PASSTHROUGH_VERSION_ID");
        public static final String PROJECT_ID = System.getenv("ANOMALY_DETECTION_PROJECT_ID");
        public static final String TEST_CHOREO_ORG_ID = System.getenv("ANOMALY_DETECTION_TEST_CHOREO_ORG_ID");
        public static final String TEST_CHOREO_ORG_UUID = System.getenv("ANOMALY_DETECTION_TEST_CHOREO_ORG_UUID");
        public static final String TEST_USER_EMAIL = System.getenv("ANOMALY_DETECTION_TEST_USER_EMAIL");
        public static final String TEST_CHOREO_ORG_HANDLE = System.getenv("ANOMALY_DETECTION_TEST_CHOREO_ORG_HANDLE");
        public static final String TEST_USER_PASSWORD = System.getenv("ANOMALY_DETECTION_TEST_USER_PASSWORD");
    }

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
            add(configYaml.anomalyDetection);
            add(configYaml.insights);
            add(configYaml.themeManagement);
        }};

        validateYamlConfigs(yamlConfigCollection);
        readTestConfigs(yamlConfigCollection);
    }

    public static String getConfig(ConfigDefinition config) {
        String value = testConfigs.get(config.name());

        if (value != null) {
            return value;
        }

        throw new IllegalStateException("Config '" + config.name() + "' has not been set");
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
                    throw new IllegalStateException("yaml contains config '" + key +
                            "' which is not defined in Config enum");
                }
            }
        }

    }

    private static void readTestConfigs(List<Map<String, String>> yamlConfigCollection) {
        for (ConfigDefinition config : ConfigDefinition.values()) {
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

    private static Optional<String> readYamlConfigValue(List<Map<String, String>> yamlConfigCollection, String config) {
        for (Map<String, String> configMap : yamlConfigCollection) {
            String yamlValue = configMap.get(config);

            if (yamlValue != null) {
                return Optional.of(yamlValue);
            }
        }

        return Optional.empty();
    }
}
