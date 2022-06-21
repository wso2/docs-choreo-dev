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
  static promoteToStg() {
    cy.get('[data-cyid="btn-api-settings"]', { timeout: 180000 }).should(
      "have.length",
      1
    );
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).focus().should("be.visible");
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).click();
    cy.get('[id="securityHeaderInput"')
      .should("have.length", 2)
      .eq(1)
      .invoke("val")
      .should("not.be.empty");
  }

  static promoteManualTriggerToStg() {
    cy.get('[data-testid="no-invoke-url-info"]', { timeout: 180000 }).should(
      "have.length",
      1
    );
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).focus().should("be.visible");
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).click();
    cy.get('[id="securityHeaderInput"')
      .should("have.length", 2)
      .eq(1)
      .invoke("val")
      .should("not.be.empty");
  }

  static promoteToProd() {
    let isPrivateOrg = Cypress.env("isPrivateOrg");
    window.localStorage.setItem("hideSocialShareModel", "true");
    if (isPrivateOrg) {
      cy.get('[data-cyid="btn-api-settings"]', { timeout: 180000 }).should(
        "have.length",
        2
      );
      cy.wait(2000);
      cy.get('[data-cyid*="promote"]', { timeout: 120000 })
        .eq(1)
        .focus()
        .should("be.visible");
      cy.wait(2000);
      cy.get('[data-cyid*="promote"]').eq(1).click();
      cy.get('[id="securityHeaderInput"]')
        .should("have.length", 2)
        .eq(1)
        .invoke("val")
        .should("not.be.empty");
    } else {
      cy.get('[data-cyid="btn-api-settings"]', { timeout: 180000 }).should(
        "have.length",
        1
      );
      cy.wait(2000);
      cy.get('[data-cyid*="promote"]', { timeout: 180000 }).focus();
      cy.wait(2000);
      cy.get('[data-cyid*="promote"]', { timeout: 180000 }).click();
      cy.get('[id="securityHeaderInput"]')
        .should("have.length", 1)
        .eq(0)
        .invoke("val")
        .should("not.be.empty");
      cy.get('[data-cyid*="promote"]', { timeout: 120000 }).should(
        "not.be.disabled"
      );
    }
  }

  static deployToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 180000 })
      .should("be.enabled")
      .click();
    this.closePopup();
    cy.get('[data-testid="btn-stop-redeploy"]', { timeout: 180000 }).should(
      "be.visible"
    );
  }

  static deployManualTriggerToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 180000 })
      .should("be.enabled")
      .click();
    this.closePopup();
    cy.get('[data-testid="no-invoke-url-info"]', { timeout: 180000 }).should(
      "be.visible"
    );
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 180000 })
      .should("be.enabled")
      .click();
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    this.closePopup();
  }

  static promoteScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[value="*/1 * * * *"]', { timeout: 180000 }).should(
      "have.length",
      1
    );
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]', { timeout: 180000 })
      .eq(0)
      .should("be.enabled")
      .click();
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]').should("have.length", 2);
  }

  static configureAndDeploy(configValue: string) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(8000);
    cy.get('[data-cyid="btn-deploy-api"]').click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
    this.closePopup();
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(".ConfigForm").then((frm) => {
      const le = frm.find(".Mui-required").length;
      if (le < 2) {
        cy.get(".ConfigForm .MuiIconButton-label").click();
        cy.get(".MuiFormControl-fullWidth>div>input").eq(1).type(value);
      } else {
        cy.get(".MuiFormControl-fullWidth>div>input").eq(1).type(value);
      }
    });
    cy.get('button[type="submit"]').click();
  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]', { timeout: 90000 });
  }

  static promoteManualTriggerToProd() {
    cy.get('[data-testid="no-invoke-url-info"]', { timeout: 180000 }).should(
      "be.visible"
    );
    cy.get('[data-cyid*="promote"]', { timeout: 180000 })
      .focus()
      .should("be.visible");
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').eq(0).should("be.enabled").click();
  }

  static promoteWebHookToProd() {
    const isPrivateOrg = Cypress.env("isPrivateOrg");

    if (isPrivateOrg) {
      cy.get('[data-cyid*="promote"]').should("have.length", 1);
      cy.wait(2000);
      cy.get('[data-cyid*="promote"]').click();
      cy.get(".MuiCardContent-root button", { timeout: 120000 })
        .contains("Next", { timeout: 120000 })
        .should("be.visible")
        .click();
      cy.get(".ConfigForm button", { timeout: 120000 })
        .contains("Promote")
        .click();
    } else {
      this.promoteToProd();
      cy.wait(2000);
      cy.get(".MuiCardContent-root button", { timeout: 120000 })
        .contains("Next")
        .should("be.visible")
        .click();
      cy.get(".ConfigForm button", { timeout: 120000 })
        .contains("Promote")
        .click();
    }
  }

  static promoteWebHookToSTG() {
    cy.get('[data-testid="securityHeaderInput"]')
      .should("have.length", 1)
      .eq(0)
      .invoke("val")
      .should("not.be.empty");
    cy.wait(2000);
    cy.get('[data-cyid*="promote"]').should("be.enabled").click();
    cy.get(".MuiCardContent-root button", { timeout: 120000 })
      .contains("Next", { timeout: 120000 })
      .should("be.visible")
      .click();
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
  static verifyStgeInvokeURL() {
    cy.wait(5000);
    return cy
      .get('[data-cyid="text-field-invoke-url"] input')
      .eq(1)
      .invoke("attr", "value");
  }
  static verifyProdInvokeURL() {
    let isPrivateOrg = Cypress.env("isPrivateOrg");
    if (isPrivateOrg) {
      cy.get('[data-cyid="text-field-invoke-url"] input', {
        timeout: 120000,
      }).should("have.length", 3);

      return cy
        .get('[data-cyid="text-field-invoke-url"] input')
        .eq(2)
        .invoke("attr", "value");
    }

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

  static stopScheduleTask() {
    cy.contains("Stop").eq(0).click({ multiple: true });
    cy.contains("Stop").eq(0).click({ multiple: true });
  }

  public static stopDevContainer() {
    cy.get('[data-testid="btn-stop-redeploy"]').eq(0).click();
  }

  public static stopProdContainer() {
    cy.get('[data-testid="btn-stop-redeploy"]').eq(0).click();
  }

  public static stopStgContainer() {
    if (Cypress.env("isPrivateOrg")) {
      cy.get('[data-testid="btn-stop-redeploy"]').eq(0).click();
    }
  }

  private static closePopup() {
    cy.get('[title="Initialization is completed"]', { timeout: 180000 }).should(
      "be.visible"
    );
    cy.get(".splitterH>div>div>div>div>div>div>div>button", { timeout: 180000 })
      .should("be.visible")
      .click();
  }
}
