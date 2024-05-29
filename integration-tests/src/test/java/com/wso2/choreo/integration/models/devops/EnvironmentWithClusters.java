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

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentWithClusters {
    @JsonProperty("ID")
    private String ID;
    @JsonProperty("CreatedAt")
    private String CreatedAt;
    @JsonProperty("UpdatedAt")
    private String UpdatedAt;
    private String metadata;
    private String name;
    private String organization_id;
    private String project_id;
    private String description;
    private List<EnvironmentCluster> environment_clusters;
    private String namespace;
    private String namespaces;
    private boolean critical;
    private String auto_maintainance;
    private String app_selector;
    private List<String> promote_from;
    private boolean org_shared;
    private String choreo_env;
    private String apim_env_id;
    private String external_apim_env_name;
    private String internal_apim_env_name;
    private String sandbox_apim_env_name;
    private boolean disabled;
    private boolean is_migrating;
    private String copied_from;
    private String template_id;
    private String cr_credential_id;
    private String web_apps_vhost;
    private boolean cilium_enabled;
    private boolean scale_to_zero_enabled;
    private String gateway_type;
}
