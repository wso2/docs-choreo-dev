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

import { console } from "../../../support/console/console";

describe("Add roles and permissions", () => {
  const newGroup = "E2EtestGroup";
  const newRole = "E2EtestRole";
  const existingRole = "API Publisher";
  const roleList = [newRole, existingRole];
  const groupDescription = "This Group is created by E2E test run.";
  const roleDescription = "This Role is created by E2E test run.";
  const roleTag = "testRoleTag";

  it("Login to Console", () => {
    console.login();
    console.deleteRoleIfExists(newRole);
    console.deleteGroupIfExists(newGroup);
  });

  it("Create a role", () => {
    console.addRole(newRole, roleDescription, roleTag);
  });

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

  it("Add a member to the group", () => {
    console.addCurrentUserToGroup(newGroup);
  });

  it("Check member is in group", () => {
    console.checkCurrentUserIsInGroup(newGroup);
  });

  it("Delete created group and role", () => {
    console.deleteRole(newRole);
    console.deleteGroup(newGroup);
  });
});
