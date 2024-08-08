/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

/// <reference types="cypress" />

import "cypress-real-events/support";
import "cypress-fail-fast";
import '@cypress/code-coverage/support';
import { MIN_RENDERING_WAIT_TIME } from "./commons/constants";
require('@neuralegion/cypress-har-generator/commands');

/**
 * Prevent Cypress from failing the test when an uncaught exception is thrown by Choreo.
 */
Cypress.on("uncaught:exception", (err, runnable) => {
  // returning false here prevents Cypress from
  // failing the test
  return false;
});

/**
 * We encounter cases where certain DOM elements being interacted with are re-rendered
 * without warning leading to them no longer being actionable. Cypress is mislead into
 * interacting with these elements while they are still unstable. This command is
 * a workaround to handle such cases. It will function similar to cy.get() but will take
 * additional steps to ensure the element is actionable before further Cypress commands
 * are chained off of it.
 * @param {string} selector - The DOM element selector
 * @param {RenderingOptions} options - The options object
 * @returns {Chainable<Element>} - The Cypress chainable object
 *
 * @example cy.getUnstable('[data-cyid="btn-create-new"]')
 */
Cypress.Commands.add(
  "getUnstable",
  (
    selector,
    options = {
      waitTime: MIN_RENDERING_WAIT_TIME,
      timeout: Cypress.config("defaultCommandTimeout"),
    }
  ) => {
    // Setting a wait time lower than MIN_RENDERING_WAIT_TIME can cause flaky tests
    if (options.waitTime < MIN_RENDERING_WAIT_TIME) {
      options.waitTime = MIN_RENDERING_WAIT_TIME;
    }

    cy.get(selector, { timeout: options.timeout })
      .should("exist")
      .then(() => {
        cy.log(
          `Waiting ${options.waitTime}ms for element ${selector} to re-render`
        );
        cy.wait(options.waitTime, { log: false });

        return cy.get(selector, { timeout: options.timeout });
      });
  }
);

/**
 * Get the data from a table cell
 * @param {string} selector - The table selector
 * @param {number} rowIndex - The row index
 * @param {number} dataIndex - The data index
 * @param {boolean} log - Whether to log the data
 * @returns {Chainable<Element>} - The Cypress chainable object
 *
 * @example cy.getTableData('[data-cyid="table"]', 0, 0)
 */

Cypress.Commands.add("getTableData", (selector, rowIndex, dataIndex, log) => {
  const isLogEnabled = log === undefined ? true : log;

  return cy
    .get(selector, { log: isLogEnabled })
    .find("tbody", { log: isLogEnabled })
    .find("tr", { log: isLogEnabled })
    .eq(rowIndex, { log: isLogEnabled })
    .find("td", { log: isLogEnabled })
    .eq(dataIndex, { log: isLogEnabled })
    .invoke({ log: isLogEnabled }, "text");
});
