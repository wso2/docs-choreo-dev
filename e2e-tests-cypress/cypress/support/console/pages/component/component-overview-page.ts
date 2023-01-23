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

  static navigateToOverview() {
    cy.get("[data-cyid=link-overview]").click();
  }

  static navigateToDeploy() {
    cy.get("[data-cyid=link-deploy]").click();
  }

  static navigateToDeployFromTest() {
    cy.get("[data-testid=deploy-link]").click();
  }

  static navigateToTest() {
    cy.contains("Test").should("be.visible").click({force: true});
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

  static navigateToDevelop() {
    cy.get('[data-cyid="link-develop"]').click();
  }

  static navigateToDevPortal() {
    cy.get("[data-cyid='developer-portal-link']")
      .invoke("attr", "href")
      .then((href) => cy.visit(href));
    return cy.get("header>div>div>p").invoke("text");
  }

  static verifyResource(validateResourceName: string = "") {
    let resourceIdentifier = "resource-/intensity";
    if (validateResourceName) {
      resourceIdentifier = "resource-/" + validateResourceName;
    }

    cy.get(`[data-testid="${resourceIdentifier}"]`);
  }

  static getComponentName() {
    return cy
      .get("#root .MuiCardHeader-content span")
      .invoke("text")
      .then((text) => text.replace("overview", "").trim());
  }

  static createNewVersion(version: string, newBranch: string) {
    cy.get('[data-cyid="version-picker"]').click();
    cy.get("[data-cyid=btn-create-version]").click();

    if (newBranch) {
      this.createNewVersionRestApi(version, newBranch);
    } else {
      this.createNewVersionApiProxy(version);
    }
  }

  private static createNewVersionApiProxy(version: string) {
    cy.contains("Create new version", { timeout: 180000 });
    cy.get('[data-cyid="text-field-new-version"]').within(e=>{cy.get("input").clear().type(version)})
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }

  private static createNewVersionRestApi(version: string, branch: string) {
    cy.get('[data-testid="dialog-close-icon"]').should("exist");
    cy.get('div>[aria-label="Without label"]').click();
    cy.get(`[data-value=${branch}]`).click();
    cy.get('[data-cyid="text-field-new-version"]>div>input')
      .clear()
      .type(version);
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }
}
