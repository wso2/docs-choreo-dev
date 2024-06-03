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

import java.util.List;

public class LocalDevelopmentConstants {

    public static class LocalDevelopmentProxyHeaders {
        
        public static final String LOCAL_DEV_MODE = "X-Use-Local-Dev-Mode";
    }

    public static class EnableLocalDevelopmentRequestParams {

        public static final String ENABLE = "enable";
        public static final String ALLOWED_URIS = "allowedUris";
    }

    public static class EnableLocalDevelopmentDefaultConfigs {

        public static final boolean ENABLE = true;
        public static final List<String> ALLOWED_URIS = List.of("https://localhost:10000");
    }

    public static class DisableLocalDevelopmentDefaultConfigs {

        public static final boolean ENABLE = false;
        public static final List<String> ALLOWED_URIS = List.of("https://localhost:10000");
    }

    public static class EnableLocalDevelopmentCustomConfigs {

        public static final boolean ENABLE = true;
        public static final List<String> ALLOWED_URIS = List.of("https://localhost:9000");
    }
}
