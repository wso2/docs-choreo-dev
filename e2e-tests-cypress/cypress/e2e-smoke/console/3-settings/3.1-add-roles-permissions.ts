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

import { OrganizationComponent } from "../../../support/console/pages/component/common/organization-components";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";

/// <reference types="cypress" />

describe("Add roles and permissions", () => {
  const FILE_ID = "1.1-add-roles-permissions";

  const roleName = "E2EtestRole";
  const roleDescription = "This Role is created by E2E test run.";
  const roleTag = "testRoleTag";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.navigateToSettings();
    OrganizationComponent.navigateToRoles();
    OrganizationComponent.deleteRoleIfExists(roleName);
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Create a role", () => {
    OrganizationComponent.createRole(roleName, roleDescription, roleTag);
  });

  it("Add a member to new role", () => {
    OrganizationComponent.addMembertoRole(roleName);
  });

  it("Delete created role", () => {
    OrganizationComponent.navigateToRoles();
    OrganizationComponent.deleteCreatedRole(roleName);
  });
});
