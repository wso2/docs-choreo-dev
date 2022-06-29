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
    cy.get('[data-testid="btn-stop-redeploy"]');
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
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    this.closePopup();
    cy.get('[data-cyid="btn-promote"]');
  }

  static promoteManualTriggerToStg() {
    cy.get('[data-cyid="btn-promote"]').should("be.enabled").wait(2000).eq(0).click();
    cy.get('[data-cyid="btn-promote"]').should("not.be.disabled");
  }

  static promoteManualTriggerToProd() {
    if (Cypress.env("isPrivateOrg")) {
      cy.get('[data-cyid="btn-promote"]').should("be.enabled").wait(2000).eq(1).click();
    } else {
      cy.get('[data-cyid="btn-promote"]').should("be.enabled").wait(2000).eq(0).click();
    }
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click()
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    this.closePopup();
  }

  static promoteScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[value="*/1 * * * *"]').should("have.length", 1).wait(2000)
    cy.get('[data-cyid*="promote"]').should("be.enabled").eq(0).click()
    cy.get("button:not([data-cyid])").contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]').should("have.length", 2)
  }

  static stopScheduleTask() {
    cy.contains("Stop").eq(0).click({ multiple: true });
    cy.contains("Stop").eq(0).click({ multiple: true });
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
      const drpDown = frm.find(".ConfigForm .MuiIconButton-label").length;
      const input = frm.find(".ConfigForm div input").length
      if (drpDown < 2) {
        cy.get(".ConfigForm .MuiIconButton-label").eq(0).click();
        if (input == 0) {
          cy.get(".ConfigForm .MuiIconButton-label").eq(1).click();
        }

        cy.get(".ConfigForm div input").type(value);
      } else {
        cy.get(".ConfigForm div input").type(value);
      }
    });
    cy.get('button[type="submit"]').click();
  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]', { timeout: 90000 });
  }

  static promoteWebHookToProd() {

    if (Cypress.env("isPrivateOrg")) {
      cy.get('[data-cyid*="promote"]').should("have.length", 1).wait(2000);
      cy.get('[data-cyid*="promote"]').click();
      cy.get(".MuiCardContent-root button").contains("Next", { timeout: 120000 }).should("be.visible").click();
      cy.get(".ConfigForm button").contains("Promote").click();
    } else {
      this.promote({ settingButtonCount: 1, invokeUrlCount: 1, invokeUrlIndex: 0 })
      cy.get(".MuiCardContent-root >.MuiBox-root>div>div>button").should('have.length',3).contains("Next").should("be.visible").click();
      cy.get(".ConfigForm button").contains("Promote").click();
    }
  }

  static promoteWebHookToSTG(config: string) {
    cy.get('[data-cyid*="promote"]').should("be.enabled").click();
    cy.get(".MuiCardContent-root button").contains("Next", { timeout: 120000 }).should("be.visible").click();
    cy.get(".ConfigForm input").type(config);
    cy.get(".ConfigForm button").contains("Promote").click();
  }


  static verifyDevInvokeURL() { return Utils.getInvokeUrl(0) }

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
        cy.get('[data-testid="btn-stop-redeploy"]').should("have.length", len).eq(stpButton).click();
        cy.get('[data-cyid="api-deploy-status"]').should("have.length", len)
      } else {
        cy.get('[data-testid="btn-stop-redeploy"]').should("have.length", len).eq(stpButton).click();
        cy.get('[data-cyid="proxy-deploy-card"]').should("have.length", len)
      }
    });
    cy.get('[data-cyid="deployment-status"]>p').eq(stpButton).invoke("text").should("eq", "Suspended");
  }

  public static stopDevContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(0, 3) }
    else { this.stopContainer(0, 2) }
  }

  public static stopProdContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(2, 3) }
    else { this.stopContainer(1, 2) }
  }

  public static stopStgContainer() {
    if (Cypress.env("isPrivateOrg")) { this.stopContainer(1, 3) }
  }

  private static closePopup() {
    cy.get('[title="Initialization is completed"]')
    cy.get(".splitterH>div>div>div>div>div>div>div>button").click();
  }

  private static promote({ settingButtonCount, promoButtonIndex = 0, invokeUrlCount, invokeUrlIndex = 0 }: PromoteConfigs) {
    cy.get('[data-cyid="btn-api-settings"]').should("have.length", settingButtonCount).wait(2000); // the number of `API Settings` buttons
    cy.get('[data-cyid*="promote"]').should("be.enabled").wait(2000).eq(promoButtonIndex).click(); // promote button
    cy.get('[id="securityHeaderInput"]').should("have.length", invokeUrlCount).eq(invokeUrlIndex).invoke("val").should("not.be.empty"); // the number of `Invoke URLs`
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }


}
