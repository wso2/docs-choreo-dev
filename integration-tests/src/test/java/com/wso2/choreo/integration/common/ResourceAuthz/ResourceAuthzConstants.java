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

package com.wso2.choreo.integration.common.ResourceAuthz;

import com.wso2.choreo.integration.models.resourceAuthorization.Permission;

import java.util.ArrayList;
import java.util.List;

public class ResourceAuthzConstants {

    public static String TEST_USER_ID = "ab003ea2-da73-47af-a574-0e4369fe665f";

    public static String DEVELOPER_ROLE_HANDLE = "developer";

    public static String ADMIN_ROLE_HANDLE = "admin";

    public enum RoleGroupMappingLevels {

        ORG, PROJECT
    }

    public static class TestProjectData {

        public static final String PROJECT_NAME_BASE = "authztestproject_";
        public static final String PROJECT_DESCRIPTION = "Test Project Description";
        public static final String REGION = "US";
    }

    public static class TestGroupData {

        public static final String GROUP_NAME_BASE = "AuthzTestGroup_";
        public static final String GROUP_DESCRIPTION = "Test Group Description";
    }

    public static class TestRoleData {

        public static final String ROLE_DESCRIPTION = "Test Role Description";
        public static final String ROLE_DISPLAY_NAME_BASE = "AuthzTestRole_";
        public static final List<Permission> PERMISSIONS = getOrgManagePermissions();
    }

    public static class ProjectViewAndOrgManageRoleData {

        public static final String ROLE_DESCRIPTION 
            = "This role allows project view permission and org manage permission";
        public static final String ROLE_DISPLAY_NAME_BASE = "AuthzPVOM_";
        public static final List<Permission> PERMISSIONS = getProjectViewOrgManagePermission();
    }

    private static List<Permission> getOrgManagePermissions() {
        
        return new ArrayList<>() {
            {
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:theme_manage", "102"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:theme_view", "103"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:theme_create", "104"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:theme_delete", "105"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:theme_deploy", "106"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:self_signup_manage",
                        "107"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:self_signup_config_view",
                        "108"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:self_signup_approval_view",
                        "109"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:self_signup_approval_update",
                        "110"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:self_signup_config_update",
                        "111"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:enterprise_login_config_manage",
                        "112"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationmanagement:enterprise_login_config_view",
                        "113"));
                add(new Permission("ORGANIZATION-MANAGEMENT",
                        "urn:choreocontrolplane:organizationapi:org_manage",
                        "60"));
            }
        };
    }

    private static List<Permission> getProjectViewOrgManagePermission() {

        List<Permission> permissionList = getOrgManagePermissions();
        permissionList.add(new Permission("PROJECT-MANAGEMENT",
                "choreo:project_view", "187"));
        return permissionList;
    }

}
