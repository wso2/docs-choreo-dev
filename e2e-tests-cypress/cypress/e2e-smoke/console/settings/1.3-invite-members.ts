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
let timestamp = "";
const INVITATION_EMAIL = Cypress.env("invitationUserEmail");

describe("Invite members", () => {
  const FILE_ID = "1.3.1-invite-members";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  beforeEach(() => {
    ChoreoHomePage.navigateToSettings();
  });

  it("Invite a member", () => {
    cy.contains("td", INVITATION_EMAIL).should("not.exist");
    OrganizationComponent.selectPendingInvitation();
    cy.contains("td", INVITATION_EMAIL).should("not.exist");
    timestamp = Math.floor((+ new Date())/1000).toString();
    OrganizationComponent.inviteMembers(INVITATION_EMAIL, "API Publisher");
    OrganizationComponent.selectPendingInvitation();
    cy.contains("td", INVITATION_EMAIL).should("be.visible");
  });

  after(() => {
    ChoreoHomePage.logout(FILE_ID);
  });
});

describe("Accept invitation", () => {
  const invited_org_handle = Cypress.env("choreoOrgHandle");
  const FILE_ID = "1.3.2-accept-invitation";

  before(() => {
    LoginPage.loginToInvitedUser(FILE_ID, timestamp);
  });

  beforeEach(() => {
    ChoreoHomePage.navigateToSettings();
  });

  it("Accept the invitation", () => {
    cy.reload();
    cy.get('[id="org-picker"]').should('be.visible');
    cy.get('[id="org-picker"]').click();
    cy.get('[data-value="' + invited_org_handle + '"]').should('be.visible');
  });

  after(() => {
    ChoreoHomePage.logout(FILE_ID);
  });
});

describe("Delete members", () => {
  const FILE_ID = "1.3.3-delete-members";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  beforeEach(() => {
    ChoreoHomePage.navigateToSettings();
  });

  it("Delete a member", () => {
    cy.contains('td', INVITATION_EMAIL).should('exist');
    cy.log("Member invitation accepted successfully");
    OrganizationComponent.deleteMember(INVITATION_EMAIL);
  });

  after(() => {
    ChoreoHomePage.logout(FILE_ID);
  });
});
