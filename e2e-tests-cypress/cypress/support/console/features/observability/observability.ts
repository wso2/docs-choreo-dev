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

import { cyLog } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { VERY_SHORT_TIME } from "../../../commons/timeouts";
import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";

export class _Observability {
  private sideMenu = new ServiceLeftMenu();

  verifyObservabilityMetricsLogs(
    env: Enums.Environment,
    matchingText: string,
    delay: number = VERY_SHORT_TIME.timeout
  ) {
    this.sideMenu.navigateToMetrics();

    this.loadLogs(env, delay);
    this.verifyLogsAreFound(matchingText);
  }

  private loadLogs(env: Enums.Environment, delay: number) {
    cy.get(TestIds.diagramLoader).should("not.exist");

    cy.get(TestIds.environmentPickerObsMetrics)
      .should("exist")
      .click()
      .then(() => {
        cy.get(TestIds.envSelectorItemsObservability)
          .should("be.visible")
          .within(() => {
            // Due to rerendering query the env again after the initial load
            cy.contains(env).should("be.visible");
            cy.contains(env).click();
          });
      });

    cy.log(`Wait ${delay / 1000}s for stats to be collected`);
    cy.wait(delay, { log: false });
    cy.get(TestIds.diagramLoader).should("not.exist");

    for (let i = 0; i < 10; i++) {
      cy.get("body", { log: false }).then((body) => {
        if (body.find(TestIds.refreshLogs).length > 0) {
          cy.get(TestIds.refreshLogs).click();
          cy.get(TestIds.diagramLoader).should("not.exist");
          const waitTime = 8000 * (i + 1);
          cy.log(`Waiting ${waitTime / 1000}s for logs to load after refresh`);
          cy.wait(waitTime, { log: false });
        } else if (body.find(TestIds.observabilityLogPanelEntry).length > 0) {
          return;
        } else {
          cy.wait(300, { log: false }); // Slow down the loop a little to give time for log window to load
        }
      });
    }
  }

  private verifyLogsAreFound(text: string) {
    let isFound = false;

    cy.get(TestIds.observabilityLogPanelEntry, { timeout: 180000 })
      .should("be.visible")
      .each((logElement) => {
        const log = logElement.text();
        cyLog(log);

        if (log.includes(text)) {
          isFound = true;
          return false;
        }
      })
      .then(() => {
        expect(isFound).to.be.true;
      });
  }
}
