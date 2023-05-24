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

import { cyGet } from "../../../commons/cy";
import { LONG_TIME, SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { PUBLISHER_API_KEYS_URL } from "../../../commons/urls";
import { Utils } from "../../../commons/utils";
import { GraphQL } from "../../apis/graphql";

export class APIDeployment {
  static DeployToDev(projectName: string, componentName: string) {
    cy.intercept({ method: "GET", url: PUBLISHER_API_KEYS_URL, times: 1 }).as(
      "keys"
    );
    cyGet('[data-cyid="btn-deploy-proxy"]', SHORT_TIME).contains("Generating Configurations").should("not.exist");
    this.RetryDevDeployment();
    cyGet('[data-cyid="btn-deploy-proxy"]').should("not.be.disabled").click();

    cy.wait("@keys", VERY_SHORT_TIME).then(() => {
      cyGet('[data-cyid="btn-next"]').should("be.visible").click();
      this.RetryDevDeployment();
      cyGet('[data-cyid="deployment-status"]')
        .contains("Active")
        .should("be.visible");
      cyGet('[data-cyid*="promote"]').should("not.be.disabled");
      GraphQL.getComponentInfo(projectName, componentName);
    });
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
        cy.wait(LONG_TIME.timeout);
      } else {
        return;
      }
      this.RetryDevDeployment(retryCount);
    });
  }

  static PromoteToProd() {
    cy.get('[data-cyid*="promote"]').click();
    cy.get('[data-cyid="btn-next"]').should("be.visible").click();
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
