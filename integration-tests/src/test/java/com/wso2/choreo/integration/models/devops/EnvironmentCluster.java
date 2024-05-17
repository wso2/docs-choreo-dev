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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentCluster {
    @JsonProperty("ID")
    private String ID;
    @JsonProperty("CreatedAt")
    private String CreatedAt;
    @JsonProperty("UpdatedAt")
    private String UpdatedAt;
    private String metadata;
    private String cluster;
    private String cluster_id;
    private String environment_id;
    private String environment;
    private String namespace;
    private String mode;
    private String config_override;
    private String secret_override;
}
