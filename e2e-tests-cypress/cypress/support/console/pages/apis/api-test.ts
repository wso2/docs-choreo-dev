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

export class APITest {
  static testAPI() {
    cy.get('[data-cyid="link-test"]').should("be.visible").click();
    cy.get('[id="backdrop-loader"]').should("not.exist");
  }

  static selectDevEnvironment() {
    cy.get("[data-testid=env]").click();
    cy.get("[data-cyid=undefined-Development]").click({ force: true });
    cy.get('[data-cyid="text-field-endpoint"]').within(() => {
      cy.get("input")
        .invoke("attr", "value")
        .then((val) => {
          Cypress.env(`${Enums.Environment.DEVELOPMENT}_test_url`, val);
        });
    });
  }

  static selectProdEnvironment() {
    cy.get("[data-testid=env]").click();
    cy.get("[data-cyid=undefined-Production]").click({ force: true });
    cy.get('[data-cyid="text-field-endpoint"]').within(() =>
      cy
        .get("input")
        .invoke("attr", "value")
        .then((val) => {
          Cypress.env(`${Enums.Environment.PRODUCTION}_test_url`, val);
        })
    );
  }

  static selectEnvironment(env: Enums.Environment) {
    cy.get("[data-testid=env]").click();
    cy.get(`[data-cyid=undefined-${env}]`).click({ force: true });
    cy.get('[data-cyid="text-field-endpoint"]').within(() => {
      cy.get("input")
        .invoke("attr", "value")
        .then((val) => {
          Cypress.env(`${env}_test_url`, val);
        });
    });
  }
}
