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
  BUILD_FAILED,
  CONFIG_CONTENT,
  CONFIG_FILE,
  CONFIG_KEY,
  CONFIG_VALUE,
  DEPLOYMENT_ERROR,
  DEPLOYMENT_PENDING,
  DEPLOYMENT_PROGRESSING,
  DEPLOYMENT_STOPPED,
  DEPLOYMENT_SUCCESS,
  MOUNT_PATH,
  SECRET_KEY,
  SECRET_VALUE,
} from "../../../commons/constants";
import { Utils } from "../../../commons/utils";
import {
  LONG_TIME,
  MEDIUM_TIME,
  SHORT_TIME,
  VERY_LONG_TIME,
  VERY_SHORT_TIME,
} from "../../../commons/timeouts";
import { cyGet } from "../../../commons/cy";
import { APIDeployment } from "../apis/api-deployment";
import { Enums } from "../../../commons/enums";

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

  private static configWebappComponent() {
    cy.get('[class="view-lines monaco-mouse-cursor-text"]').type('{backspace}').type(CONFIG_CONTENT);
    cy.get('[data-testid="btn-next"]').click();
  }

  private static configWebAppComponentPromote() {
    cy.get(
      '[data-cyid="promote-selector-default-configs"]'
    ).click();
    cy.get('[data-cyid="btn-next-button"]').click();
    this.configWebappComponent();
  }

  static reDeployToDev(
    isAdditionalConfigs: boolean = true,
    isManagedByAPIM: boolean = true,
    isManualTrigger: boolean = false
  ) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Generating Configurations", LONG_TIME)
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .should("be.enabled")
      .click();

    if (isAdditionalConfigs) {
      if (isManagedByAPIM) {
        Utils.interceptConfig();
      }
      this.pollElement('[data-cyid="btn-next-button"]').click();
    }

    APIDeployment.RetryDevDeployment();
    if (isManualTrigger) {
      cy.get('[data-cyid="btn-promote-button"]', LONG_TIME).should(
        "be.enabled"
      );
      return;
    }

    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.get('[data-cyid="btn-status-action"]', LONG_TIME).eq(0).scrollIntoView();
    cy.get('[data-testid="btn-stop"]', LONG_TIME)
      .should("be.visible")
      .wait(600)
      .get('[data-testid="btn-stop"]', MEDIUM_TIME)
      .should("be.visible");
    cy.contains("div", "Development")
      .parents("div")
      .eq(3)
      .within(() => {
        cy.get('[data-cyid="deployment-status"]', VERY_LONG_TIME).contains(
          DEPLOYMENT_SUCCESS,
          VERY_LONG_TIME
        );
      });
    cy.get('[data-cyid="link-test"]').should("be.visible");
  }

  static deployToDev(
    projectName: string,
    componentName: string,
    isAdditionalConfigs: boolean = true,
    isManagedByAPIM: boolean = true,
    isManualTrigger: boolean = false,
    isWebApp: boolean = false
  ) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Generating Configurations", LONG_TIME)
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .should("be.enabled")
      .click();

    if (isAdditionalConfigs) {
      if (isManagedByAPIM) {
        Utils.interceptConfig();
      }
      if (!isWebApp) {
        this.pollElement('[data-cyid="btn-next-button"]').click();
      } else {
        this.configWebappComponent()
      }
    }

    APIDeployment.RetryDevDeployment();
    if (isManualTrigger) {
      cy.get('[data-cyid="btn-promote-button"]', LONG_TIME).should(
        "be.enabled"
      );
      return;
    }
    cy.get('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    GraphQL._getComponentDeploymentStatus(projectName, componentName);
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
    numberOfNextPrompts: number = 2,
    isWebApp: boolean = false
  ) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    APIDeployment.RetryPromotionToProd();
    cyGet('[data-cyid="btn-promote-button"]').realClick();
    if (isAdditionalConfigs) {
      cyGet("body").then((bdy) => {
        if (bdy.find('[data-testid="deployment-history-btn"]').length == 2) {
          cyGet('[data-cyid="btn-next-button"]').realClick();
        } else {
          for (var i = 0; i < numberOfNextPrompts; i++) {
            cyGet('[data-cyid="btn-next-button"]').realClick();
          }
        }
      });
    }

    if (isManagedByAPIM) {
      Utils.interceptConfig();
    }

    if (isWebApp) {
      this.configWebAppComponentPromote();
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
    cyGet('[data-cyid="btn-promote-button"]', LONG_TIME).should(
      "not.be.disabled"
    );
  }

  static promoteManualTriggerToProd() {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote-button"]', LONG_TIME)
      .should("be.enabled")
      .click();
    APIDeployment.RetryPromotionToProd();
  }

  static deployScheduleTask() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .should("be.enabled")
      .click();
    cy.get('[data-cyid="btn-next-button"]').contains("Deploy").click();
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
    cy.get('[data-cyid="btn-next-button"]').contains("Deploy").click();
    cy.get('[value="*/1 * * * *"]', LONG_TIME).eq(1).should("be.visible");
    APIDeployment.RetryPromotionToProd();
  }

  static configureAndDeploy(configValue: string) {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Generating Configurations", LONG_TIME)
      .should("not.exist");
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Loading Configurations", LONG_TIME)
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-api-button"]', LONG_TIME).should(
      "be.enabled"
    );
    cy.contains("Configure & Deploy", LONG_TIME).should("be.visible").click();
    this.addConfiguration(configValue);
    APIDeployment.RetryDevDeployment();
    cy.get('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(VERY_SHORT_TIME.timeout);
    cy.get('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="deployment-status"]', LONG_TIME).contains(
      DEPLOYMENT_SUCCESS,
      LONG_TIME
    );
  }

  static addConfiguration(value: string) {
    cy.get(".ConfigForm", MEDIUM_TIME).should("be.visible");
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
      cy.get('[data-cyid="btn-next-button"]').click();
      this.addConfiguration(configValue);
    } else {
      cy.get('[data-cyid="btn-submit-configform"]')
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

  public static stopSingleDevContainer() {
    this.stopContainer(0, 1);
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
    cy.get('[data-cyid="btn-promote-button"]', LONG_TIME).should(
      "not.be.disabled"
    );
    cy.wait(3000);
    cy.get('[data-cyid="btn-promote-button"]', LONG_TIME).click();
  }
  static verifyDeploymentStatus() {
    cy.get('[data-cyid="deployment-status"]').eq(0).contains("Active");
  }

  static configureAndDeployProxyApiToDev() {
    window.localStorage.setItem("hideSocialShareModel", "true");
    cy.wait(4000);
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-proxy-button"]')
      .should("be.enabled")
      .click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next-button"]').should("be.enabled");
    Utils.getRenderedElement('[data-cyid="btn-next-button"]').click();
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="deployment-status"]')
      .contains(DEPLOYMENT_SUCCESS)
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static promoteProxyApiToProd() {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote-button"]').should("be.enabled").click();
    cy.contains("Configure & Deploy").should("be.visible");
    cy.get('[data-cyid="btn-next-button"]').should("be.enabled");
    cy.get('[data-cyid="btn-next-button"]').should("exist").click();
    APIDeployment.RetryPromotionToProd();
    cy.get('[id="circular-loader"]').should("not.exist");
    cy.get('[data-testid="config-loader"]').should("not.exist");

    cy.get('[data-cyid="env-baseProduction-env-card"]')
      .contains("Production")
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]')
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static addNewVersion(branch: string = "feature", version: string = "1.1") {
    cy.get('[data-cyid="version-picker"]').click();
    cy.get('[data-cyid="btn-create-version-button"]')
      .should("be.visible")
      .click();
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

  private static configByocComponent() {
    cy.get('[data-testid="config-name-input"]').type(CONFIG_KEY);
    cy.get('[data-testid="config-value-input"]').type(CONFIG_VALUE);
    cy.get(
      '[data-cyid="editable-key-value-input-primary-button-button"]'
    ).click();
    cy.get('[data-cyid="configurations-config-accordion-summary"]').should(
      "be.visible"
    );
    cy.get('[data-testid="config-name-input"]').type(SECRET_KEY);
    cy.get('[data-testid="config-value-input"]').type(SECRET_VALUE);
    cy.get('[data-testid="config-is-secret-checkbox"]').click();
    cy.get(
      '[data-cyid="editable-key-value-input-primary-button-button"]'
    ).click();
    cy.get('[data-cyid="secrets-config-accordion-summary"]').should(
      "be.visible"
    );
    cy.get('[data-testid="btn-next"]').click();
    cy.get('[data-cyid="mount-path"]').type(MOUNT_PATH);
    cy.get('[class="view-lines monaco-mouse-cursor-text"]').type(CONFIG_FILE);
    cy.get('[data-testid="btn-next"]').click();
  }

  private static configByocComponentPromote() {
    cy.get(
      '[data-cyid="promote-selector-default-configs"]'
    ).click();
    cy.get('[data-cyid="btn-next-button"]').click();
    this.configByocComponent();
  }
  static deployService(
    projectName: string,
    componentName: string,
    endpointName: string,
    changeVisibility?: boolean,
    configSetupStepAvailable = false,
    configEnvVars = false
  ) {
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Generating Configurations", LONG_TIME)
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-api-button"]').should("be.enabled").click();
    cyGet('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .should("be.enabled")
      .click();
    if (configSetupStepAvailable) {
      cy.get('[data-cyid="btn-next-button"]').contains("Next").click();
    }
    if (configEnvVars) {
      this.configByocComponent();
    }
    cy.get(`[data-cyid="${endpointName}-endpoint-accordion"]`).should(
      "be.visible"
    );
    if (changeVisibility) {
      cy.get(`[data-testid="${endpointName}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get('[data-testid="Public-visibility-option"]')
        .should("be.visible")
        .click();
      cy.get('[data-cyid="endpoint-submit-btn-button"]').click();
    }
    cyGet('[data-cyid="btn-next-button"]').click();
    APIDeployment.RetryDevDeployment();
    cyGet('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    GraphQL._getComponentDeploymentStatus(projectName, componentName);
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.wait(600);
    cyGet('[data-testid="btn-stop"]', MEDIUM_TIME).should("be.visible");
    cy.contains("div", "Development")
      .parents("div")
      .eq(3)
      .within(() => {
        cyGet('[data-cyid="deployment-status"]', LONG_TIME)
          .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
          .should("be.visible");
      });
    cyGet('[data-testid="Endpoints-env-artifact"]').should("be.visible");
    this.verifyEndpointIsDeployed();
    cyGet('[data-cyid="deployment-status"]', SHORT_TIME)
      .contains(DEPLOYMENT_PENDING, SHORT_TIME)
      .should("not.exist");
    cyGet('[data-cyid="deployment-status"]', SHORT_TIME)
      .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
      .should("not.exist");
  }

  private static verifyEndpointIsDeployed() {
    for (var i = 0; i < 5; i++) {
      let isEndpointLoaded = false;
      cy.get("body", { log: false }).then((body) => {
        if (body.find('[data-cyid="Endpoints-status-chip"]').length > 0) {
          cy.get('[data-cyid="Endpoints-status-chip"]', { log: false }).then(
            ($statusElement) => {
              let statusText = $statusElement.children().eq(0).text();

              const waitTime = 5000 * (i + 1);
              if (
                statusText.includes(DEPLOYMENT_PENDING) ||
                statusText.includes(DEPLOYMENT_PROGRESSING)
              ) {
                cy.get('[data-cyid="commit-history-detail-box"]')
                  .eq(0)
                  .contains(BUILD_FAILED)
                  .should("not.exist");
                cy.log(
                  `Endpoint is ${statusText}, check back in ${
                    waitTime / 1000
                  } seconds`
                );
                cy.wait(waitTime, { log: false });
              } else {
                isEndpointLoaded = true;
              }
            }
          );
        }
      });

      if (isEndpointLoaded) {
        break;
      }
    }

    cyGet('[data-cyid="Endpoints-status-chip"]').contains(DEPLOYMENT_SUCCESS);
  }

  static reDeployService(
    projectName: string,
    componentName: string,
    endpointName: string,
    changeVisibility?: boolean,
    configSetupStepAvailable = false
  ) {
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .contains("Generating Configurations", LONG_TIME)
      .should("not.exist");
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-deploy-api-button"]').should("be.enabled").click();
    cy.get('[data-cyid="btn-deploy-api-button"]', LONG_TIME)
      .should("be.enabled")
      .click();
    if (configSetupStepAvailable) {
      cy.get('[data-cyid="btn-next-button"]').contains("Next").click();
    }
    cy.get(`[data-cyid="${endpointName}-endpoint-accordion"]`).should(
      "be.visible"
    );
    if (changeVisibility) {
      cy.get(`[data-testid="${endpointName}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get('[data-testid="Public-visibility-option"]')
        .should("be.visible")
        .click();
      cy.get('[data-cyid="endpoint-submit-btn-button"]').click();
    }
    cyGet('[data-cyid="btn-next-button"]').click();
    APIDeployment.RetryDevDeployment();
    cy.get('[data-cyid="btn-status-action"]', LONG_TIME).eq(0).scrollIntoView();
    // UI re-rendering takes place, so recheck if the Stop button has been loaded after a short wait
    // to ensure rendering completes before checking the deployment status
    cy.get('[data-testid="btn-stop"]', LONG_TIME)
      .should("be.visible")
      .wait(600)
      .get('[data-testid="btn-stop"]', MEDIUM_TIME)
      .should("be.visible");
    cyGet('[data-cyid="deployment-status"]', VERY_LONG_TIME)
      .contains(DEPLOYMENT_SUCCESS, VERY_LONG_TIME)
      .should("be.visible");
    cyGet('[data-testid="Endpoints-env-artifact"]').should("be.visible");
    this.verifyEndpointIsDeployed();
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

  static promoteService(
    endpointName: string,
    changeVisibility?: boolean,
    count = 0,
    configSetupStepAvailable = false,
    configEnvVars = false
  ) {
    APIDeployment.RetryPromotionToProd();
    cy.get('[data-cyid="btn-promote-button"]', LONG_TIME)
      .should("be.enabled")
      .click();
    if (configSetupStepAvailable) {
      cy.get('[data-cyid="btn-next-button"]')
        .should("be.visible")
        .click()
        .wait(2000);
      Utils.clickOnOptionalElement('[data-cyid="btn-next-button"]', 2000);
    }

    if (configEnvVars) {
      this.configByocComponentPromote();
    }

    cy.get(
      `[data-cyid="${endpointName}-endpoint-accordion"]`,
      SHORT_TIME
    ).should("be.visible");
    if (changeVisibility) {
      cy.get(`[data-testid="${endpointName}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get('[data-testid="Public-visibility-option"]')
        .should("be.visible")
        .click();
      cy.get('[data-cyid="endpoint-submit-btn-button"]').click();
    }

    cy.get('[data-cyid="btn-next-button"]').click();
    APIDeployment.RetryPromotionToProd();
    if (count > 0) {
      cy.get('[data-testid="Endpoints-status"]', LONG_TIME)
        .should("have.length", 2)
        .eq(1)
        .then(($statusElement) => {
          if ($statusElement.text().includes(DEPLOYMENT_ERROR)) {
            cy.wait(SHORT_TIME.timeout);
          }
        });
    }

    cy.get('[data-testid="btn-stop"]', LONG_TIME)
      .should("have.length", 2)
      .eq(1)
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]', SHORT_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_SUCCESS, VERY_LONG_TIME);
    cy.get('[data-cyid="btn-promote-button"]', LONG_TIME).should(
      "not.be.disabled"
    );
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
      .then(($statusElement) => {
        if ($statusElement.text().includes(DEPLOYMENT_ERROR)) {
          count++;
          if (count > 3) {
            return;
          }
          this.promoteService(endpointName, false, count, true, false);
        }
      });
    cy.get('[data-testid="Endpoints-status"]', SHORT_TIME)
      .should("have.length", 2)
      .eq(1)
      .contains(DEPLOYMENT_SUCCESS, SHORT_TIME)
      .should("exist");
  }

  static initiateServiceDeployment(visibilityLevel: string) {
    cyGet('[data-testid="btn-deploy-api"]', VERY_LONG_TIME).should(
      "be.enabled"
    );
    cyGet('[data-testid="btn-deploy-api"]', LONG_TIME).click();
    cyGet('[data-testid="Readinglist-edit-btn"]', LONG_TIME).should(
      "be.visible"
    );
    cy.get(`[data-cyid="${visibilityLevel}-chip"]`, LONG_TIME).should(
      "be.visible"
    );
  }

  static changeVisibilityLevel(visibilityLevel: string) {
    cyGet('[data-testid="Readinglist-edit-btn"]', LONG_TIME).click();
    cyGet(`[data-testid="${visibilityLevel}-visibility-option"]`).click();
    cyGet('[data-cyid="endpoint-submit-btn-button"]').click();
  }

  static getVisibilityLevel() {
    return cy
      .get(
        `[data-cyid="Readinglist-endpoint-accordion"] [data-cyid*="-chip"]`,
        LONG_TIME
      )
      .eq(1)
      .invoke("text");
  }

  static deployServiceWithVisibilityLevel() {
    cyGet('[data-testid="btn-next"]', LONG_TIME).should("be.enabled");
    cyGet('[data-testid="btn-next"]', LONG_TIME).click();
    cy.wait(60000);
  }

  static verifyDeploymentStatusOfService(projectName, componentName) {
    cyGet('[data-testid="btn-stop"]', LONG_TIME).should("be.visible");
    GraphQL._getComponentDeploymentStatus(projectName, componentName);
    return cyGet('[data-cyid="deployment-status"]>h6').eq(0).invoke("text");
  }

  static promoteServiceWithVisibilityLevel() {
    cyGet('[data-cyid="btn-promote-button"]').should("be.enabled");
    cyGet('[data-cyid="btn-promote-button"]').click();
    cyGet('[data-testid="btn-next"]', LONG_TIME).click();
    cy.wait(60000);
    return cyGet('[data-cyid="deployment-status"]>h6').eq(1).invoke("text");
  }

  static deployManualTriggerWithConfig(url: string) {
    cyGet('[data-testid="btn-deploy-api"]', LONG_TIME).should("be.enabled");
    cyGet('[data-testid="btn-deploy-api"]').click();
    cyGet('[data-cyid="invke_url"]>input').type(url);
    cyGet('[data-cyid="btn-submit-configform"]').click();
    cyGet('[data-cyid="run-once-button"]', VERY_LONG_TIME).should("be.enabled");
    cyGet('[data-cyid="btn-promote-button"]').should("be.enabled");
  }

  static promoteManualTriggerWithConfig(url: string) {
    cyGet('[data-cyid="btn-promote-button"]').should("be.enabled");
    cyGet('[data-cyid="btn-promote-button"]').click();
    cyGet('[data-cyid="btn-next-button"]').should("be.visible").click();
    cyGet('[data-cyid="invke_url"]>input').type(url);
    cyGet('[data-cyid="btn-submit-configform"]').click();
    cyGet('[data-cyid="run-once-button"]', VERY_LONG_TIME)
      .eq(1)
      .should("be.enabled");
  }

  static runManualTrigger(env: Enums.Environment, count: number) {
    for (let i = 0; i < count; i++) {
      if (env === Enums.Environment.DEVELOPMENT) {
        cyGet('[data-cyid="run-once-button"]').eq(0).click();
      } else {
        cyGet('[data-cyid="run-once-button"]').eq(1).click();
      }

      cy.wait(5000);
    }
  }
}
