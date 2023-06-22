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
import { Enums } from "../../../commons/enums";
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import {
  DEV_PORTAL_APP_KEY_GEN_URL,
  DEV_PORTAL_SUBSCRIPTIONS_URL,
} from "../../../commons/urls";

export class ApiCredentials {
  static navigateCredentialsTab() {
    cy.get('[data-testid="credentials-item-link"]').click();
    cy.url().should("include", "/credentials");
    cy.log("Successfully navigated to credentials tab");
  }

  static generateCredentials(env: Enums.Environment) {
    cy.log("Generating credentials");

    cy.intercept({
      method: "POST",
      url: DEV_PORTAL_APP_KEY_GEN_URL,
      times: 1,
    }).as("generateAppKey");
    cy.get(
      `[data-testid="${env.toLowerCase()}-credentials-menu-item"]`
    ).click();
    cyGet('[data-testid="generate-creds-btn"]').click();
    cy.get('[data-testid="remove-creds-btn"]').should("be.visible");
    cy.get("#copy-textfield").invoke("val").should("not.be.empty");
    cy.wait("@generateAppKey", VERY_SHORT_TIME).then(() => {
      cy.get('[data-testid="generate-access-token-btn"]').should("exist");
      cy.log("Successfully generated credentials");
    });
  }

  static navigateToEnvironment(env: Enums.Environment) {
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.log("Navigating to environment: " + env);
    cy.log("DEV_PORTAL_SUBSCRIPTIONS_URL: " + DEV_PORTAL_SUBSCRIPTIONS_URL);
    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_SUBSCRIPTIONS_URL,
      times: 1,
    }).as("navigate");
    //Had to add this due to page loading delay
    cy.wait(VERY_SHORT_TIME.timeout);
    cy.get(
      `[data-testid="${env.toLowerCase()}-credentials-menu-item"]`
    ).click();
    cy.wait("@navigate", SHORT_TIME).then(() => {
      cy.get('[data-testid="credentials-item-link"]')
        .should("be.visible")
        .click();
      cy.log("Successfully navigated to credentials tab");
    });
  }
}
