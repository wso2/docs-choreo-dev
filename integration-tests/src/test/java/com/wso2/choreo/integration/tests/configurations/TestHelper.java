
/*
 * Copyright (c) 2025, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.configurations;

import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfig;
import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfigValue;

public final class TestHelper {
    public static final String EXPECTED_API_RESPONSE = "{\"user-info\": \"YmFzaWMtaW5mbzoKICBhZ2U6IDMwCiAgaXNfcmVnaXN0ZXJlZDogdHJ1ZQogIG5hbWU6ICJKb2huIERvZSIKICBzZXg6ICJtYWxlIgpvY2N1cGF0aW9uOgogIGNvbXBhbnk6ICJ3c28yIgogIHRpdGxlOiAiU0UiCg==\", \"index\": \"1\"}";

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

}
