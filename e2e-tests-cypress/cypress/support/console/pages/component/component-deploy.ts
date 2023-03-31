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

import { GraphQL } from "../../apis/graphql";
import { Utils } from "../../utils";

interface PromoteConfigs {
  settingButtonCount: number;
  promoButtonIndex?: number;
  invokeUrlCount: number;
  invokeUrlIndex?: number;
}

export class ComponentDeployPage {
  static deployToDev(isExternalAPI: boolean = true) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 300000 }).should("be.enabled").click();
    if (isExternalAPI) {
      Utils.interceptConfig()
      cy.get('[data-cyid="btn-next"]', { timeout: 600000 }).click();
    }
    cy.get('[data-testid="btn-stop"]', { timeout: 600000 }).should("be.visible");
    GraphQL.getComponentDeploymentStatus()
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cy.get('[data-testid="btn-stop"]', { timeout: 600000 }).should("be.visible");
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 }).contains("Active", { timeout: 360000 });
  }


  static promoteToProd(isExternalAPI: boolean = true) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000);
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .click();

      cy.get('body').then(bdy=>{
        if (bdy.find('[data-testid="deployment-history-btn"]').length==2){
          cy.get('[data-cyid="btn-next"]').realClick();
        }
      })


    if (isExternalAPI) {
      Utils.interceptConfig()
      cy.get('[data-cyid="btn-next"]').realClick();
      cy.get('[data-cyid="btn-next"]').realClick();
    }


    cy.get('[data-testid="btn-stop"]', { timeout: 360000 })
      .should("have.length", 2)
      .eq(1)
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 })
      .should("have.length", 2)
      .eq(1)
      .contains("Active", { timeout: 360000 });
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should(
      "not.be.disabled"
    );
  }

  static deployManualTriggerToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 360000 })
      .should("be.enabled")
      .click();
  }

  static promoteManualTriggerToProd() {
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")

      .click();
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    cy.get('[data-cyid="btn-next"]').contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', { timeout: 360000 }).should(
      "have.length",
      1
    );
  }

  static promoteScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[value="*/1 * * * *"]').should("have.length", 1).wait(2000);
    cy.get('[data-cyid*="promote"]').should("be.enabled").eq(0).click();
    cy.get('[data-cyid="btn-next"]').contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', { timeout: 360000 }).should(
      "have.length",
      2
    );
  }



  static configureAndDeploy(configValue: string) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
    cy.get('[data-testid="btn-stop"]', { timeout: 360000 }).should(
      "be.visible"
    );
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cy.get('[data-testid="btn-stop"]').should("be.visible");
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 }).contains("Active", { timeout: 360000 });
  }

  static addConfiguration(value: string) {
    cy.get(".ConfigForm").should("be.visible");
    cy.get(".ConfigForm div input").type(value);
    cy.get('.ConfigForm button[type="submit"]').click();
  }



  static promoteWebHookToProd(configValue: string) {
    this.promote({ settingButtonCount: 2, invokeUrlCount: 0, invokeUrlIndex: 0 });
    cy.get('button[type="submit"]').should("be.enabled").click();
    this.addConfiguration(configValue);
    cy.get('[data-testid="btn-stop"]', { timeout: 360000 }).should(
      "have.length",
      2
    );
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 })
      .should("have.length", 2)
      .eq(1)
      .contains("Active", { timeout: 360000 });
    cy.get('[data-cyid*="test-nav-btn"]').should("be.visible");
  }

  static stopAllDeployment() {
    this.stopDevContainer();
    this.stopStgContainer();
    this.stopProdContainer();
  }

  private static stopContainer(stpButton: number, len: number) {
    cy.get("body").then((body) => {
      if (body.find('[data-testid="btn-view-logs"]').length > 0) {
        cy.get('[data-testid="btn-stop"]')
          .should("have.length", len)
          .eq(stpButton)
          .click();
        cy.get('[data-cyid="deployment-status"]>h6')
          .should("have.length", len)
          .eq(stpButton)
          .invoke("text")
          .should("eq", "Suspended");
      } else {
        cy.get('[data-testid="btn-stop"]')
          .should("have.length", len)
          .eq(stpButton)
          .click();
        cy.get('[data-cyid="deployment-status"]>h6')
          .should("have.length", len)
          .eq(stpButton)
          .invoke("text")
          .should("eq", "Suspended");
      }
    });
  }

  public static stopDevContainer() {
    if (Cypress.env("isPrivateOrg")) {
      this.stopContainer(0, 3);
    } else {
      this.stopContainer(0, 2);
    }
  }

  public static stopProdContainer() {
    if (Cypress.env("isPrivateOrg")) {
      this.stopContainer(0, 1);
    } else {
      this.stopContainer(0, 1);
    }
  }

  public static stopStgContainer() {
    if (Cypress.env("isPrivateOrg")) {
      this.stopContainer(1, 2);
    }
  }

  private static promote({ }: PromoteConfigs) {
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000)
      .click(); // promote button
    cy.wait(6000);
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should(
      "not.be.disabled"
    );
  }
  static verifyDeploymentStatus() {
    cy.get('[data-cyid="deployment-status"]').eq(0).contains("Active");
  }

  static configureAndDeployProxyApiToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    cy.get('[data-cyid="btn-deploy-proxy"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled");
    cy.get('[data-cyid="btn-next"]').should("exist").click();
    cy.get('[data-cyid="deployment-status"]')
      .contains("Active")
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static promoteProxyApiToProd() {
    cy.get('[data-cyid="btn-promote"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled");
    cy.get('[data-cyid="btn-next"]').should("exist").click();
    cy.get('[data-cyid="proxy-env-card-header"]>div>span')
      .contains("Production")
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]')
      .should("have.length", 2)
      .eq(1)
      .contains("Active")
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static addNewVersion(branch: string = "feature", version: string = "1.1") {
    cy.get('[data-cyid="version-picker"]').click()
    cy.get('[data-cyid="btn-create-version"]').should('be.visible').click()
    cy.get('[role="dialog"]').within(() => {
      cy.get('[data-testid*="feature"]').click()
    })
    cy.get(`[data-value="${branch}"]`).click()
    cy.get('[role="dialog"]').within(() => {
      cy.get(`[name="Version name"]`).type(version)
      cy.get('[data-testid="create-version-create"]').click()
      cy.get('[data-testid="dialog-close-icon"]').should('not.exist')
    })
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
  }
}
