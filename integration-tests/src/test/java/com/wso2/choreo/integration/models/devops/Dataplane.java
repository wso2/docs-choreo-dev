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

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataplane {
    private String id;
    private String name;
    private String type;
    private String createdOn;
    private Map<String, Boolean> labels;
    @JsonProperty("isActive")
    private boolean isActive;
    private String externalGatewayVirtualHost;
    private String internalGatewayVirtualHost;
    private String externalIngressDefaultDomain;
    private String gatewayType;
    private boolean ciliumEnabled;
    private boolean scaleToZeroEnabled;
    private String stsDefaultDomain;
}
