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

import { cyGet } from "../../../../commons/cy";
import {
  LONG_TIME,
  MEDIUM_TIME,
  SHORT_TIME,
} from "../../../../commons/timeouts";
import { Utils } from "../../../../commons/utils";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { ManualTrigger } from "./manualTrigger-component";


export class _ManualTriggerDeployment {
  private sideMenu = new ServiceLeftMenu();

  public deploy(
    component: ManualTrigger,
    configStepsAvailable = 1
  ) {
    this.sideMenu.navigateToDeploy();
    this.waitTillReadyToDeploy();
    this.startDeployment(component);
    this.stepThroughConfigSteps(configStepsAvailable);
  }

  private stepThroughConfigSteps(configStepsAvailable: number) {
    for (let i = 0; i < configStepsAvailable; i++) {
      cy.get(TestIds.next).should("be.visible").click();
    }
  }


  public promote(
    component: ManualTrigger,
  ) {
    this.sideMenu.navigateToDeploy();
    this.startPromotion(component);
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

  private startDeployment(component: ManualTrigger) {

    cyGet(TestIds.deploySplitToggle, MEDIUM_TIME).should("be.enabled").click();
    cyGet(TestIds.executeDeploy, MEDIUM_TIME).should("be.enabled").click();
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
    cy.log("Checking for retry for promotion to prod, attempt: " + retryCount);
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

  private startPromotion(component: ManualTrigger) {
    this.retryPromotionToProd();
    cy.get(TestIds.promote, LONG_TIME).should("be.enabled").click();
  }

}