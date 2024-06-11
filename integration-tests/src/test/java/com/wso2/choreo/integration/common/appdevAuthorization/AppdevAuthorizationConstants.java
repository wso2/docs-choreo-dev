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

package com.wso2.choreo.integration.common.appdevAuthorization;

public class AppdevAuthorizationConstants {

    public static final String TEST_PROJECT_REGION = "US";
    public static final String DEV_ENVIRONMENT_NAME = "Development";

    public static class TestRoleData {

        public static final String NAME = "Test-Role";
        public static final String UPDATED_NAME = "Updated-Test-Role";
        public static final String DESCRIPTION = "A sample role";
    }

    public static class TestGroupData {

        public static final String NAME = "Test-Group";
    }

    public static class TestPermissionData {

        public static final String PERMISSION = "urn:choreointegrationtestsuserdevdlwqo:testgreetingservice:Edit-Items";
        public static final String UPDATED_PERMISSION = "urn:choreointegrationtestsuserdevdlwqo:testgreetingservice:Edit-Items-Updated";
    }
}
