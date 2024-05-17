/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 * 
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.models.keymanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdpAddRequestDTO {
    private String name;
    private String wellKnownEndpoint;
    private String type;
    private String description;
    private String issuer;
    private String tokenEndpoint;
    private boolean enabled;
    private String tokenType;
    private String alias;
    private KMCertificate certificates;
    private Map<String, Object> additionalProperties;
    private String scopesClaim;
    private String consumerKeyClaim;
    private String authorizeEndpoint;
    private String revokeEndpoint;
    private String logoutEndpoint;
}
