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

<<<<<<< HEAD
import { cyGet, cyLog } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { LONG_TIME, SHORT_TIME, VERY_LONG_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
=======
import { cyGet } from "../../../commons/cy";
import {
  LONG_TIME,
  SHORT_TIME,
  VERY_SHORT_TIME,
} from "../../../commons/timeouts";
>>>>>>> 3e455fd0f76d5a82dc1b6c4079c32e26fe4d629c
import { PUBLISHER_API_KEYS_URL } from "../../../commons/urls";
import { GraphQL } from "../../apis/graphql";

export class APIDeployment {


  static DeployToDev(projectName: string, componentName: string) {
    cy.intercept({ method: "GET", url: PUBLISHER_API_KEYS_URL, times: 1 }).as(
      "keys"
    );
    cyGet('[data-cyid="btn-deploy-proxy"]', SHORT_TIME)
      .contains("Generating Configurations")
      .should("not.exist");
    this.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-proxy"]').should("not.be.disabled").click();

    cy.wait("@keys", VERY_SHORT_TIME).then(() => {
      cyGet('[data-cyid="btn-next"]').should("be.visible").click();
      this.RetryDevDeployment();
      cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME).eq(0).should('contain', 'Active')
      cyGet('[data-cyid*="promote"]').should("not.be.disabled");
      GraphQL.getComponentInfo(projectName, componentName);
    });
  }

  static deployProxyAPIToDev(projectName: string, componentName: string) {
    cyGet('[data-testid="btn-deploy-proxy"]').should('be.enabled').click()
    GraphQL.getComponentInfo(projectName, componentName);

  }

  static verifyProxyDeployment(isRedeployment = false) {
    GraphQL.getDeployStatus(Enums.DeploymentStages.CODE_GEN, Enums.DeploymentStatus.success)
    cy.get('button').contains('Save & Deploy', VERY_LONG_TIME).should('be.visible').click()


    if (isRedeployment) {
      GraphQL.getDeployStatus(Enums.DeploymentStages.PROXY_DEPLOY, Enums.DeploymentStatus.completed)
    }





    cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME).eq(0).should('contain', 'Active')
  }

  static RetryDevDeployment(retryCount = 0) {
    cy.log("Checking for retry deployment");
    retryCount++;
    if (retryCount > 4) {
      return;
    }

    cy.get("body").then((bdy) => {
      if (bdy.find('[data-testid="retry-button"]').length > 0) {
        cy.log("Retry count: " + retryCount);
        cy.get('[data-testid="retry-button"]').click();
        cy.wait(VERY_SHORT_TIME.timeout);
      } else {
        return;
      }
      this.RetryDevDeployment(retryCount);
    });
  }

  static RetryPromotionToProd(retryCount = 0) {
    cy.log("Checking for retry for promotion to prod");
    retryCount++;
    if (retryCount > 4) {
      return;
    }

    cy.get("body").then((bdy) => {
      if (bdy.find('[data-testid="deployment-fetch-error"]').length > 0) {
        cy.log("Retry count: " + retryCount);
        cy.get('[data-testid="deployment-fetch-error"]').within(() => {
          cy.get('[data-testid="retry-button"]').click();
          cy.wait(VERY_SHORT_TIME.timeout);
        });
      } else {
        return;
      }
        this.RetryPromotionToProd(retryCount);
    });
  }

  static PromoteToProd() {
<<<<<<< HEAD
    cy.get('[data-cyid*="promote"]').click().wait(5000);
    cy.get('body').then((bdy) => {

      if (bdy.find('[data-cyid="expand-more"]').length > 0) {

        if (bdy.find('[data-cyid="btn-next"]').length > 0) {
          cy.get('[data-cyid="btn-next"]').should("be.visible").click();
        }

        if (bdy.text().includes('Promote')) {
          cy.get('.ConfigForm').within(() => {
            cy.contains('Promote').click()  // Promote button
          })
        }
      }
    })



=======
    cy.get('[data-cyid*="promote"]').click();
    cy.get('[data-cyid="btn-next"]').should("be.visible").click();
    this.RetryPromotionToProd();
>>>>>>> 3e455fd0f76d5a82dc1b6c4079c32e26fe4d629c
    cy.get('[data-cyid="proxy-env-card-header"]>div>span')
      .contains("Production")
      .should("be.visible");
    cyGet('[data-cyid="deployment-status"]')
      .should("have.length", 2)
    cyGet('[data-cyid="deployment-status"]>h6', VERY_LONG_TIME).eq(1).should('contain', 'Active')
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }
}
