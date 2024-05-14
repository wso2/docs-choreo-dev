/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Utils } from "../../../support/commons/utils";
import { console } from "../../../support/console/console";
import { OrganizationComponent } from "../../../support/console/pages/component/common/organization-components";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";

describe("Add roles and permissions", () => {
  const newGroup = "E2EtestGroup";
  const newRole = "E2EtestRole";
  const existingRole = "API Publisher";
  const roleList = [newRole, existingRole];
  const groupDescription = "This Group is created by E2E test run.";
  const roleDescription = "This Role is created by E2E test run.";
  const roleTag = "testRoleTag";

  it("Login to Console", () => {
    if (Utils.isNewUserManagementEnabled()) {
      console.login();
      console.deleteGroupIfExists(newGroup);
      console.deleteRoleIfExists(newRole);
    } else {
      LoginPage.login();
      ChoreoHomePage.navigateToSettings();
      OrganizationComponent.navigateToRoles();
      OrganizationComponent.deleteRoleIfExists(newRole);
    }
  });

  it("Create a role", () => {
    if (Utils.isNewUserManagementEnabled()) {
      console.addRole(newRole, roleDescription, roleTag);
    } else {
      OrganizationComponent.createRole(newRole, roleDescription, roleTag);
    }
  });

  if (Utils.isNewUserManagementEnabled()) {
    it("Create a group", () => {
      console.addGroup(newGroup, groupDescription);
    });

    it("Add role to the group", () => {
      console.addRolesToGroup(roleList, newGroup);
    });

    it("Remove role from the group", () => {
      console.removeRolesFromGroup([existingRole], newGroup);
      console.checkRolesInGroup([newRole], newGroup);
    });
  }

  it("Add a member to the group", () => {
    if (Utils.isNewUserManagementEnabled()) {
      console.addCurrentUserToGroup(newGroup);
    } else {
      OrganizationComponent.addMembertoRole(newRole);
    }
  });

  it("Check member is in group", () => {
    if (Utils.isNewUserManagementEnabled()) {
      console.checkCurrentUserIsInGroup(newGroup);
    } else {
      OrganizationComponent.navigateToMembers();
      OrganizationComponent.checkMemberRole(newRole);
    }
  });

  it("Delete created group and role", () => {
    if (Utils.isNewUserManagementEnabled()) {
      console.deleteGroup(newGroup);
      console.deleteRole(newRole);
    } else {
      OrganizationComponent.navigateToMembers();
      OrganizationComponent.navigateToRoles();
      OrganizationComponent.deleteCreatedRole(newRole);
    }
  });
});
