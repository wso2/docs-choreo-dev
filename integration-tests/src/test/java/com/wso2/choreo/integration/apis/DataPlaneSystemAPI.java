/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis;

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;

public abstract class DataPlaneSystemAPI {
    protected static final String CHOREO_SYSTEM_API_PREFIX = Configuration
            .getConfig(ConfigDefinition.CHOREO_SYSTEM_API_PREFIX);

    public static final String CHOREO_EU_DP_URL = CHOREO_SYSTEM_API_PREFIX + Configuration
            .getConfig(ConfigDefinition.CHOREO_EU_DP_URL) + "/systemapis";
    public static final String CHOREO_US_DP_URL = CHOREO_SYSTEM_API_PREFIX + Configuration
            .getConfig(ConfigDefinition.CHOREO_US_DP_URL) + "/systemapis";
}
