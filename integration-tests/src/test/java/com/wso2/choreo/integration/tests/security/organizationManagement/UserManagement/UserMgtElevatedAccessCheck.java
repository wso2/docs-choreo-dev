package com.wso2.choreo.integration.tests.security.organizationManagement.UserManagement;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class UserMgtElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgHandle;
    private static String idpId;
    private static String roleName;
    private static String roleDescription;
    private static String rolePermissionId;
    private static String roleDomainArea;
    private static String roleHandle;
    private static String userId;
    private static String groupName;
    private static String invitedUserEmail;
    private static String invitationUuid;
    private static String adminEmail;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_UserMgtElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        idpId = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_IDP_ID);
        roleName = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ROLE_NAME);
        roleDescription = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ROLE_DESCRIPTION);
        rolePermissionId = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ROLE_PERMISSION_ID);
        roleDomainArea = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ROLE_DOMAIN_AREA);
        roleHandle = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ROLE_HANDLE);
        userId = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_USER_ID);
        groupName = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_GROUP_NAME);
        invitedUserEmail = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_INVITED_USER_EMAIL);
        invitationUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_INVITATION_UUID);
        adminEmail = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ADMIN_EMAIL);
    }

    @Test
    @CitrusTest
    public void getUserDetailByUUId_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/users/" + idpId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

    @Test
    @CitrusTest
    public void deleteOrganizationMemberByUserIdpId_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/users/" + idpId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

    @Test
    @CitrusTest
    public void updateRolesOfUserByIdpId_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/users/" + idpId + "/roles";
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "updateRoles.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRolesByOrgHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles?include=members";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

    @Test
    @CitrusTest
    public void createRoleInAnOrganization_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles";
        Map<String, String> params = new HashMap<>();
        params.put("CREATE_ROLE_NAME", roleName);
        params.put("CREATE_ROLE_DESCRIPTION", roleDescription);
        params.put("CREATE_ROLE_PERMISSION_ID", rolePermissionId);
        params.put("CREATE_ROLE_DOMAIN_AREA", roleDomainArea);
        params.put("CREATE_ROLE_HANDLE", roleHandle);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "createRole.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRoleDetailsByRoleHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles/" + roleHandle + "?include=permissions";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

    @Test
    @CitrusTest
    public void updateOrganizationRoleByHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles/" + roleHandle;
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "updateOrganizationRoleByHandle.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateRolePermissionsByRoleHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles/" + roleHandle;
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "updateRolePermissionsByRoleHandle.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteAnOrganizationRoleByRoleHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles/" + roleHandle;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void updateMembersOfAnOrganiizationByRoleHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/roles/" + roleHandle + "/users";
        Map<String, String> params = new HashMap<>();
        params.put("USER_TO_ADD_TO_THE_ROLE", userId);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "updateMembersOfAnOrganiizationByRoleHandle.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void listGroupRoleMappingsByOrgHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/group-role-mappings";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void createGroupRoleMappingForOrg_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/group-role-mappings";
        Map<String, String> params = new HashMap<>();
        params.put("GROUP_NAME", groupName);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "createGroupRoleMappingForOrg.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateGroupRoleMappingForOrgByGroupName_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/group-role-mappings/" + groupName;
        Map<String, String> params = new HashMap<>();
        params.put("GROUP_NAME", groupName);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "updateGroupRoleMappingForOrgByGroupName.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteGroupRoleMappingForOrgByGroupName_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/group-role-mappings/" + groupName;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getMemberInvitationsByOrgHandle_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/invitations";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

    @Test
    @CitrusTest
    public void inviteMemberToTheOrg_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/invitations";
        Map<String, String> params = new HashMap<>();
        params.put("INVITED_USER_EMAIL", invitedUserEmail);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement/" +
                "inviteMemberToTheOrg.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteMemberInvitation_UserMgtElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.USER_MGT_SUFFIX + orgHandle + "/invitations?email=" + invitedUserEmail;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl,
                accessToken);
    }

}
