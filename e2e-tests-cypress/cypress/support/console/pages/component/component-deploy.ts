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

import { Utils } from "../../utils";

interface PromoteConfigs {
  settingButtonCount: number,
  promoButtonIndex?: number,
  invokeUrlCount: number,
  invokeUrlIndex?: number
}

export class ComponentDeployPage {

  static deployToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").wait(5000).click();
    this.closePopup();
    cy.get('[data-testid="btn-stop"]', { timeout: 360000 }).should('be.visible');
  }

  static promoteToStg() {
    this.promote({ settingButtonCount: 1, invokeUrlCount: 2, invokeUrlIndex: 1 })
  }

  static promoteToProd() {
    window.localStorage.setItem("hideSocialShareModel", "true");

    if (Cypress.env("isPrivateOrg")) {
      this.promote({ settingButtonCount: 2, promoButtonIndex: 1, invokeUrlCount: 2, invokeUrlIndex: 1 })
    } else {
      this.promote({ settingButtonCount: 1, invokeUrlCount: 2, invokeUrlIndex: 1 })
    }
  }

  static deployManualTriggerToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 360000 }).should("be.enabled").click();
    this.closePopup();
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should('be.visible');
  }

  static promoteManualTriggerToStg() {
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should("be.enabled").wait(2000).eq(0).click();
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should("not.be.disabled");
  }

  static promoteManualTriggerToProd() {
    if (Cypress.env("isPrivateOrg")) {
      cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should("be.enabled").wait(2000).eq(1).click();
    } else {
      cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should("be.enabled").wait(2000).eq(0).click();
    }
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click()
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    this.closePopup();
    cy.get('[value="*/1 * * * *"]', { timeout: 360000 }).should("have.length", 1)
  }

  static promoteScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[value="*/1 * * * *"]').should("have.length", 1).wait(2000)
    cy.get('[data-cyid*="promote"]').should("be.enabled").eq(0).click()
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', { timeout: 360000 }).should("have.length", 2)
  }

  static stopScheduleTask() {
    cy.contains("Stop").eq(0).click({ multiple: true });
    cy.contains("Stop").eq(0).click({ multiple: true });
  }

  static configureAndDeploy(configValue: string) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    cy.get('[data-cyid="btn-deploy-api"]').should('be.enabled').click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
    this.closePopup();
    cy.get('[data-testid="securityHeaderInput"]', { timeout: 360000 }).should('have.length', 1)
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(".ConfigForm").then((frm) => {
      const drpDown = frm.find(".ConfigForm .MuiIconButton-label").length;
      const input = frm.find(".ConfigForm div input").length
      if (drpDown < 2) {
        cy.get(".ConfigForm .MuiIconButton-label").eq(0).click();
        if (input == 0) {
          cy.get(".ConfigForm .MuiIconButton-label").eq(1).click();
        }
      }
      cy.get(".ConfigForm div input").type(value);

    });
    cy.get('button[type="submit"]').click();

  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]', { timeout: 90000 });
  }

  static promoteWebHookToProd(configValue: string) {

    if (Cypress.env("isPrivateOrg")) {
      cy.get('[data-cyid*="promote"]').should("have.length", 1).wait(2000);
      cy.get('[data-cyid*="promote"]').click();
      cy.get(".MuiCardContent-root button").contains("Next", { timeout: 360000 }).should("be.visible").click();
      cy.get(".ConfigForm button").contains("Promote").click();
      cy.get('[data-cyid="btn-api-settings"]', { timeout: 360000 }).should('have.length', 3)
    } else {
      this.promote({ settingButtonCount: 1, invokeUrlCount: 1, invokeUrlIndex: 0 })
      cy.get(".MuiCardContent-root >.MuiBox-root>div>div>button").should('have.length', 3).contains("Next").should("be.visible").click();
      this.addConfiguration(configValue);
      cy.get('[data-cyid="btn-api-settings"]', { timeout: 360000 }).should('have.length', 2)
    }
  }

  static promoteWebHookToSTG(config: string) {
    cy.get('[data-cyid*="promote"]').should("be.enabled").click();
    cy.get(".MuiCardContent-root button").contains("Next", { timeout: 360000 }).should("be.visible").click();
    cy.get(".ConfigForm input").type(config);
    cy.get(".ConfigForm button").contains("Promote").click();

  }


  static verifyDevInvokeURL() { return Utils.getInvokeUrl(0) }

  static verifyInternalAPIdevWarning() {
    cy.get('[data-testid="warning-banner"]', { timeout: 150000 }).eq(0).should("be.visible")
    return cy.get('[data-testid="env.invoke.url.internal.endpoint.warning"]>p').eq(0).invoke('text')
  }


  static verifyInternalAPIprodWarning() {
    cy.get('[data-testid="warning-banner"]', { timeout: 150000 }).eq(1).should("be.visible")
    return cy.get('[data-testid="env.invoke.url.internal.endpoint.warning"]>p').eq(1).invoke('text')
  }

  static verifyStgeInvokeURL() { return Utils.getInvokeUrl(1) }

  static verifyProdInvokeURL() {
    if (Cypress.env("isPrivateOrg")) { return Utils.getInvokeUrl(2) }
    else { return Utils.getInvokeUrl(1) }
  }

  static stopAllDeployment() {
    cy.wait(3000);
    this.stopDevContainer();
    this.stopStgContainer();
    this.stopProdContainer();
  }

  private static stopContainer(stpButton: number, len: number) {
    cy.get("body").then((body) => {
      if (body.find('[data-testid="btn-view-logs"]').length > 0) {
        cy.get('[data-testid="btn-stop"]').should("have.length", len).eq(stpButton).click();
        cy.get('[data-cyid="deployment-status"]>h6').should("have.length", len).eq(stpButton).invoke("text").should("eq", "Suspended");
      } else {
        cy.get('[data-testid="btn-stop"]').should("have.length", len).eq(stpButton).click();
        cy.get('[data-cyid="deployment-status"]>h6').should("have.length", len).eq(stpButton).invoke("text").should("eq", "Suspended");
      }
    });
  }

  public static stopDevContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(0, 3) }
    else { this.stopContainer(0, 2) }
  }

  public static stopProdContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(0, 1) }
    else { this.stopContainer(0, 1) }
  }

  public static stopStgContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(1, 2) }
  }

  private static closePopup() {
    cy.get('[title="Initialization is completed"]')
    cy.get(".splitterH>div>div>div>div>div>div>div>button").click();
  }

  private static promote({ settingButtonCount, promoButtonIndex = 0, invokeUrlCount, invokeUrlIndex = 0 }: PromoteConfigs) {
    cy.get('[data-cyid="btn-api-settings"]', { timeout: 360000 }).should("have.length", settingButtonCount).wait(2000); // the number of `API Settings` buttons
    cy.get('[data-cyid*="promote"]', { timeout: 360000 }).should("be.enabled").wait(2000).eq(promoButtonIndex).click(); // promote button
    cy.get('[id="securityHeaderInput"]', { timeout: 360000 }).should("have.length", invokeUrlCount).eq(invokeUrlIndex).invoke("val").should("not.be.empty"); // the number of `Invoke URLs`
    cy.get('[data-cyid*="promote"]', { timeout: 360000 }).should("not.be.disabled");
  }


}
