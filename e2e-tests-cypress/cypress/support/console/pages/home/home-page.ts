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

export class HomePage {
  static username = "button[aria-haspopup]>span>p";

  static navigateToHome() {
    cy.get('[data-testid="main-left-nav-item-Home"]')
      .should("be.visible")
      .click();
  }

  static navigateToProjects(fileID) {
    cy.get('[data-testid="main-left-nav-item-Components"]')
      .should("be.visible")
      .click();
    this.interceptProjects(fileID);
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

  static getLoggedUsername() {
    this.navigateToHome();
    return cy.get(this.username).invoke("text");
  }

  static getLoggedUserEmail() {
    this.clickOnLoggedInUser();
    return cy.get("ul>li>div>p").invoke("text");
  }

  static clickOnLoggedInUser() {
    cy.get(this.username).click();
  }

  static logout(fileID) {
    cy.task("deleteFile", fileID);
    this.clickOnLoggedInUser();
    cy.contains("Logout").click();
  }

  static navigateToSettings() {
    this.clickOnLoggedInUser();
    cy.get('[data-testid="header-user-profile-item-settings"]')
      .should("be.visible")
      .click();
  }

  private static interceptProjects(fileID) {
    cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("projects");
    return cy.wait("@projects", { timeout: 20000 }).then((e) => {
      const { projects } = e.response.body.data;
      cy.task("writeTestData", {
        fileName: fileID,
        key: "projects",
        value: projects,
      });
    });
  }
}
