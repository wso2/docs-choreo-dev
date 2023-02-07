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
import { MEDIUM_TIME_OUT, STANDARD_TIME_OUT } from "../../constants";

export class ApiCredentials {
  static navigateCredentialsTab() {
    cy.get('[data-testid="credentials-item-link"]').click();
    cy.url().should("include", "/credentials");
    cy.log("Successfully navigated to credentials tab");
    cy.wait(3000);
  }

  static generateCredentials() {
    cy.log("Generating credentials");
    cy.get('[data-testid="generate-creds-btn"]').click();
    cy.wait(3000);
    cy.get('[data-testid="generate-access-token-btn"]').should("exist");
    cy.log("Successfully generated credentials");
  }

  static removeCredentials() {
    cy.log("Removing credentials");
    cy.get('[data-testid="remove-creds-btn"]').click();
    cy.get('[data-testid="remove-creds-confirmation-ok"]').click();
    cy.wait(3000);
    cy.get('[data-testid="generate-access-token-btn"]').should("not.exist");
    cy.log("Successfully removed credentials");
  }
}
