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

import {
  SETTINGS_TEXT,
  INVITATION_EMAIL,
} from "../../../support/console/pages/component/common/constants";
import { LoginPage } from "../../../support/console/pages/login-page";
import { HomePage } from "../../../support/console/pages/home/home-page";
import { OrganizationComponent } from "../../../support/console/pages/component/common/organization-components";

/// <reference types="cypress" />

describe("Invite members", () => {
  const FILE_ID = "Invite members";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  beforeEach(() => {
    HomePage.navigateToSettings();
  });

  it("Invite a member", () => {
    OrganizationComponent.inviteMembers(INVITATION_EMAIL, "API Publisher");
    OrganizationComponent.selectPendingInvitation();
    cy.contains("td", INVITATION_EMAIL).should("be.visible");
    OrganizationComponent.deleteRecord(INVITATION_EMAIL);
  });

  after(() => {
    HomePage.logout(FILE_ID);
  });
});
