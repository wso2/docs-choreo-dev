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

export class ComponentOverviewPage {
  static goBack() {
    cy.get("button>span>p").contains("Components").should("be.visible").click();
  }

  static navigateToDevelop() {
    cy.get("[data-cyid=link-develop]").click();
  }

  static navigateToDeploy() {
    cy.get("[data-cyid=link-deploy]", { timeout: 180000 }).click();
  }

  static navigateToDeployFromTest() {
    cy.get("[data-testid=deploy-link]").click();
  }

  static navigateToTest() {
    cy.contains("Test").should("be.visible").click();
  }

  static navigateToManage() {
    cy.contains("Manage").should("be.visible").click({ force: true });
  }

  static navigateToObserve() {
    cy.get("[data-cyid=link-observe]").click();
  }

  static navigateToDevops() {
    cy.contains("Devops").should("be.visible").click();
  }

  static getComponentName() {
    return cy
      .get("#root .MuiCardHeader-content span")
      .should("be.visible")
      .invoke("text")
      .then((text) => text.replace("overview", "").trim());
  }

  static createNewVersion(version: string) {
    cy.get("#version-picker").click();
    cy.get("[data-cyid=btn-create-version]").click();
    cy.get("[data-cyid=text-field-new-version]").within(() => {
      cy.get("input").clear();
      cy.get("input").type(version);
    });
    cy.get("[data-testid=create-version-create]").click();
    cy.intercept({
      method: "GET",
      url: `${Cypress.env(
        "apimSvcURL"
      )}/api/am/publisher/v2/apis/*/swagger?organizationId=*`,
      times: 1,
    }).as("version");
    cy.wait("@version", { timeout: 120000 });
    cy.wait(5000);
  }
}
