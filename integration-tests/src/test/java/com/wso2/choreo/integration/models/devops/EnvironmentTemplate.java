/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.models.devops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentTemplate {
    private UUID id;
    private Instant createdAt;
    private int organizationId;
    private UUID organizationUuid;
    @JsonProperty("env_name")
    private String envName;
    private String region;
    private String choreoEnv;
    private UUID clusterId;
    private UUID dockerCredentialUuid;
    private String externalApimEnvName;
    private String internalApimEnvName;
    private String sandboxApimEnvName;
    private boolean critical;
    private String pdpWebAppDnsPrefix;
    private String deletionStatus;
}
