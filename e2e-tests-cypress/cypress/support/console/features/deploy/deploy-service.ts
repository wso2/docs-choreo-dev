/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import {
  BUILD_FAILED,
  CONFIG_CONTENT,
  DEPLOYMENT_PENDING,
  DEPLOYMENT_PROGRESSING,
  DEPLOYMENT_SUCCESS,
} from "../../../commons/constants";
import { cyGet } from "../../../commons/cy";
import { LONG_TIME, MEDIUM_TIME, SHORT_TIME } from "../../../commons/timeouts";
import { ConfigEntryStep, Types } from "../../../commons/types";
import { Utils } from "../../../commons/utils";
import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { Service } from "../../entities/component/service-component";
import { DeploymentTrack } from "../deployment-track/deployment-track";
import { ManualTrigger } from "../../entities/component/manual-trigger-component";
import { ScheduleTrigger } from "../../entities/component/schedule-trigger-component";
import { WebApp } from "../../entities/component/webapp-component";
import { Webhook } from "../../entities/component/webhook-component";
import { TestRunner } from "../../entities/component/test-runner-component";
import { EndpointAccessibility, Enums } from "../../../commons/enums";
import { Byoc } from "../../entities/component/byoc-component";

export interface DeployServiceFeature {
  _addNewVersion(component: Service, branch: string, version: string);

  _deployService(
    component: Service,
    shouldModifyEndpoint: boolean,
    endpointVisibility: EndpointAccessibility,
    configStepsAvailable?: ConfigEntryStep[]
  );

  _deployTask(
    component: ManualTrigger | ScheduleTrigger | TestRunner | Byoc,
    configStepsAvailable?: ConfigEntryStep[]
  );

  _deployWebapp(component: WebApp, hasAuthSettings: boolean);

  _deployWebhook(component: Webhook, configStepsAvailable?: ConfigEntryStep[]);

  _promoteService(
    component: Service,
    shouldModifyEndpoint: boolean,
    endpointVisibility: EndpointAccessibility,
    configStepsAvailable?: ConfigEntryStep[]
  );

  _promoteTask(
    component: ManualTrigger | ScheduleTrigger | TestRunner | Byoc,
    configStepsAvailable?: ConfigEntryStep[]
  );

  _promoteWebapp(
    component: WebApp,
    hasAuthSettings: boolean,
    configStepsAvailable?: ConfigEntryStep[]
  );

  _promoteWebhook(component: Webhook, configStepsAvailable?: ConfigEntryStep[]);
}

export function mixinServiceDeploy<T extends Types.Constructor>(
  base: T
): Types.Constructor<DeployServiceFeature> & T {
  return class extends base {
    private sideMenu = new ServiceLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    _deployService(
      component: Service,
      shouldModifyEndpoint: boolean,
      endpointVisibility: EndpointAccessibility,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.waitTillReadyToDeploy();

      this.startDeployment(component);

      this.stepThroughConfigSteps(component, configStepsAvailable);

      this.reviewAndUpdateEndpoint(
        component,
        shouldModifyEndpoint,
        endpointVisibility
      );

      this.verifyDeploymentStatus();

      this.verifyEndpointStatus(Enums.Environment.DEVELOPMENT);

      this.verifyEndpointAccessibility(
        component,
        endpointVisibility,
        Enums.Environment.DEVELOPMENT
      );
    }

    _deployTask(
      component: ManualTrigger | ScheduleTrigger | TestRunner,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.waitTillReadyToDeploy();

      this.startDeployment(component);

      this.stepThroughConfigSteps(component, configStepsAvailable);

      this.verifyTaskDeploymentStatus();
    }

    _deployWebapp(component: WebApp, hasAuthSettings: boolean) {
      this.sideMenu.navigateToDeploy();

      this.waitTillReadyToDeploy();

      this.startDeployment(component);

      this.configureWebApp(hasAuthSettings);

      this.verifyDeploymentStatus();

      this.storeDevWebAppUrl(component);
    }

    _deployWebhook(
      component: Webhook,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();
      this.waitTillReadyToDeploy();
      this.startWebhookDeployment(component);
      this.stepThroughConfigSteps(component, configStepsAvailable);
      this.verifyDeploymentStatus();
    }

    //Promotion methods start here

    _promoteWebhook(
      component: Webhook,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();
      this.webhookPromotion(component);
      this.stepThroughConfigSteps(component, configStepsAvailable);
      this.verifyPromotionStatus();
    }

    _promoteService(
      component: Service,
      shouldModifyEndpoint: boolean,
      endpointVisibility: EndpointAccessibility,
      configStepsAvailable: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.startPromotion(component);

      this.stepThroughConfigSteps(component, configStepsAvailable);

      this.reviewAndUpdateEndpoint(
        component,
        shouldModifyEndpoint,
        endpointVisibility
      );

      this.verifyPromotionStatus();

      this.verifyEndpointStatus(Enums.Environment.PRODUCTION);

      this.verifyEndpointAccessibility(
        component,
        endpointVisibility,
        Enums.Environment.PRODUCTION
      );
    }

    _promoteTask(
      component: ManualTrigger | ScheduleTrigger | TestRunner | Byoc,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.startPromotion(component);

      this.stepThroughConfigSteps(component, configStepsAvailable);

      this.verifyTaskPromotionStatus();
    }

    _promoteWebapp(
      component: WebApp,
      hasAuthSettings: boolean,
      configStepsAvailable: ConfigEntryStep[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.startPromotion(component);

      this.stepThroughConfigSteps(component, configStepsAvailable);

      this.configureWebApp(hasAuthSettings);

      this.verifyPromotionStatus();

      this.storeProdWebAppUrl(component);
    }

    _addNewVersion(component: Service, branch: string, version: string) {
      this.sideMenu.navigateToDeploy();

      component.updateVersionList(version);

      cy.get(TestIds.selectVersion).click();
      cy.get(TestIds.createVersion).should("be.visible").click();
      cy.get(TestIds.dialog).within(() => {
        cy.get('[data-testid*="feature"]').click();
      });
      cy.get(`[data-value="${branch}"]`).click();
      cy.get(TestIds.dialog).within(() => {
        cy.get(TestIds.versionName).type(version);
        cy.get(TestIds.createDeploymentTrack).click();
        cy.get(TestIds.createDeploymentTrack).should("not.exist");
      });
    }

    private saveEndpointUrls(
      service: Service,
      endpointVisibility: EndpointAccessibility,
      environment: Enums.Environment
    ) {
      let envCardSelector = TestIds.devEnvCard;

      if (environment === Enums.Environment.PRODUCTION) {
        envCardSelector = TestIds.prodEnvCard;
      }

      cy.get(envCardSelector).within(() => {
        cy.get(TestIds.availableEndpoints).within(() => {
          cy.get(TestIds.viewArtifact).click();
        });
      });

      cy.get(TestIds.componentLoader).should("not.exist");

      let urlTypeRegex = /^Project URL.*/;
      let urlMatcher = /^http:\/\/.*/;

      if (endpointVisibility === EndpointAccessibility.Public) {
        urlTypeRegex = /^Public URL.*/;
        urlMatcher = /^https:\/\/.*/;
      } else if (endpointVisibility === EndpointAccessibility.Organization) {
        urlTypeRegex = /^Organization URL.*/;
        urlMatcher = /^https:\/\/.*/;
      }

      cy.get(TestIds.endpointCard)
        .should("be.visible")
        .contains(urlTypeRegex)
        .next()
        .invoke("attr", "title")
        .then((url) => {
          if (url === undefined) {
            throw new Error("URL is undefined");
          }

          expect(url).to.match(urlMatcher);
          if (environment == Enums.Environment.DEVELOPMENT) {
            service.setDevEndpointUrl(url);
          } else {
            service.setProdEndpointUrl(url);
          }
        });
    }

    private waitTillReadyToDeploy() {
      Utils.getRenderedElement(TestIds.buildCard);
      cyGet(TestIds.buildCard, MEDIUM_TIME).should("be.visible").wait(1000);
      cyGet(TestIds.buildCard, MEDIUM_TIME)
        .contains("Generating Configurations", MEDIUM_TIME)
        .should("not.exist");
      cyGet(TestIds.buildCard, MEDIUM_TIME)
        .contains("Loading Configurations", MEDIUM_TIME)
        .should("not.exist");
      cyGet(TestIds.buildCard, MEDIUM_TIME)
        .contains("Loading", MEDIUM_TIME)
        .should("not.exist");
      this.retryEnvCardDataRetrieval();
    }

    private startDeployment(
      component: Service | ManualTrigger | ScheduleTrigger | TestRunner | WebApp
    ) {
      this.deploymentTrack.validate(component);

      cyGet(TestIds.deploySplitToggle, MEDIUM_TIME)
        .should("be.enabled")
        .click();
      cyGet(TestIds.configureDeploy).click();
      cyGet(TestIds.executeDeploy, MEDIUM_TIME).should("be.enabled").click();
    }

    private startWebhookDeployment(component: Webhook) {
      cyGet(TestIds.deploySplitToggle, MEDIUM_TIME)
        .should("be.enabled")
        .click();
      cyGet(TestIds.configureDeploy).click();
      cyGet(TestIds.executeDeploy, MEDIUM_TIME).should("be.enabled").click();
    }

    private webhookPromotion(component: Webhook) {
      cy.wait(3000);
      cyGet(TestIds.promote, MEDIUM_TIME).should("be.enabled").click();
      cy.get(TestIds.next).should("be.visible").click();
    }

    private stepThroughConfigSteps(
      component:
        | Webhook
        | Service
        | TestRunner
        | ManualTrigger
        | ScheduleTrigger
        | WebApp
        | Byoc,
      configStepsAvailable?: ConfigEntryStep[]
    ) {
      if (configStepsAvailable === undefined) {
        return;
      }

      for (let i = 0; i < configStepsAvailable.length; i++) {
        const configStep = configStepsAvailable[i];
        // undefined means that there are no config values to enter for respective step
        if (configStep.configEntryFunction !== undefined) {
          if (configStep.args !== undefined) {
            configStep.configEntryFunction(configStep.args);
          } else {
            configStep.configEntryFunction();
          }
        } else {
          cy.get(TestIds.next).should("be.visible").click();
        }
      }
    }

    private reviewAndUpdateEndpoint(
      component: Service,
      shouldModifyEndpoint: boolean,
      endpointVisibility: EndpointAccessibility
    ) {
      cy.get(
        `[data-cyid="${component.getEndpointName()}-endpoint-accordion"]`
      ).should("be.visible");

      if (shouldModifyEndpoint) {
        cy.get(`[data-testid="${component.getEndpointName()}-edit-btn"]`)
          .should("be.visible")
          .click();
        cy.get(TestIds.endpointVisibility(endpointVisibility))
          .should("be.visible")
          .click();
        cy.get(TestIds.endpointSubmit).click();
      }

      cyGet(TestIds.next).click();
    }

    private verifyDeploymentStatus() {
      this.retryEnvCardDataRetrieval();

      cy.get(TestIds.devEnvCard).within(() => {
        cyGet(TestIds.stop, LONG_TIME).should("be.visible");
        cy.wait(600);
        cyGet(TestIds.stop, MEDIUM_TIME).should("be.visible");

        cy.get(TestIds.deploymentStatus, LONG_TIME)
          .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
          .should("be.visible");
      });
    }

    private verifyEndpointStatus(env: Enums.Environment) {
      let envCardSelector = TestIds.devEnvCard;

      if (env === Enums.Environment.PRODUCTION) {
        envCardSelector = TestIds.prodEnvCard;
      }

      cy.get(envCardSelector).within(() => {
        cyGet(TestIds.availableEndpoints).should("be.visible");
      });

      this.verifyEndpointIsDeployed(envCardSelector);

      cy.get(envCardSelector).within(() => {
        cyGet(TestIds.deploymentStatus, SHORT_TIME)
          .contains(DEPLOYMENT_PENDING, SHORT_TIME)
          .should("not.exist");
        cyGet(TestIds.deploymentStatus, SHORT_TIME)
          .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
          .should("not.exist");
      });
    }

    private verifyTaskDeploymentStatus() {
      cy.get(TestIds.devEnvCard).within(() => {
        cy.get(TestIds.deploymentHistory).should("be.visible");
      });
    }

    private verifyPromotionStatus() {
      this.retryEnvCardDataRetrieval();

      cy.get(TestIds.prodEnvCard).within(() => {
        cyGet(TestIds.stop, LONG_TIME).should("be.visible");
        cy.wait(600);
        cyGet(TestIds.stop, MEDIUM_TIME).should("be.visible");

        cy.get(TestIds.deploymentStatus, LONG_TIME)
          .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
          .should("be.visible");
      });
    }

    private verifyTaskPromotionStatus() {
      cy.get(TestIds.prodEnvCard).within(() => {
        cy.get(TestIds.deploymentHistory).should("be.visible");
      });
    }

    private verifyEndpointAccessibility(
      service: Service,
      endpointVisibility: EndpointAccessibility,
      env: Enums.Environment
    ) {
      // Begin workaround for https://github.com/wso2-enterprise/choreo/issues/25414
      this.sideMenu.navigateToOverview();
      this.sideMenu.navigateToDeploy();
      // End workaround for https://github.com/wso2-enterprise/choreo/issues/25414

      this.saveEndpointUrls(service, endpointVisibility, env);
      // Non public endpoints are not accessible over the internet
      if (endpointVisibility !== EndpointAccessibility.Public) {
        this.sideMenu.navigateToTest();
        cy.get(TestIds.notificationBanner).should("be.visible");
        this.sideMenu.navigateToManage();
        cy.get(TestIds.noEndpointNotification).should("be.visible");
      }
    }

    private verifyEndpointIsDeployed(envCardLocator: string) {
      for (var i = 0; i < 6; i++) {
        let isEndpointLoaded = false;
        cy.get(envCardLocator, { log: false }).then((envCard) => {
          if (envCard.find(TestIds.endpointStatus).length > 0) {
            cy.get(TestIds.endpointStatus, { log: false }).then(
              ($statusElement) => {
                let statusText = $statusElement.children().eq(0).text();

                const waitTime = 10000;
                if (
                  statusText.includes(DEPLOYMENT_PENDING) ||
                  statusText.includes(DEPLOYMENT_PROGRESSING)
                ) {
                  cy.get(TestIds.commitHistory)
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
          } else {
            cy.wait(5000, { log: false });
          }
        });

        if (isEndpointLoaded) {
          break;
        }
      }

      cy.get(envCardLocator).within(() => {
        cyGet(TestIds.endpointStatus).contains(DEPLOYMENT_SUCCESS);
      });
    }

    private retryEnvCardDataRetrieval() {
      cy.log("Checking for retry deployment");
      for (let i = 0; i < 4; i++) {
        Utils.clickOnOptionalElement(TestIds.retry, LONG_TIME.timeout);

        Utils.clickOnOptionalElement(TestIds.refresh, SHORT_TIME.timeout);

        Utils.waitIfOptionalElementPresent(TestIds.notDeployed, 3000);
      }

      cy.get(TestIds.retry).should("not.exist");
      cy.get(TestIds.refresh).should("not.exist");
    }

    private retryPromotionToProd(retryCount = 0) {
      retryCount++;
      cy.log(
        "Checking for retry for promotion to prod, attempt: " + retryCount
      );
      if (retryCount > 4) {
        return;
      }

      cy.get("body", { log: false }).then((bdy) => {
        if (bdy.find(TestIds.deploymentFetchError).length > 0) {
          cy.log("Retry count: " + retryCount);
          cy.get(TestIds.deploymentFetchError).within(() => {
            cy.get(TestIds.retry).click();
            cy.wait(LONG_TIME.timeout);
          });
        }

        cy.wait(1300, { log: false });
        this.retryPromotionToProd(retryCount);
      });
    }

    private startPromotion(
      component: Service | ManualTrigger | ScheduleTrigger | TestRunner | WebApp | Byoc
    ) {
      this.deploymentTrack.validate(component);

      this.retryPromotionToProd();
      cy.get(TestIds.promote, LONG_TIME).should("be.enabled").click();

      if (component instanceof ScheduleTrigger) {
        cy.get(TestIds.next, LONG_TIME).should("be.enabled").click();
        cy.get('[value="*/1 * * * *"]', LONG_TIME).eq(1).should("be.visible");
      }
    }

    private configureWebApp(hasAuthSettings: boolean) {
      cy.get(TestIds.formConfigField).type("{backspace}").type(CONFIG_CONTENT);
      cy.get(TestIds.next).click();

      if (hasAuthSettings) {
        cy.contains("h5", "Authentication Settings").click();
        cy.get(TestIds.next).should("be.visible").click();
      }
    }

    private storeDevWebAppUrl(component: WebApp) {
      cy.get(TestIds.devEnvCard).within(() => {
        cy.get(TestIds.appUrl)
          .invoke("attr", "href")
          .then((url) => {
            if (url) {
              component.setDevWebAppUrl(url);
            } else {
              throw new Error("href attribute is undefined");
            }
          });
      });
    }

    private storeProdWebAppUrl(component: WebApp) {
      cy.get(TestIds.prodEnvCard).within(() => {
        cy.get(TestIds.appUrl)
          .invoke("attr", "href")
          .then((url) => {
            if (url) {
              component.setProdWebAppUrl(url);
            } else {
              throw new Error("href attribute is undefined");
            }
          });
      });
    }
  };
}
