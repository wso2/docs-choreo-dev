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
  settingButtonCount: number;
  promoButtonIndex?: number;
  invokeUrlCount: number;
  invokeUrlIndex?: number;
}

export class ComponentDeployPage {
  static deployToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.intercept(
      `${Cypress.env(
        "newAppSvcURL"
      )}/alert-configuration-service/1.0.0/org/*/alert-config`
    ).as("config");
    cy.wait("@config", { timeout: 18000 });
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").focus().click();
    cy.get('[data-testid="btn-stop"]', { timeout: 360000 }).should(
      "be.visible"
    );
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 }).contains(
      "Active",
      { timeout: 360000 }
    );
  }

  static promoteToStg() {
    this.promote({
      settingButtonCount: 1,
      invokeUrlCount: 2,
      invokeUrlIndex: 1,
    });
  }

  static promoteToProd() {
    window.localStorage.setItem("hideSocialShareModel", "true");

    if (Cypress.env("isPrivateOrg")) {
      this.promote({
        settingButtonCount: 2,
        promoButtonIndex: 1,
        invokeUrlCount: 2,
        invokeUrlIndex: 1,
      });
    } else {
      this.promote({
        settingButtonCount: 1,
        invokeUrlCount: 2,
        invokeUrlIndex: 1,
      });
    }
  }

  static promoteToProdApiPerspectiveView() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000);
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .click();
    cy.wait(6000);
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should(
      "not.be.disabled"
    );
  }

  static deployManualTriggerToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', { timeout: 360000 })
      .should("be.enabled")
      .click();
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should(
      "be.visible"
    );
  }

  static promoteManualTriggerToStg() {
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000)
      .eq(0)
      .click();
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 }).should(
      "not.be.disabled"
    );
  }

  static promoteManualTriggerToProd() {
    cy.get('[data-cyid="btn-promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000)
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

  static stopScheduleTask() {
    cy.contains("Stop").eq(0).click({ multiple: true });
    cy.contains("Stop").eq(0).click({ multiple: true });
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
    cy.get('[data-cyid="deployment-status"]', { timeout: 360000 }).contains(
      "Active",
      { timeout: 360000 }
    );
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(".ConfigForm").then((frm) => {
      const drpDown = frm.find(".ConfigForm .MuiIconButton-label").length;
      const input = frm.find(".ConfigForm div input").length;
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
    this.promote({
      settingButtonCount: 2,
      invokeUrlCount: 0,
      invokeUrlIndex: 0,
    });
    this.addConfiguration(configValue);
    cy.get('[data-cyid="deployment-status"]').should("have.length", 2);
    cy.get('[data-cyid*="test-nav-btn"]').should("be.visible");
  }

  static promoteWebHookToSTG(config: string) {
    cy.get('[data-cyid*="promote"]').should("be.enabled").click();
    cy.get(".MuiCardContent-root button")
      .contains("Next", { timeout: 360000 })
      .should("be.visible")
      .click();
    cy.get(".ConfigForm input").type(config);
    cy.get(".ConfigForm button").contains("Promote").click();
  }

  static verifyInternalAPIdevWarning() {
    cy.get('[data-testid="warning-banner"]', { timeout: 150000 })
      .eq(0)
      .should("be.visible");
    return cy
      .get('[data-testid="env.invoke.url.internal.endpoint.warning"]>p')
      .eq(0)
      .invoke("text");
  }

  static verifyInternalAPIprodWarning() {
    cy.get('[data-testid="warning-banner"]', { timeout: 150000 })
      .eq(1)
      .should("be.visible");
    return cy
      .get('[data-testid="env.invoke.url.internal.endpoint.warning"]>p')
      .eq(1)
      .invoke("text");
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

  private static promote({
    settingButtonCount,
    promoButtonIndex = 0,
    invokeUrlCount,
    invokeUrlIndex = 0,
  }: PromoteConfigs) {
    cy.get('[data-cyid*="promote"]', { timeout: 360000 })
      .should("be.enabled")
      .wait(2000)
      .eq(promoButtonIndex)
      .click(); // promote button
    cy.wait(6000);
    cy.get('[data-cyid*="promote"]', { timeout: 360000 }).should(
      "not.be.disabled"
    );
  }

  static configureAndDeploySalesforceToGsheet(
    sfUsername: string,
    sfPassword: string,
    salesforceOAuthConfig: string,
    sfclientId: string,
    sfclientSecret: string,
    sfrefreshToken: string,
    sfrefreshUrl: string,
    sfsalesforceBaseUrl: string,
    gsClientId: string,
    gsclientSecret: string,
    gsrefreshToken: string,
    gsrefreshUrl: string,
    gsspreadsheetId: string,
    gsworksheetName: string
  ) {
    cy.wait(8000);
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]').click();
    cy.contains("Deploy").should("be.visible").click();
    const configField = "[type='text']";
    cy.get("[data-cyid='username']").type(sfUsername);
    cy.get("[data-cyid='password']").type(sfPassword);
    cy.get("[data-cyid='clientId']").eq(0).type(salesforceOAuthConfig);
    cy.get("[data-cyid='clientSecret']").eq(0).type(sfclientId);
    cy.get("[data-cyid='refreshToken']").eq(0).type(sfclientSecret);
    cy.get("[data-cyid='refreshUrl']").eq(0).type(sfrefreshToken);
    cy.get("[data-cyid='worksheetName']")
      .scrollIntoView()
      .type(gsworksheetName);
    cy.get("[data-cyid='salesforceBaseUrl']").type(sfrefreshUrl);
    cy.get("[data-cyid='salesforceBaseUrl']").type(sfsalesforceBaseUrl);
    cy.get("[data-cyid='clientId']").eq(1).type(gsClientId);
    cy.get("[data-cyid='clientSecret']").eq(1).type(gsclientSecret);
    cy.get("[data-cyid='refreshToken']").eq(1).type(gsrefreshToken);
    cy.get("[data-cyid='refreshUrl']").eq(1).type(gsrefreshUrl);
    cy.get("[data-cyid='spreadsheetId']").type(gsspreadsheetId);
    cy.get('button[type="submit"]').scrollIntoView().click();
  }

  static promoteConfigDeployment() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-promote"]', { timeout: 250000 })
      .scrollIntoView()
      .should("be.visible")
      .click({ force: true });
    cy.wait(5000);
    cy.get('[data-cyid="btn-promote"]').click({ force: true });
    cy.wait(5000);
    cy.get('input[name="promote-deploy"]').eq(1).click();
    cy.get('[type="button"]').contains("Next").click();
    cy.wait(10000);
    cy.get("[data-cyid='username']").click();
    cy.get('span[class="MuiButton-label"]').contains("Back").scrollIntoView();
    cy.get('button[type="submit"]').click();
  }

  static verifyDeploymentStatus() {
    cy.get('[data-cyid="deployment-status"]').eq(0).contains("Active");
  }

  static configureAndDeployProxyApiToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    cy.get('[data-cyid="btn-deploy-proxy"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled").click();
    cy.get('[data-cyid="deployment-status"]')
      .contains("Active")
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static promoteProxyApiToProd() {
    cy.get('[data-cyid="btn-promote"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled").click();
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
}
