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

  it("Delete existing invitation", () => {
    OrganizationComponent.deleteInvitation(INVITATION_EMAIL)
  });

  it("Invite a member to users org", () => {
    ChoreoHomePage.navigateToSettings();
    OrganizationComponent.verifyEmailIsNotDisplayed(INVITATION_EMAIL);
    OrganizationComponent.selectPendingInvitation();
    OrganizationComponent.verifyEmailIsNotDisplayed(INVITATION_EMAIL);
    OrganizationComponent.inviteMembers(INVITATION_EMAIL, "API Publisher");
    OrganizationComponent.selectPendingInvitation();
    cy.get('[data-cyid="search-app"]').clear().type(INVITATION_EMAIL);
    OrganizationComponent.verifyEmailIsDisplayed(INVITATION_EMAIL);
  });

  it("Accept the invitation and open Register page", () => {
    LoginPage.acceptInviteAsInvitedUser(timestamp);
  });
});
