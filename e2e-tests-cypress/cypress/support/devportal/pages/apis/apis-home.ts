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
import { STANDARD_TIME_OUT } from "../../constants";

export class Apis {
  static navigateToApiOverview(apiName: string): void {
    cy.log("Navigating to Overview");
    cy.get('[data-testid="apis-appbar-btn"]', { timeout: STANDARD_TIME_OUT })
      .should("be.visible")
      .click();
    cy.log("Searching the API");
    cy.get("#outlined-search-bar-api-listing").clear();
    cy.get("#outlined-search-bar-api-listing", {
      timeout: STANDARD_TIME_OUT,
    }).type(apiName + "{enter}");
    cy.get('[data-testid="apiCard-' + apiName + '"]').first()
      .should("be.visible")
      .click();
    cy.log("Successfully navigated to Overview");
  }
  
  static searchApiAndSelect() {
  
    cy.get('[data-testid=txt-api-name]').invoke('text').then((textApiName) => {
      cy.get('[data-testid=apis-appbar-btn]').click();
      cy.get('[data-testid*="apiCard"]').should('be.visible')
      cy.get('#outlined-search-bar-api-listing').type(textApiName);
      cy.get('button').contains('Search').click();
      cy.get('[data-testid=apiCard-' + textApiName + ']').should('have.length', 2);
      cy.get('[data-testid=apiCard-' + textApiName + ']').last().click();
    });
  }
}
