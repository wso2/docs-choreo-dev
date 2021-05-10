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

import { DEVELOP, OVERVIEW, PATH_SEPARATOR } from "./constants";

Cypress.Commands.add('updateSubscriptionPlans', () => {
    cy.get('[data-testid="Subscriptions"]').click();
    cy.get('[data-testid="checkbox-Bronze"]').click();
    cy.get('[data-testid="checkbox-Gold"]').click();
    cy.get('[data-testid="subscription-save-btn"]').click();
    cy.get('[id="circular-loader"]').should('not.exist');
    cy.get('[data-testid="Overview"]').click();
    cy.get('[data-testid="overview-item-Business Plans"]').should('have.text', 'Bronze, Gold, Unlimited');
    cy.log('Subscriptions updated successfully');
});

Cypress.Commands.add('updateEndpointConfiguration', (newEndpoint: string) => {
    cy.get('[data-testid="Endpoint Configuration"]').click();
    cy.get('[data-testid="api-endpoint"]').type(newEndpoint);
    cy.get('[data-testid="endpoint-config-save-btn"]').click();
    cy.get('[id="circular-loader"]').should('not.exist');
    cy.get('[data-testid="api-endpoint"]').within(() => {
        cy.findByRole('textbox').should('have.value', newEndpoint);
    });
    cy.wait(1000);
    cy.log('Endpoint configuration updated successfully');
});

Cypress.Commands.add('createAndDeployRevision', () => {
    cy.get('[data-testid="deployments"]').click();
    cy.get('[id="revision-point-1"]').should('exist');
    cy.get('[id="revision-point-2"]').should('not.exist');
    cy.get('[data-testid="create-deploy-revision-btn"]').click();
    cy.get('[data-testid="create-deploy-revision-dialog-btn"]').click();
    cy.get('[id="revision-point-2"]').should('exist');
    cy.log('Revision created and deployed successfully');
});

Cypress.Commands.add('testApiInPublisherTestConsole', () => {
    cy.get('[data-testid="test"]').click();
    cy.wait(3000);
    cy.log("Invoking the API");
    cy.get('.opblock-summary').eq(0).click();
    cy.get('.btn').click();
    cy.get('.execute-wrapper > .btn').click();
    cy.wait(2000);
    cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
    cy.log('Invoked the API successfully');
});

Cypress.Commands.add('deleteApiFromOverview', () => {
    cy.get('[data-testid="develop"]').click();
    cy.get('[data-testid="Overview"]').click();
    cy.get('[data-testid="delete-api-btn"]').click();
    cy.get('[data-testid="delete-api"]').click();
    cy.url().should('not.include', DEVELOP + OVERVIEW + PATH_SEPARATOR);
    cy.log('API deleted successfully');
});
