/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.models.Insights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightDTO { 
    private String apiName;
    private String apiVersion;
    private String email;
    private String environmentId;
    private String externalEnvId;
    private String fromTime;
    private String internalEnvId;
    private boolean isLatency;
    private String metric;
    private String orgId;
    private String organization;
    private String sandboxEnvId;
    private String tenant;
    private int threshold;
    private String toTime;
}

