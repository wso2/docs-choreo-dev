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

import { Enums } from "../../../commons/enums";
import { LONG_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";

export class InsightsPage {
  static selectEnvironment(env: Enums.Environment) {
    cy.contains("Environment").should("be.visible");
    cy.contains("Environment").next().click();
    cy.contains(env).click();
  }

  static selectTimePeriod(timePeriod: string = "Past 15 minutes") {
    cy.get('[data-testid="date-picker"]', VERY_SHORT_TIME).click();
    cy.get("ul>div").contains(timePeriod).click();
    cy.get('.recharts-layer>path[fill*="url"]');
  }

  static getTotalTraffic() {
    cy.get(".recharts-area");
    cy.contains("Total Traffic").should("be.visible");
    return cy.get("main").find("span>span").eq(0).invoke("text");
  }

  static getTotalErrorRequestCount() {
    return cy.get("main").find("span>span").eq(1).invoke("text");
  }

  static getAverageErrorRate() {
    return cy.get("main").find("span>span").eq(2).invoke("text");
  }
}
