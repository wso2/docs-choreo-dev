/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { TestIds } from "../../constants/TestIds";

export class DevPortalLeftMenu {
  navigateToOverview() {
    cy.get('[data-testid="overview-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToSandboxCredentials() {
    cy.get('[data-testid="sandbox-credentials-menu-item"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToProductionCredentials() {
    cy.get('[data-testid="production-credentials-menu-item"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToTryOut() {
    cy.get('[data-testid="tryout-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToDocuments() {
    cy.get('[data-testid="documents-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToSDKs() {
    cy.get('[data-testid="sdks-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToContracts() {
    cy.get('[data-testid="contracts-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }
}
