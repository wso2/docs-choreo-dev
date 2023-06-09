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
import {
  DEPLOYMENT_PENDING,
  DEPLOYMENT_PROGRESSING,
  DEPLOYMENT_STOPPED,
  DEPLOYMENT_SUCCESS,
} from "../../../commons/constants";
import { Utils } from "../../../commons/utils";
import { LONG_TIME, MEDIUM_TIME, SHORT_TIME } from "../../../commons/timeouts";
import { cyGet } from "../../../commons/cy";
import { APIDeployment } from "../apis/api-deployment";

interface PromoteConfigs {
  settingButtonCount: number;
  promoButtonIndex?: number;
  invokeUrlCount: number;
  invokeUrlIndex?: number;
}

export class ComponentDeployPage {
  static count: number = 6;

  private static pollElement(locator: string) {
    cy.wait(5000);
    return cy.get("body").then((bdy) => {
      if (this.count > 0 && bdy.find(locator).length == 0) {
        this.pollElement(locator);
        this.count--;
      } else {
        return cy.get(locator);
      }
    });
  }

  static deployToDev(
    isAdditionalConfigs: boolean = true,
    isManagedByAPIM: boolean = true,
    isManualTrigger: boolean = false
  ) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api"]', SHORT_TIME)
      .contains("Generating Configurations")
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-api"]', LONG_TIME)
      .should("be.enabled")
      .click();

    if (isAdditionalConfigs) {
      if (isManagedByAPIM) {
        Utils.interceptConfig();
      }
      this.pollElement('[data-cyid="btn-next"]').click();
    }

    APIDeployment.RetryDevDeployment();
    if (isManualTrigger) {
      cy.get('[data-cyid="btn-promote"]', LONG_TIME).should("be.enabled");
      return;
    }
    cy.get('[data-testid="btn-stop"]', MEDIUM_TIME).should("be.visible");
    GraphQL.getComponentDeploymentStatus();
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cy.get('[data-testid="btn-stop"]', MEDIUM_TIME).should("be.visible");
    cy.get('[data-cyid="deployment-status"]', LONG_TIME).contains(
      DEPLOYMENT_SUCCESS,
      LONG_TIME
    );
    cy.get('[data-cyid="link-test"]').should("be.visible");
  }

  static promoteToProd(
    isAdditionalConfigs: boolean = true,
    isManagedByAPIM: boolean = true,
    numberOfNextPrompts: number = 2
  ) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    APIDeployment.RetryPromotionToProd();
    cyGet('[data-cyid="btn-promote"]').realClick();
    if (isAdditionalConfigs) {
      cyGet("body").then((bdy) => {
        if (bdy.find('[data-testid="deployment-history-btn"]').length == 2) {
          cyGet('[data-cyid="btn-next"]').realClick();
        } else {
          for (var i = 0; i < numberOfNextPrompts; i++) {
            cyGet('[data-cyid="btn-next"]').realClick();
          }
        }
      });
    }

    if (isManagedByAPIM) {
      Utils.interceptConfig();
    }

    APIDeployment.RetryPromotionToProd();
    cyGet('[data-testid="btn-stop"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .should("be.visible");
    cyGet('[data-cyid="deployment-status"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains("Active", LONG_TIME);
    cyGet('[data-cyid="btn-promote"]', LONG_TIME).should("not.be.disabled");
  }

  static promoteManualTriggerToProd() {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote"]', LONG_TIME).should("be.enabled").click();
    APIDeployment.RetryPromotionToProd();
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    cy.get('[data-cyid="btn-next"]').contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', LONG_TIME).eq(0).should("be.visible");
    APIDeployment.RetryDevDeployment();
  }

  static promoteScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    APIDeployment.RetryPromotionToProd();
    cy.get('[value="*/1 * * * *"]', LONG_TIME).eq(0).should("be.visible");
    Utils.getRenderedElement('[data-cyid*="promote"]')
      .should("be.enabled")
      .eq(0)
      .click();
    cy.get('[data-cyid="btn-next"]').contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', LONG_TIME).eq(1).should("be.visible");
    APIDeployment.RetryPromotionToProd();
  }

  static configureAndDeploy(configValue: string) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
    cy.get('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cy.get('[data-testid="btn-stop"]').should("be.visible");
    cy.get('[data-cyid="deployment-status"]', LONG_TIME).contains(
      DEPLOYMENT_SUCCESS,
      LONG_TIME
    );
  }

  static addConfiguration(value: string) {
    cy.get(".ConfigForm").should("be.visible");
    cy.get(".ConfigForm div input").type(value);
    cy.get('.ConfigForm button[type="submit"]').click();
  }

  static promoteWebHookToProd(
    configValue: string,
    isNewComponent: boolean = true
  ) {
    APIDeployment.RetryPromotionToProd();
    this.promote({
      settingButtonCount: 2,
      invokeUrlCount: 0,
      invokeUrlIndex: 0,
    });
    if (isNewComponent) {
      cy.get('[data-cyid="btn-next"]').click();
      this.addConfiguration(configValue);
    } else {
      cy.get(".ConfigForm button")
        .contains("Promote")
        .should("have.length", 1)
        .click();
    }
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-testid="btn-stop"]', LONG_TIME).should("have.length", 2);
    cy.get('[data-cyid="deployment-status"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_SUCCESS, LONG_TIME);
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
          .should("eq", DEPLOYMENT_STOPPED);
      } else {
        cy.get('[data-testid="btn-stop"]')
          .should("have.length", len)
          .eq(stpButton)
          .click();
        cy.get('[data-cyid="deployment-status"]>h6')
          .should("have.length", len)
          .eq(stpButton)
          .invoke("text")
          .should("eq", DEPLOYMENT_STOPPED);
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

  private static promote({}: PromoteConfigs) {
    cy.get('[data-cyid="btn-promote"]', LONG_TIME).should("not.be.disabled");
    cy.wait(3000);
    cy.get('[data-cyid="btn-promote"]', LONG_TIME).click();
  }
  static verifyDeploymentStatus() {
    cy.get('[data-cyid="deployment-status"]').eq(0).contains("Active");
  }

  static configureAndDeployProxyApiToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-proxy"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled");
    Utils.getRenderedElement('[data-cyid="btn-next"]').click();
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="deployment-status"]')
      .contains(DEPLOYMENT_SUCCESS)
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static promoteProxyApiToProd() {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next"]').should("be.enabled");
    cy.get('[data-cyid="btn-next"]').should("exist").click();
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="proxy-env-card-header"]>div>span')
      .contains("Production")
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]')
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_SUCCESS)
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static addNewVersion(branch: string = "feature", version: string = "1.1") {
    cy.get('[data-cyid="version-picker"]').click();
    cy.get('[data-cyid="btn-create-version"]').should("be.visible").click();
    cy.get('[role="dialog"]').within(() => {
      cy.get('[data-testid*="feature"]').click();
    });
    cy.get(`[data-value="${branch}"]`).click();
    cy.get('[role="dialog"]').within(() => {
      cy.get(`[name="Version name"]`).type(version);
      cy.get('[data-testid="create-version-create"]').click();
      cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
    });
  }

  static deployService(endpointName: string, changeVisibility?: boolean) {
    cy.get('[data-cyid="btn-deploy-api"]', SHORT_TIME)
      .contains("Generating Configurations")
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-api"]').should("be.enabled").click();
    cy.get('[data-cyid="btn-deploy-api"]', LONG_TIME)
      .should("be.enabled")
      .click();
    cy.get(`[data-testid="${endpointName}-endpoint"]`).should("be.visible");
    if (changeVisibility) {
      cy.get(`[data-testid="${endpointName}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get('[data-testid="Public-visibility-option"]')
        .should("be.visible")
        .click();
      cy.get('[data-cyid="endpoint-submit-btn"]').click();
    }
    cyGet('[data-cyid="btn-next"]').click();
    APIDeployment.RetryDevDeployment();
    cyGet('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    GraphQL.getComponentDeploymentStatus();
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cyGet('[data-testid="btn-stop"]', MEDIUM_TIME).should("be.visible");
    cyGet('[data-cyid="deployment-status"]', LONG_TIME)
      .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
      .should("be.visible");
    cyGet('[data-testid="Endpoints-env-artifact"]').should("be.visible");
    GraphQL.getServiceEndpointStatus();
    cyGet('[data-cyid="deployment-status"]', SHORT_TIME)
      .contains(DEPLOYMENT_PENDING, SHORT_TIME)
      .should("not.exist");
    cyGet('[data-cyid="deployment-status"]', SHORT_TIME)
      .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
      .should("not.exist");
    cyGet('[data-testid="Endpoints-status"]', LONG_TIME).contains(
      DEPLOYMENT_SUCCESS,
      LONG_TIME
    );
  }

  static promoteService(endpointName: string, changeVisibility?: boolean) {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote"]', LONG_TIME).should("be.enabled").click();
    cy.get(`[data-testid="${endpointName}-endpoint"]`).should("be.visible");
    if (changeVisibility) {
      cy.get(`[data-testid="${endpointName}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get('[data-testid="Public-visibility-option"]')
        .should("be.visible")
        .click();
      cy.get('[data-cyid="endpoint-submit-btn"]').click();
    }
    cy.get('[data-cyid="btn-next"]').click();
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-testid="btn-stop"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]', SHORT_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains("Active", SHORT_TIME);
    cy.get('[data-cyid="btn-promote"]', LONG_TIME).should("not.be.disabled");
    cy.get('[data-testid="Endpoints-env-artifact"]')
      .should("have.length", 2)
      .eq(1)
      .should("be.visible");
    cy.get('[data-testid="Endpoints-status"]', SHORT_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_PENDING, SHORT_TIME)
      .should("not.exist");
    cy.get('[data-testid="Endpoints-status"]', SHORT_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
      .should("not.exist");
    cy.get('[data-testid="Endpoints-status"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains("Active", SHORT_TIME)
      .should("exist");
  }
}
