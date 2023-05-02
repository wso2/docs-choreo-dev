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

import { LoginPage } from "../../../support/console/pages/login-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { OrganizationComponent } from "../../../support/console/pages/component/common/organization-components";

/// <reference types="cypress" />
let timestamp = Math.floor((+new Date() - 100000) / 1000).toString();
const INVITATION_EMAIL = Cypress.env("invitationUserEmail");

describe("Invite members", () => {
  before(() => {
    LoginPage.login();

  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Invite a member to users org", () => {
    OrganizationComponent.deleteInvitation(INVITATION_EMAIL)
    ChoreoHomePage.navigateToSettings();
    OrganizationComponent.verifyEmailIsNotDisplayed(INVITATION_EMAIL);
    OrganizationComponent.selectPendingInvitation();
    OrganizationComponent.verifyEmailIsNotDisplayed(INVITATION_EMAIL);
    OrganizationComponent.inviteMembers(INVITATION_EMAIL, "API Publisher");
    OrganizationComponent.selectPendingInvitation();
    OrganizationComponent.verifyEmailIsDisplayed(INVITATION_EMAIL);
  });
});

describe("Accept invitation", () => {
  const invited_org_handle = Cypress.env("choreoOrgHandle");

  before(() => {
    LoginPage.acceptInviteAsInvitedUser(timestamp);
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify member is accepted to Org", () => {
    ChoreoHomePage.isOrgHandleVisible(invited_org_handle);
  });
});

describe("Delete members", () => {
  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Delete a member", () => {
    ChoreoHomePage.navigateToSettings();
    OrganizationComponent.navigateToMembers();
    OrganizationComponent.verifyEmailIsDisplayed(INVITATION_EMAIL);
    OrganizationComponent.deleteMember(INVITATION_EMAIL);
  });
});
