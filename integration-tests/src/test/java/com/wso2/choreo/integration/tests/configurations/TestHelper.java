/*
 * Copyright (c) 2025, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you've
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.configurations;

import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfig;
import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfigValue;
import com.wso2.choreo.integration.models.configservice.*;
import com.wso2.choreo.integration.models.environments.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestHelper {
    public static final String EXPECTED_SCHEMA_API_RESPONSE = "{\"user-info\": \"YmFzaWMtaW5mbzoKICBhZ2U6IDMwCiAgaXNfcmVnaXN0ZXJlZDogdHJ1ZQogIG5hbWU6ICJKb2huIERvZSIKICBzZXg6ICJtYWxlIgpvY2N1cGF0aW9uOgogIGNvbXBhbnk6ICJ3c28yIgogIHRpdGxlOiAiU0UiCg==\", \"index\": \"1\"}";
    public static final String EXPECTED_MAPPING_API_DEV_RESPONSE = "{\n" +
            "  \"redis_conf_content\": \"cHJvdGVjdGVkLW1vZGUgbm8Kc2F2ZSA2MCAxMDAwCgpkaXIgL3Zhci9saWIvZGV2L3JlZGlzCmRiZmlsZW5hbWUgZGV2LWR1bXAucmRi\",\n" +
            "  \"redis_host\": \"dev-hostname\",\n" +
            "  \"redis_password\": \"dev-password\"\n" +
            "}";
    public static final String EXPECTED_MAPPING_API_PROD_RESPONSE = "{\n" +
            "  \"redis_conf_content\": \"cHJvdGVjdGVkLW1vZGUgbm8Kc2F2ZSA2MCAxMDAwCgpkaXIgL3Zhci9saWIvcHJvZC9yZWRpcwpkYmZpbGVuYW1lIHByb2QtZHVtcC5yZGI=\",\n" +
            "  \"redis_host\": \"prod-hostname\",\n" +
            "  \"redis_password\": \"prod-password\"\n" +
            "}";

    public static SchemaConfig[] generateAddConfigurationPayload(String envId) {
        return new SchemaConfig[] {

                SchemaConfig.builder().key("IDENTIFICATION_NUMBER").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("1")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("basic-info.name").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("John Doe")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("basic-info.is_registered").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("true")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("basic-info.age").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("30")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("basic-info.sex").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("male")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("occupation.title").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("SE")
                                        .build()
                        }).build(),
                SchemaConfig.builder().key("occupation.company").values(
                        new SchemaConfigValue[] {
                                SchemaConfigValue.builder()
                                        .environmentUuid(envId)
                                        .value("wso2")
                                        .build()
                        }).build(),
        };
    };

    public static ConfigurationGroup generateConfigGroupCreationPayload(String groupName, List<Environment> environments) {
        Map<Boolean, List<String>> groupedEnvTemplateIds = environments.stream()
                .collect(Collectors.partitioningBy(
                        Environment::isCritical,
                        Collectors.mapping(Environment::getTemplateId, Collectors.toList())
                ));

        List<String> nonCriticalEnvIds = groupedEnvTemplateIds.get(false);
        List<String> criticalEnvIds = groupedEnvTemplateIds.get(true);

        Configuration hostname = Configuration.builder()
                .key("hostname")
                .keyUuid("")
                .isSensitive(false)
                .isFile(false)
                .values(Stream.concat(
                        criticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("prod-hostname")
                                        .build()),
                        nonCriticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("dev-hostname")
                                        .build())
                ).collect(Collectors.toList()))
                .build();

        Configuration password = Configuration.builder()
                .key("password")
                .keyUuid("")
                .isSensitive(true)
                .isFile(false)
                .values(Stream.concat(
                        criticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("prod-password")
                                        .build()),
                        nonCriticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("dev-password")
                                        .build())
                ).collect(Collectors.toList()))
                .build();

        Configuration redisConfFile = Configuration.builder()
                .key("redis-conf")
                .keyUuid("")
                .isSensitive(false)
                .isFile(true)
                .values(Stream.concat(
                        criticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("cHJvdGVjdGVkLW1vZGUgbm8Kc2F2ZSA2MCAxMDAwCgpkaXIgL3Zhci9saWIvcHJvZC9yZWRpcwpkYmZpbGVuYW1lIHByb2QtZHVtcC5yZGI=")
                                        .build()),
                        nonCriticalEnvIds.stream()
                                .map(envId -> ConfigurationValue.builder()
                                        .environmentUuid(envId)
                                        .value("cHJvdGVjdGVkLW1vZGUgbm8Kc2F2ZSA2MCAxMDAwCgpkaXIgL3Zhci9saWIvZGV2L3JlZGlzCmRiZmlsZW5hbWUgZGV2LWR1bXAucmRi")
                                        .build())
                ).collect(Collectors.toList()))
                .build();

        EnvironmentSet criticalEnvSet = EnvironmentSet.builder()
                .environmentSetUuid("ed99725e-115b-46bc-ac9d-0cae09edda7a")
                .environmentTemplates(criticalEnvIds)
                .build();

        EnvironmentSet nonCriticalEnvSet = EnvironmentSet.builder()
                .environmentSetUuid("f08d7467-6ffa-44a2-bd2d-3744d87053d8")
                .environmentTemplates(nonCriticalEnvIds)
                .build();

        return ConfigurationGroup.builder()
                .groupName(groupName)
                .groupDisplayName(groupName.replace(" ", "-"))
                .description("Redis Config Group for Integration Tests")
                .scopes(new ArrayList<>())
                .configurations(Arrays.asList(hostname, password, redisConfFile))
                .environmentSets(Arrays.asList(criticalEnvSet, nonCriticalEnvSet))
                .build();
    }

    public static List<MappingConfiguration> generateMappingConfigurations(ConfigurationGroup configGroup,
                                                                           String environmentTemplateId) {
        List<MappingConfiguration> mappingConfigurations = new ArrayList<>();
        String configGroupUuid = configGroup.getGroupUuid();

         Map<String, String> keyMappings = Map.of(
                "hostname", "REDIS_HOST",
                "password", "REDIS_PASSWORD",
                "redis-conf", "/workspace/configs/redis.conf"
        );

         configGroup.getConfigurations().forEach(config -> {
             if (keyMappings.containsKey(config.getKey())) {
                 List<ConfigurationValue> envValues = config.getValues().stream()
                         .filter(configValue -> configValue.getEnvironmentUuid().equals(environmentTemplateId))
                         .collect(Collectors.toList());

                 MappingConfiguration mappingConfig = MappingConfiguration.builder()
                         .key(keyMappings.get(config.getKey()))
                         .configKeyId(config.getKeyUuid())
                         .configGroupId(configGroupUuid)
                         .isSensitive(config.isSensitive())
                         .isFile(config.isFile())
                         .isDynamic(false)
                         .values(envValues)
                         .keyId("")
                         .build();

                 mappingConfigurations.add(mappingConfig);
             }
         });

         return mappingConfigurations;
    }
}
