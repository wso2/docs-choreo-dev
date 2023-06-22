/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

import { LoginPage } from "../../../support/console/pages/login-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { OrganizationComponent } from "../../../support/console/pages/component/common/organization-components";

/// <reference types="cypress" />
const groupName = "Engineer";
const oldRoles = ["API Publisher", "Developer"];
const newRoles = ["API Subscriber", "Admin"];

describe("Test group role mappings", () => {
  before(() => {
    LoginPage.login();
    ChoreoHomePage.navigateToSettings();
    OrganizationComponent.navigateToRoleMapping();

  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Add a group role mapping", () => {
    OrganizationComponent.addMappings(groupName, oldRoles);
    OrganizationComponent.verifyGroupNameIsDisplayed(groupName);
  });

  it("Update a group role mapping", () => {
    OrganizationComponent.updateMappings(groupName, oldRoles, newRoles);
  });

  it("Delete a group role mapping", () => {
    OrganizationComponent.verifyGroupNameIsDisplayed(groupName);
    OrganizationComponent.deleteCreatedMapping(groupName);
    OrganizationComponent.verifyGroupNameIsNotDisplayed(groupName);
  });
});
