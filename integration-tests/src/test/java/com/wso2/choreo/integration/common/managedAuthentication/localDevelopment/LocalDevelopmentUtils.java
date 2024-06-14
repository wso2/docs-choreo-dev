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

package com.wso2.choreo.integration.common.managedAuthentication.localDevelopment;

import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.DisableLocalDevelopmentDefaultConfigs;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.EnableLocalDevelopmentCustomConfigs;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.EnableLocalDevelopmentDefaultConfigs;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.EnableLocalDevelopmentRequestParams;

import java.util.HashMap;

/**
 * Utility class for Local Development related tests.
 */
public class LocalDevelopmentUtils {

    public static HashMap<String, Object> getEnableLocalDevelopmentWithDefaultConfigRequest() {

        HashMap<String, Object> configRequest = new HashMap<>();
        configRequest.put(EnableLocalDevelopmentRequestParams.ENABLE,
                EnableLocalDevelopmentDefaultConfigs.ENABLE);
        configRequest.put(EnableLocalDevelopmentRequestParams.ALLOWED_URIS,
                EnableLocalDevelopmentDefaultConfigs.ALLOWED_URIS);

        return configRequest;
    }

    public static HashMap<String, Object> getDisableLocalDevelopmentWithDefaultConfigRequest() {

        HashMap<String, Object> configRequest = new HashMap<>();
        configRequest.put(EnableLocalDevelopmentRequestParams.ENABLE,
                DisableLocalDevelopmentDefaultConfigs.ENABLE);
        configRequest.put(EnableLocalDevelopmentRequestParams.ALLOWED_URIS,
                DisableLocalDevelopmentDefaultConfigs.ALLOWED_URIS);

        return configRequest;
    }

    public static HashMap<String, Object> getEnableLocalDevelopmentWithCustomConfigRequest() {

        HashMap<String, Object> configRequest = new HashMap<>();
        configRequest.put(EnableLocalDevelopmentRequestParams.ENABLE,
                EnableLocalDevelopmentCustomConfigs.ENABLE);
        configRequest.put(EnableLocalDevelopmentRequestParams.ALLOWED_URIS,
                EnableLocalDevelopmentCustomConfigs.ALLOWED_URIS);

        return configRequest;
    }
}
