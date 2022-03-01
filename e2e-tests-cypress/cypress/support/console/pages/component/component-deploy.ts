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

export class ComponentDeployPage {
  static deploy() {
    cy.get('[data-cyid="btn-deploy-api"]').should("be.visible").click();
  }

  static configureAndDeploy(configValue: string) {
    cy.wait(8000);
    cy.get('[data-cyid="btn-deploy-api"]').click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(
      '[class="MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputMarginDense MuiOutlinedInput-inputMarginDense"]'
    ).type(value);
    cy.get('button[type="submit"]').click();
  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]', { timeout: 90000 });
  }

  static ismanualDeploymentSuccessful() {
    return cy.get('[title="Deployed successfully"]', { timeout: 90000 });
  }

  static promoteToProd() {
    cy.get('[data-cyid*="promote"]', { timeout: 120000 })
      .should("be.visible")
      .click();

    cy.get("body").then((b) => {
      if (
        b.find('[data-cyid="btn-deploy-api"]').text() === "Configure & Deploy"
      ) {
        cy.contains("Next").click();
        cy.get(".ConfigForm button", { timeout: 120000 })
          .contains("Promote")
          .click();
      }
    });
  }

  static verifyDevInvokeURL() {
    return cy
      .get('[data-cyid="text-field-invoke-url"] input', { timeout: 120000 })
      .eq(0)
      .invoke("attr", "value");
  }

  static verifyProdInvokeURL() {
    cy.get('[data-cyid="deployment-status"]')
      .should("have.length", 2)
      .eq(1)
      .contains("Active")
      .should("be.visible");

    cy.get('[data-cyid="text-field-invoke-url"] input', {
      timeout: 120000,
    }).should("have.length", 2);

    return cy
      .get('[data-cyid="text-field-invoke-url"] input')
      .eq(1)
      .invoke("attr", "value");
  }

  static stopDevDeployment() {
    cy.get("body").then((body) => {
      if (body.find('[data-cyid="btn-stop-redeploy"]')) {
        cy.get('[data-cyid="btn-stop-redeploy"]').eq(0).click();
        return cy
          .get('[data-cyid="btn-stop-redeploy"]>button>span')
          .eq(0)
          .invoke("text");
      }
    });
    cy.get('[data-cyid="btn-stop-deployment"]').eq(0).click();
    return cy
      .get('[data-cyid="btn-stop-deployment"]>span')
      .eq(0)
      .invoke("text");
  }
  static stopProdDeployment() {
    cy.get("body").then((body) => {
      if (body.find('[data-cyid="btn-stop-redeploy"]')) {
        cy.get('[data-cyid="btn-stop-redeploy"]').eq(1).click();
        return cy
          .get('[data-cyid="btn-stop-redeploy"]>button>span')
          .eq(1)
          .invoke("text");
      }
    });
    cy.get('[data-cyid="btn-stop-deployment"]')
      .should("have.length", 2)
      .eq(1)
      .click();
    cy.get('[data-cyid="text-field-invoke-url"]').should("have.length", 2);
    return cy
      .get('[data-cyid="btn-stop-deployment"]>span')
      .eq(1)
      .invoke("text");
  }
}
