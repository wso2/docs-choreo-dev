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

export class ChoreoHomePage {
  static username = '[data-testid="header-user-profile-menu"]';

  static navigateToHome() {
    const handle = Cypress.env("userData")["handle"];
    cy.get(`[href="/organizations/${handle}/home"]`).eq(1).click();
  }

  static navigateToComponents() {
    cy.get('[data-testid="main-left-nav-item-Components"]')
      .should("be.visible")
      .click();
  }

  static selectHomeMenu() {
    cy.get('a>[alt="Choreo Logo"]').click();
  }

  static navigateToMarketPlace() {
    cy.get('[data-testid="main-left-nav-item-Marketplace"]')
      .should("be.visible")
      .click();
  }

  static navigateToInsights() {
    cy.get('[data-testid="main-left-nav-item-Insights"]')
      .should("be.visible")
      .click();
  }

  static isOrgHandleVisible(orgHandle: string) {
    cy.get('[id="org-picker"]').should("be.visible");
    cy.get('[id="org-picker"]').click();
    cy.get('[data-value="' + orgHandle + '"]').should("be.visible");
  }

  static getLoggedUserEmail() {
    cy.get(this.username).should("be.visible").click();
    return cy.get("ul>li>div>p").invoke("text");
  }

  static logout() {
    cy.request(Cypress.env("sign_out_url"));
  }

  static navigateToSettings() {
    cy.get('[data-testid="main-left-nav-item-Settings"]', {
      timeout: 120000,
    }).click();
  }
}
