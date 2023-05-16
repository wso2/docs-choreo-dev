/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { SHORT_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";


export class ComponentObservePage {
  static gotoLogs(timeToWait = 0) {
    cy.wait(timeToWait);
    cy.get('[data-testid="panel-Logs-btn"]').should("be.visible").click();
  }

  static selectEnv(env: Enums.Environment) {
    let index = 0;
    if (env == Enums.Environment.DEVELOPMENT) {
      index = 1;
    }

    cy.get('[data-cyid="environment-selector"]').should("be.visible").click();
    cy.get(`#environment-selector-label-option-${index}`).click({
      force: true,
    });
  }

  static verifyTextInLogs(text: string) {
    cy.get('[data-testid="log-panel-entry"]', { timeout: 180000 })
      .should("be.visible")
      .each(($e) => {
        let log = $e
          .text()
          .replace("ballerina: sending metrics to Choreo", "")
          .trim()
          .toString();
        if (log.includes(text)) {
          const exactText = log.slice(log.indexOf("{"), log.indexOf("}") + 1);
          expect(text).to.be.eq(exactText);
        }
      });
  }

  static navigateToSampleApp() {
    const observabilityViewUrl =
      Cypress.env("loginURL").replace("login?fidp=choreoe2etest", "") +
      "observe/sample";
    Utils.setBrowserCookie();
    cy.visit(observabilityViewUrl);
    cy.url().should("eq", observabilityViewUrl);
    cy.get('[data-testid="backdrop-loader"]').should("not.exist");
  }

  static verifyLogsView() {
    const employeeInfoNotFoundLogEntry = "No logs found from";
    const commonLogLine = "employee information not found in the hr-service";

    cy.get('[data-testid="panel-Logs-btn"]').should("be.visible");
    cy.get('[data-testid="panel-Logs-btn"]').click();

    cy.log("Asserting mandatory log entry with part of the search phrase");
    cy.get("#log-search").should("be.visible");
    cy.get("#log-search").type(commonLogLine.substring(0, 13));
    cy.get('[data-testid="log-search-btn"]').click();
    cy.contains('[data-testid="log-panel-entry"]', commonLogLine).should(
      "exist"
    );

    cy.log("Asserting mandatory log entry by providing a search phrase");
    cy.get('[data-testid="log-search"]').clear();
    cy.get('[data-testid="log-search"]').type(commonLogLine);
    cy.get('[data-testid="log-search-btn"]').click();
    cy.contains('[data-testid="log-panel-entry"]', commonLogLine).should(
      "exist"
    );
    cy.contains(
      '[data-testid="log-panel"]',
      employeeInfoNotFoundLogEntry
    ).should("not.exist");
  }

  static verifyObserveOverview() {
    const employeeInfoNotFoundLogEntry =
      "employee information not found in the hr-service";
    const emptyHistogramMessage =
      "No requests received during the selected time period";
    const responseTimeRegexp = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}[+-]\d{2}:\d{2}/;
    let d;
    let prevY;
    let finalX;
    let finalY;
    cy.get(".diagram-canvas").should("exist");
    cy.get(".worker-line").should("exist");
    cy.get('[data-testid="preloader"]').should("not.exist");

    cy.contains(
      '[data-testid="histogram-throughput"]',
      emptyHistogramMessage
    ).should("not.exist");
    cy.contains(
      '[data-testid="histogram-response-time"]',
      emptyHistogramMessage
    ).should("not.exist");
    cy.get('[data-testid="histogram-throughput"]')
      .get("g.recharts-layer.recharts-area")
      .should("exist");
    cy.get('[data-testid="histogram-response-time"]')
      .get("g.recharts-layer.recharts-area")
      .should("exist");

    cy.log("Asserting the default log panel");
    cy.get('[data-testid="log-panel"]').should("exist");

    cy.log("Waiting on graphs to be expanded");
    cy.get('[data-testid="histogram-response-time"]')
      .find("g.recharts-layer.recharts-area")
      .find("path")
      .then(($path) => {
        cy.log("Getting coordinates to click on the latency graph");
        d = $path.attr("d");
        d = d.replace("Z", "");
        const newD = d.split("L");
        for (const v of newD) {
          const arr = v.split(",");
          if (prevY !== undefined && prevY !== arr[1]) {
            //Added margins to the coordinates manually
            finalX = arr[0] + 95;
            finalY = arr[1] - 30;
            break;
          }
          prevY = arr[1];
        }

        cy.get('[data-testid="histogram-throughput"]')
          .find("svg")
          .click(Math.round(finalX), Math.round(finalY));
        cy.get('[data-testid="preloader"]').should("not.exist");

        cy.log(
          "Asserting the log panel after clicking on the very first point in the latency graph"
        );
        cy.contains(
          '[data-testid="log-panel"]',
          employeeInfoNotFoundLogEntry
        ).should("not.exist");

        cy.log("Asserting the request list");
        cy.get('[data-testid="log-panel"]', SHORT_TIME).should(
          "exist"
        );
        cy.get('[data-testid="log-panel-entry"]')
          .its("length")
          .should("be.gte", 1);

        cy.get('[data-testid="log-panel-entry"]')
          .eq(0)
          .find("div>div")
          .then(($elements) => {
            expect($elements[0].textContent).to.match(responseTimeRegexp);
          });
      });
  }

  static verifyDiagnosticView() {
    const timestampRegex =
      /\d{4}\/\d{2}\/\d{2}\s\d{2}:\d{2}:\d{2}/;
    const numberOfBins = 5;

    cy.log("Waiting for the diagram to be rendered");
    cy.get(".diagram-canvas").should("exist");
    cy.get('[data-testid="diagnostics-view-tab"]').should("be.visible");

    cy.log("Accessing the diagnostics view");
    cy.get('[data-testid="diagnostics-view-tab"]')
      .click()
      .then(() => {
        cy.get('[data-testid="time-interval-loader"]').should("not.exist");
        cy.get('[data-testid="logs-loader"]').should("not.exist");
        cy.get('[data-testid="error-graph-loader"]').should("not.exist");
        cy.get('[data-testid="throughput-graph-loader"]').should("not.exist");
        cy.get('[data-testid="latency-graph-loader"]').should("not.exist");
        cy.get('[data-testid="cpu-graph-loader"]').should("not.exist");
        cy.get('[data-testid="memory-graph-loader"]').should("not.exist");

        cy.log("Asserting the Date/Time and Logs columns");
        for (let i = 0; i < numberOfBins; i++) {
          cy.contains(
            '[data-testid="time-interval-' + i + '"]',
            timestampRegex
          ).should("exist");
          cy.get('[data-testid="logs-partition-' + i + '"]').should("exist");
        }
        cy.get('[data-testid="time-interval-5"]').should("not.exist");
        cy.get('[data-testid="logs-partition-5"]').should("not.exist");
        cy.log("Verifying whether all the graphs are rendered");
        cy.get('[data-testid="error-graph"]', SHORT_TIME).should(
          "exist"
        );
        cy.get('[data-testid="throughput-graph"]', SHORT_TIME).should(
          "exist"
        );
        cy.get('[data-testid="latency-graph"]', SHORT_TIME).should(
          "exist"
        );
        cy.get('[data-testid="cpu-graph"]', SHORT_TIME).should("exist");
        cy.get('[data-testid="memory-graph"]', SHORT_TIME).should(
          "exist"
        );

        cy.log("Scroll the graph and check selector repositioning");
        cy.get('[data-testid="diagnostics-view-slider"]').should("be.visible");
        cy.get('[data-testid="rca-container"]').scrollTo("top");
        cy.get('[data-testid="diagnostics-view-slider"]').should("be.visible");
        cy.get('[data-testid="flame-graph-btn"]').should("be.visible");

        cy.log("Test Flame Graph view");
        cy.get('[data-testid="flame-graph-btn"]').click();
        cy.get('[data-testid="flame-graph-loader"]').should("not.exist");

        cy.get('[data-testid="flame-graph-message-container"]', {
          timeout: 60000,
        }).should("be.visible");

        cy.get('[data-testid="flame-graph"]').should("exist");
        cy.get('[data-testid="latencies-for-flame-graph"]').should("exist");
        cy.log(
          "Close the flame graph and navigate to the diagnostics view again"
        );
        cy.get('[data-testid="flame-graph-close-btn"]').should("be.visible");
        cy.get('[data-testid="flame-graph-close-btn"]')
          .click()
          .then(() => {
            cy.get('[data-testid="cpu-graph-loader"]').should("not.exist");
            cy.get('[data-testid="cpu-graph"]').should("exist");
          });
      });
  }
}
