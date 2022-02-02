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
    cy.contains("Go Back").should("be.visible").click();
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
    cy.contains("Manage").should("be.visible").click();
  }

  static navigateToObserve() {
    cy.contains("Observe").should("be.visible").click();
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

  static createNewVersion() {
    cy.get("#version-picker").click();
    cy.get("[data-cyid=btn-create-version]").click();
    cy.get("[data-cyid=text-field-new-version]").within(() => {
      cy.get("input").clear();
      cy.get("input").type("1.0.1");
    });
    cy.get("[data-testid=create-version-create]").click();
<<<<<<< HEAD
    cy.intercept(
      "GET",
      "https://sts.preview-dv.choreo.dev/api/am/publisher/v2/apis/*/swagger?organizationId=*"
    ).as("version");

    cy.wait("@version", { timeout: 30000 });
=======
    cy.wait(5000);
>>>>>>> 2a6565f55b42ced9fea9abe7ec364ca8450519f4
  }
}
