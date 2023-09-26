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

import {
  LONG_TIME,
  SHORT_TIME,
  VERY_LONG_TIME,
  VERY_SHORT_TIME,
} from "../../../commons/timeouts";
import { cyGet } from "../../../commons/cy";
import { PUBLISHER_API_KEYS_URL } from "../../../commons/urls";
import { GraphQL } from "../../apis/graphql";
import { MEDIUM_TIME } from "../../../commons/timeouts";
import {
  BUILD_FAILED,
  BUILD_PARTIAL,
  BUILD_SUCCESS,
  DEPLOYMENT_STOPPED,
  DEPLOYMENT_SUCCESS,
} from "../../../commons/constants";
import { Utils } from "../../../commons/utils";

export class APIDeployment {
  static deployToDev() {
    cy.intercept({ method: "GET", url: PUBLISHER_API_KEYS_URL, times: 1 }).as(
      "keys"
    );
    cyGet('[data-cyid="default-build-card"]', MEDIUM_TIME)
      .should("be.visible")
      .wait(1000);
    cyGet('[data-cyid="default-build-card"]', MEDIUM_TIME)
      .contains("Generating Configurations", MEDIUM_TIME)
      .should("not.exist");
    cyGet('[data-cyid="default-build-card"]', MEDIUM_TIME)
      .contains("Loading Configurations", MEDIUM_TIME)
      .should("not.exist");
    cyGet('[data-cyid="default-build-card"]', MEDIUM_TIME)
      .contains("Loading", MEDIUM_TIME)
      .should("not.exist");
    this.RetryDevDeployment();
    cyGet('[data-cyid="direct-deploy-option-proxy-split-toggle-button-button"]', MEDIUM_TIME)
      .should("not.be.disabled")
      .click();

    cyGet('[data-cyid="configure-&-deploy-option"]')
      .click();

    this.RetryDevDeployment();

    cy.wait("@keys", VERY_SHORT_TIME).then(() => {
      cyGet('[data-cyid="btn-next-button"]').should("be.visible").click();
      this.RetryDevDeployment();
      cy.get('[id="circular-loader"]').should("not.exist");

      cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME)
        .eq(0)
        .should("not.contain", DEPLOYMENT_STOPPED);

      cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME)
        .eq(0)
        .should("contain", DEPLOYMENT_SUCCESS);

      cyGet('[data-cyid*="promote"]').should("not.be.disabled");
      cy.get('[id="circular-loader"]').should("not.exist");
    });
  }

  static deployProxyAPIToDev() {
    cyGet('[data-cyid="direct-deploy-option-proxy-split-toggle-button-button"]', MEDIUM_TIME)
      .should("not.be.disabled")
      .click();

    cyGet('[data-cyid="configure-&-deploy-option"]')
      .click();
  }

  static verifyProxyDeployment(buildCount: number) {
    // For every deployment, there will be 2 new build statuses in the DOM, even though only one is visible
    buildCount = buildCount + 2;

    this.checkBuildStatus(buildCount);

    cy.get('[data-cyid="btn-submit-configform"]', VERY_LONG_TIME)
      .should("be.visible")
      .click();

    this.RetryDevDeployment();

    this.checkBuildStatus(buildCount);

    this.waitForDevDeployment();
    cy.get('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME)
      .eq(0, VERY_LONG_TIME)
      .should("contain", "Active");

    return buildCount;
  }

  static waitForDevDeployment(retryCount = 0) {
    cy.log("Waiting for proxy deployment");
    if (retryCount > 15) {
      return;
    }

    cy.get('[data-cyid="default-build-card"]').within(() => {
      cy.get('[data-cyid="map-build-status"]')
        .eq(0)
        .contains(BUILD_FAILED)
        .should("not.exist");
    });

    this.RetryDevDeployment();

    retryCount++;
    cy.get("body").then((body) => {
      const element = body
        .find('[data-cyid="deployment-status"]>h6')
        .filter((index, el) => {
          return el.textContent.trim() === "Active";
        });

      if (element.length > 0) {
        let isNewDeployment = false;
        body
          .find('[data-cyid="env-baseDevelopment-env-card"]')
          .each((index, element) => {
            const timeElement = element.querySelector(
              '[data-cyid="proxy-deployed-time"]>span>p'
            );
            console.log(timeElement);
            if (timeElement) {
              const deployedTime = timeElement.textContent.trim();
              const timeRegex = /^(\d+)\s+(minute|second)s?\s+ago$/;
              const match = deployedTime.match(timeRegex);
              if (match) {
                const time = parseInt(match[1]);
                const unit = match[2];
                if (
                  (unit === "minute" && time <= retryCount) ||
                  (unit === "second" && time < 60)
                ) {
                  cy.log(
                    "Deployed time is less than " +
                      retryCount +
                      " minute(s) or 60 seconds"
                  );
                  isNewDeployment = true;
                  return;
                }
              } else {
                cy.log("Unable to extract deployed time");
              }
            } else {
              cy.log("Skipping env card without deployed time");
            }
          });
        if (isNewDeployment) {
          return;
        }
      }
      cy.wait(SHORT_TIME.timeout);
      this.waitForDevDeployment(retryCount);
    });
  }

  private static checkBuildStatus(deploymentIteration: number) {
    cy.log("Checking Build Status");

    Utils.clickOnOptionalElement(
      '[data-testid="Retry-button-button"]',
      VERY_SHORT_TIME.timeout
    );

    // Ensure that the new build process has been triggered
    cy.get('[data-cyid="default-build-card"]').within(() => {
      cy.get('[data-cyid="map-build-status"]', MEDIUM_TIME)
        .should("be.visible")
        .and("have.length", deploymentIteration);
    });

    cy.get('[data-cyid="default-build-card"]').within(() => {
      cy.get('[data-cyid="map-build-status"]')
        .eq(0)
        .contains(BUILD_FAILED)
        .should("not.exist");
    });

    const regex = new RegExp(`(${BUILD_SUCCESS}|^${BUILD_PARTIAL})`, "gm");

    // Check if the latest build has reached a completed state
    cy.get('[data-cyid="default-build-card"]').within(() => {
      cy.get('[data-cyid="map-build-status"]')
        .eq(0)
        .contains(regex, VERY_LONG_TIME)
        .should("be.visible");
    });
  }

  static RetryDevDeployment() {
    cy.contains('role="progressbar"').should("not.exist");

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

  static RetryPromotionToProd(retryCount = 0) {
    cy.log("Checking for retry for promotion to prod");
    retryCount++;
    if (retryCount > 4) {
      return;
    }

    cy.contains('role="progressbar"').should("not.exist");

    cy.get("body").then((bdy) => {
      if (bdy.find('[data-testid="deployment-fetch-error"]').length > 0) {
        cy.log("Retry count: " + retryCount);
        cy.get('[data-testid="deployment-fetch-error"]').within(() => {
          cy.get('[data-testid="retry-btn"]').click();
          cy.wait(LONG_TIME.timeout);
        });
      } else {
        return;
      }
      this.RetryPromotionToProd(retryCount);
    });
  }

  static promoteToProd(
    projectName: string = "",
    componentName: string = "",
    hasMediationPolicy: boolean = false
  ) {
    this.RetryPromotionToProd();
    cyGet('[data-cyid="btn-promote-button"]').should("be.enabled").click();
    cy.wait(5000);
    cy.contains('role="progressbar"').should("not.exist");
    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="btn-next-button"]').length > 0) {
        cy.get('[data-cyid="btn-next-button"]').should("be.visible").click();
      }
    });
    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="expand-more"]').length > 0) {
        cy.get(".ConfigForm").within(() => {
          cy.get("button").contains("Promote").click(); // Promote button
        });
      }
    });
    cy.get("body").then((bdy) => {
      if (bdy.find(".ConfigForm").length > 0) {
        cy.get(".ConfigForm").within(() => {
          cy.get("button").contains("Promote").click(); // Promote button
        });
      }
    });
    if (hasMediationPolicy) {
      cy.wait(15000);
      GraphQL.getPrmotionStatus(projectName, componentName);
    }
    this.RetryPromotionToProd();
    cy.get('[data-cyid="env-baseProduction-env-card"]')
      .contains("Production")
      .should("be.visible");
    cyGet('[data-cyid="deployment-status"]', SHORT_TIME).should("have.length", 2);
    cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME)
      .eq(1)
      .should("contain", "Active");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }
}
