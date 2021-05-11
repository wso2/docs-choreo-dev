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

Cypress.Commands.add('devportalLogin', () => {
    cy.log('Logging into Devportal');
    cy.visit(Cypress.env('devportalLoginURL'));
    cy.wait(5000);
    cy.get('#usernameUserInput').type(Cypress.env('devportalIdpUsername'));
    cy.get('#password').type(Cypress.env('devportalIdpPassword'));
    cy.get('.right > .ui').click();
    cy.log('Successfully logged into Devportal');
}),

Cypress.Commands.add('navigateToOverviewInDevportal', (apiName: string) => {
    cy.log('Navigating to Overview');
    cy.wait(5000);
    cy.get('[data-testid="apis-appbar-btn"]').click();
    cy.get('[data-testid="apiCard-' + apiName + '"]').click();
    cy.log('Successfully navigated to Overview');
})
