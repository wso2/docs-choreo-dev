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

import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { MARKETPLACE_SERVICES_API } from "../../../commons/urls";
import { TestIds } from "../../constants/TestIds";
import { LeftMenu } from "./left-menu";

export class ConsoleLeftMenu extends LeftMenu {
  navigateToComponents() {
    this.navigateToMenuItem('[data-cyid="listing"]');
  }

  navigateToComponentUsageInsights() {
    this.navigateToMenuItem('[data-cyid="usage-insights"]');
    cy.contains("Coming Soon").should("be.visible");
  }

  navigateToProjectUsageInsights() {
    this.navigateToMenuItem('[data-cyid="project-usage-insights-button"]');
  }

  navigateToMarketplace() {
    cy.url().then((url) => {
      if (!url.includes("internal-marketplace")) { // If not already in the marketplace
        cy.intercept({ method: "GET", url: MARKETPLACE_SERVICES_API, times: 1, }).as("navigateToMarketPlace");

        cy.get('[data-cyid="marketplace-link"]', VERY_SHORT_TIME)
          .invoke("removeAttr", "target")
          .click();
        cy.get(TestIds.backdropLoader, SHORT_TIME).should("not.exist");
        cy.wait("@navigateToMarketPlace", VERY_SHORT_TIME);
      }
    });
    
  }
}
