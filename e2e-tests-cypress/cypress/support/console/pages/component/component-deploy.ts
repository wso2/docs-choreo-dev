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
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 180000 })
      .should("be.enabled")
      .click();
 //   this.hideSharePopover();
  }

  static deployScheduleTask() {
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 180000 })
      .should("be.enabled")
      .click();
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', { timeout: 180000 }).should(
      "have.length",
      1
    );
    cy.wait(2000);
 //   this.hideSharePopover();
  }

  static promoteScheduleTask() {
    cy.get('[data-cyid*="promote"]', { timeout: 180000 })
      .eq(0)
      .should("be.visible")
      .click();
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]').should("have.length", 2);
  }

  static configureAndDeploy(configValue: string) {
    cy.wait(8000);
    cy.get('[data-cyid="btn-deploy-api"]').click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
  //  this.hideSharePopover();
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(".ConfigForm").then((frm) => {
      const le = frm.find('[placeholder="Required value"]').length;
      if (!le) {
        cy.get(".ConfigForm .MuiIconButton-label").click();
        cy.get('[placeholder="Required value"]').type(value);
      } else {
        cy.get('[placeholder="Required value"]').type(value);
      }
    });
    cy.get('button[type="submit"]').click();
  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]', { timeout: 90000 });
  }

  static promoteToProd() {
    cy.get('[data-testid="undeploy-info"]', { timeout: 180000 }).should(
      "be.visible"
    );
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]', { timeout: 180000 })
      .eq(0)
      .should("be.visible")
      .click();
  }

  static promoteManualTriggerToProd() {
    cy.get('[data-cyid*="promote"]', { timeout: 180000 }).should("be.visible");
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).should("be.enabled").click();
  }

  static promoteWebHookToProd() {
    this.promoteToProd();
    cy.get(".MuiCardContent-root button").contains("Next").click();
    cy.get(".ConfigForm button", { timeout: 120000 })
      .contains("Promote")
      .click();
  }

  static verifyDevInvokeURL() {
    return cy
      .get('[data-cyid="text-field-invoke-url"] input', { timeout: 180000 })
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

  static stopAllDeployment() {
    cy.wait(3000);
    this.stopDevContainer();
    this.stopStgContainer();
    this.stopProdContainer();
  }

  private static stopDevContainer() {
    cy.get(".MuiButton-label").contains("Stop").eq(0).click();
  }

  private static stopProdContainer() {
    cy.get(".MuiButton-label").contains("Stop").eq(0).click();
  }

  private static stopStgContainer() {
    if (Cypress.env("isPrivateOrg")) {
      cy.get(".MuiButton-label").contains("Stop").eq(0).click();
    }
  }
  // private static hideSharePopover() {
  //   cy.get('[data-testid="dialog-close-icon"]', { timeout: 200000 })
  //     .should("be.visible")
  //     .click();
  // }
}
