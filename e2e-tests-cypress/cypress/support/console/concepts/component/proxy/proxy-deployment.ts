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
  BUILD_IN_PROGRESS,
  BUILD_QUEUED,
  DEPLOYMENT_STOPPED,
  DEPLOYMENT_SUCCESS,
} from "../../../../commons/constants";
import { cyGet } from "../../../../commons/cy";
import { Enums } from "../../../../commons/enums";
import {
  LONG_TIME,
  MEDIUM_TIME,
  SHORT_TIME,
  VERY_LONG_TIME,
  VERY_SHORT_TIME,
} from "../../../../commons/timeouts";
import { Utils } from "../../../../commons/utils";
import { TestIds } from "../../../constants/TestIds";
import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "./proxy-component";
import { ProxyUtils } from "./proxy-utils";

export class _ProxyDeployment {
  private sideMenu = new ProxyLeftMenu();

  deploy(component: Proxy) {
    this.sideMenu.navigateToDeploy();

    ProxyUtils.validateDeploymentTrack(component);

    this.waitTillReadyToDeploy();

    this.RetryDevDeployment();

    this.startDeployment();

    this.verifyDeploymentStatus(component);
  }

  promote(component: Proxy) {
    this.sideMenu.navigateToDeploy();

    ProxyUtils.validateDeploymentTrack(component);

    this.waitTillReadyToDeploy();

    this.RetryPromotionToProd();
    cyGet(TestIds.promote).should("be.enabled").click();

    if (component.isPolicyAdded()) {
      cy.get(TestIds.configSubmit, VERY_SHORT_TIME)
        .should("be.visible")
        .click();
      cy.get(TestIds.configSubmit).should("not.exist");
    } else {
      cy.get(TestIds.next, SHORT_TIME).should("be.visible").click();
      cy.get(TestIds.next).should("not.exist");
    }

    cy.get(TestIds.progressBar, SHORT_TIME).eq(0).should("not.be.visible");

    this.RetryPromotionToProd();
    cy.get(TestIds.prodEnvCard).should("exist");
    cy.get(TestIds.prodEnvCard).within(() => {
      cyGet(TestIds.deploymentStatus, VERY_LONG_TIME).should(
        "contain",
        DEPLOYMENT_SUCCESS
      );
    });

    cy.get(TestIds.promote).should("not.be.disabled");
  }

  addNewVersion(component: Proxy, version: string = "1.1") {
    this.sideMenu.navigateToDeploy();

    component.updateVersionList(version);

    cy.get(TestIds.versionPicker).click();
    cy.get(TestIds.createProxyVersion).should("be.visible").click();
    cy.get(TestIds.dialog).within(() => {
      cy.get(TestIds.versionName).type(version);
      cy.get(TestIds.createProxyVersionDialog).click();
      cy.get(TestIds.createProxyVersionDialog).should("not.exist");
    });
  }

  private RetryPromotionToProd(retryCount = 0) {
    retryCount++;
    cy.log("Checking for retry for promotion to prod, attempt: " + retryCount);
    if (retryCount > 4) {
      return;
    }

    cy.get("body", { log: false }).then((bdy) => {
      if (bdy.find('[data-testid="deployment-fetch-error"]').length > 0) {
        cy.log("Retry count: " + retryCount);
        cy.get('[data-testid="deployment-fetch-error"]').within(() => {
          cy.get('[data-testid="retry-btn"]').click();
          cy.wait(LONG_TIME.timeout);
        });
      }

      cy.wait(1300, { log: false });
      this.RetryPromotionToProd(retryCount);
    });
  }

  private waitTillReadyToDeploy() {
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
  }

  private RetryDevDeployment() {
    cy.log("Checking for retry deployment");
    for (let i = 0; i < 4; i++) {
      Utils.clickOnOptionalElement(
        '[data-testid="retry-btn"]',
        LONG_TIME.timeout
      );

      Utils.clickOnOptionalElement(
        '[data-cyid="refresh-button-button"]',
        SHORT_TIME.timeout
      );

      Utils.waitIfOptionalElementPresent(
        '[data-cyid="card-body-not-deployed"]',
        3000
      );
    }

    cy.get('[data-testid="retry-btn"]').should("not.exist");
    cy.get('[data-cyid="refresh-button-button"]').should("not.exist");
  }

  private startDeployment() {
    cyGet(TestIds.deployProxySplitToggle, MEDIUM_TIME)
      .should("not.be.disabled")
      .click();

    cyGet(TestIds.configureDeploy, VERY_SHORT_TIME).click();

    cyGet(TestIds.executeDeployProxySplitToggle, MEDIUM_TIME)
      .should("not.be.disabled")
      .click();
  }

  private verifyDeploymentStatus(component: Proxy) {
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.buildStatus)
      .eq(0)
      .contains(BUILD_IN_PROGRESS, LONG_TIME)
      .should("not.exist");

    if (component.isPolicyAdded()) {
      cy.get(TestIds.configSubmit, SHORT_TIME).should("be.visible").click();
    } else {
      cy.get(TestIds.next, VERY_SHORT_TIME).should("be.visible").click();
    }
    cy.get(TestIds.buildStatus, SHORT_TIME)
      .eq(0)
      .contains(BUILD_QUEUED)
      .should("not.exist");
    this.RetryDevDeployment();
    cy.get(TestIds.componentLoader).should("not.exist");

    cy.get(TestIds.buildStatus)
      .eq(0)
      .contains(BUILD_IN_PROGRESS, LONG_TIME)
      .should("not.exist");

    cy.get(TestIds.devEnvCard).within(() => {
      cy.get(TestIds.deploymentStatus, LONG_TIME)
        .contains(DEPLOYMENT_STOPPED, LONG_TIME)
        .should("not.exist");

      cy.get(TestIds.deploymentStatus, LONG_TIME)
        .contains(DEPLOYMENT_SUCCESS, VERY_LONG_TIME)
        .should("be.visible");
    });

    cy.get(TestIds.promote).should("not.be.disabled");
    cy.get(TestIds.componentLoader).should("not.exist");
  }
}
