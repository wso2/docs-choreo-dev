/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common;

import com.wso2.choreo.integration.apis.marketplace.ConnectionService;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzConstants;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzUtils;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.marketplace.ConnectionInfo;
import com.wso2.choreo.integration.models.resourceAuthorization.Group;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupWithUsersDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.Role;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleAssociation;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Is responsible for cleaning up reoccurring data that is introduced by integration tests.
 */
public class DataCleaner  {
    private static final Logger log = LogManager.getLogger(DataCleaner.class);
    private static final int hourInMilliseconds = 60 * 60 * 1000;

    public static void removeOldTestData(ChoreoOrganization org) throws Exception {
        TokenHandler tokenHandler = TestContext.getTestUserTokenHandler();
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        DevopsPortalApi.deletePreviousThirdPartyRegistryCredentials(tokenHandler.getTestTokenForCPAPIs(), orgUuid);
        List<ChoreoProject> projects = org.getProjects(tokenHandler.getTestTokenForCPAPIs());

        log.info("Total number of projects: " + projects.size());

        int numberOfTestProjects = 0;
        int numberOfTestProjectsDeleted = 0;
        for (ChoreoProject project: projects) {
            String name = project.getName();

            if (name.startsWith(Constant.TEST_OLD_PROJECT_NAME_PREFIX) ||
                name.startsWith(Constant.TEST_PROJECT_NAME_PREFIX)) {

                ++numberOfTestProjects;
                if (shouldProjectBeDeleted(project.getName())) {
                    //get all connections visible to that project
                    ConnectionInfo[] connectionInfo= ConnectionService.getChoreoConnections(tokenHandler.getTestTokenForCPAPIs(),project.getId());
                    for (ConnectionInfo connection: connectionInfo){
                       ConnectionService.deleteChoreoConnection(tokenHandler.getTestTokenForCPAPIs(),connection.getGroupUuid());
                    }
                    List<ChoreoComponent> components = project.getComponents(tokenHandler.getTestTokenForCPAPIs());

                    for (ChoreoComponent component : components) {
                        project.deleteComponent(tokenHandler.getTestTokenForCPAPIs(), component.getId());
                    }

                    if (org.deleteProject(tokenHandler.getTestTokenForCPAPIs(), project.getId())) {
                        ++numberOfTestProjectsDeleted;
                    }
                }
            }
        }
        log.info("Total number of test projects: " + numberOfTestProjects);
        log.info("Total number of test projects deleted: " + numberOfTestProjectsDeleted);

        removeOldTestDataInOrgLevel(org);
    }

    private static boolean shouldProjectBeDeleted(String projectName) throws ParseException {
        // Projects that can be deleted that were created with the Old project name prefix have already been removed.
        // What remains are those that cannot be deleted due to connectors being published.
        if (projectName.startsWith(Constant.TEST_OLD_PROJECT_NAME_PREFIX)) {
            return false;
        }

        String[] parts = projectName.split("_");
        long createdDateTime = 0;
        if (parts.length >= 3) {
            String timestampPart = parts[1];
            createdDateTime = Long.parseLong(timestampPart);
        } else {
            createdDateTime = Long.parseLong(projectName.split(Constant.TEST_PROJECT_NAME_PREFIX)[1]);
        }

        String dateFormatStr = "Jan 01 2023 00:00:01.000 UTC";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        long twenty23BeginDateTime = dateFormat.parse(dateFormatStr).getTime();

        long currentDateTime = new Date().getTime();

        // Delete projects created after beginning of 2023(some older project data can't be deleted)
        // and are 1 hour older than current time
        return createdDateTime > twenty23BeginDateTime && currentDateTime - createdDateTime > hourInMilliseconds;
    }

    public static void removeOldTestDataInOrgLevel(ChoreoOrganization org) throws Exception {

        // Delete stale groups
        List<Group> groups = ResourceAuthzUtils.getGroups().getList();

        for (Group group : groups) {

            if (!shouldGroupBeDeleted(group.getDisplayName())) {
                continue;
            }

            List<RoleAssociation> roleAssociations = ResourceAuthzUtils.getRolesInGroup(group.getHandle())
                    .getRoleAssociations();
            log.info("Total number of roles to be removed from group: " + roleAssociations.size());

            RoleGroupMappingResponseDTO roleRemovalResponse = null;

            // Remove all the roles in the group so the group can be deleted.
            // Using a single API call to remove all roles in the group
            // because all roles are from the same level (PROJECT).
            if (!roleAssociations.isEmpty()) {
                roleRemovalResponse = ResourceAuthzUtils
                        .removeRoleFromGroupForCleanup(group.getHandle(), roleAssociations);
                if (roleRemovalResponse != null) {
                    log.info("Number of roles removed: " + roleRemovalResponse.getRoleAssociations().size());
                }
            }

            if (roleRemovalResponse != null) {
                GroupWithUsersDTO groupWithUsers = ResourceAuthzUtils.getGroupMembers(group.getHandle());
                // In resource authz test cases, we only have one user in the group
                if (groupWithUsers.getUsers().size() == 1) {
                    log.info("User to be deleted: " + groupWithUsers.getUsers().get(0).getEmail());
                    ResourceAuthzUtils.removeMemberFromGroup(group.getHandle(),
                            groupWithUsers.getUsers().get(0).getIdpId());
                }
            }

            ResourceAuthzUtils.deleteGroup(group.getHandle());
        }

        // Delete stale roles

        List<Role> roles = ResourceAuthzUtils.getRoleList().getList();
        log.info("Total number of roles to be deleted: " + roles.size());

        for (Role role : roles) {

            if (!shouldRoleBeDeleted(role.getDisplayName())) {
                continue;
            }

            ResourceAuthzUtils.deleteRole(role.getHandle());
        }
    }

    private static boolean shouldGroupBeDeleted(String groupName) throws ParseException {

        if (!groupName.startsWith(ResourceAuthzConstants.TestGroupData.GROUP_NAME_BASE)) {
            return false;
        }

        String timestamp = groupName.split("_")[1];

        return !isValidTimestamp(timestamp);
    }

    private static boolean shouldRoleBeDeleted(String roleName) throws ParseException {

        if (!(roleName.startsWith(ResourceAuthzConstants.TestRoleData.ROLE_DISPLAY_NAME_BASE) || roleName
                .startsWith(ResourceAuthzConstants.ProjectViewAndOrgManageRoleData.ROLE_DISPLAY_NAME_BASE))) {
            return false;
        }

        String timestamp = roleName.split("_")[1];

        return !isValidTimestamp(timestamp);
    }

    private static boolean isValidTimestamp(String timestampString) throws ParseException {

        try {
            long timestamp = Long.parseLong(timestampString);
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime oneHourLater = dateTime.plusHours(1);

            return currentTime.isBefore(oneHourLater);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
