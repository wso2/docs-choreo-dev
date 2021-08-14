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

import {
    LONG_TIME_OUT, MEDIUM_TIME_OUT, STANDARD_TIME_OUT, DEPLOYMENT_TIME_OUT, DEVELOP,
    OVERVIEW, PATH_SEPARATOR
} from "../../common/constants";

Cypress.Commands.add('updateSubscriptionPlans', () => {
    cy.get('[data-testid="Subscriptions"]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid="checkbox-Bronze"]').click();
    cy.get('[data-testid="checkbox-Gold"]').click();
    cy.get('[data-testid="subscription-save-btn"]').click();
    cy.get('[id="circular-loader"]').should('not.exist');
    cy.log('Subscriptions updated successfully');
});

Cypress.Commands.add('updateEndpointConfiguration', (newEndpoint: string) => {
    cy.get('[data-testid="Endpoint Configuration"]').click();
    cy.get('[data-testid="api-endpoint"]').within(() => {
        cy.get('input').clear().type(newEndpoint);
    });
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
    cy.get('[data-testid="test"]', { timeout: STANDARD_TIME_OUT }).click();
    cy.log("Generating an access token");
    cy.get('[data-testid="get-test-key-btn"]').click();
    cy.get('[data-testid=accessTokenInput]').should('not.be.empty');
    cy.log("Successfully generated an access token");

    cy.log("Invoking the API");
    cy.wait(2000);
    cy.get('.opblock-summary', { timeout: STANDARD_TIME_OUT }).eq(0).click();
    cy.get('.btn').click();
    cy.get('.execute-wrapper > .btn', { timeout: STANDARD_TIME_OUT }).click();
    cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status',
        { timeout: LONG_TIME_OUT })
        .should('have.text', '200');
    cy.log('Invoked the API successfully');
});

Cypress.Commands.add('deleteApiFromOverview', () => {
    cy.get('[data-testid="develop"]').click();
    cy.get('[data-testid="Overview"]').click();
    cy.get('[data-testid="delete-api-btn"]').click({force: true });
    cy.get('[data-testid="delete-api"]').click();
    cy.url().should('not.include', DEVELOP + OVERVIEW + PATH_SEPARATOR);
    cy.log('API deleted successfully');
});

Cypress.Commands.add('searchApiFromListAndVisit', (apiName: string) => {
    cy.log('Searching the api from the table');
    cy.get('[data-testid="apis-list-table-loader"]', { timeout: MEDIUM_TIME_OUT }).should('not.exist');
    cy.get('[data-testid=api-search-btn]', { timeout: MEDIUM_TIME_OUT }).click();
    cy.get('[data-testid=api-search-text-field]').type(apiName);
    cy.wait(3000);
    cy.get('[data-testid="apis-list-table-loader"]').should('not.exist');
    cy.get('[data-testid=apis-list-table]').within(() => {
        cy.contains(apiName).click();
    });
});

Cypress.Commands.add('verifyApiOverview', (apiName: string, apiVersion: string) => {
    cy.log('Visiting API overview page');
    cy.get('[data-testid=overview-title]', { timeout: STANDARD_TIME_OUT }).should('exist');
    cy.get('[data-testid=go-to-dev-portal-btn]').should('not.be.enabled');
    cy.get('[data-testid=api-name-nav-label]').should('have.text', apiName);
    cy.get('[data-testid=api-version-nav-label]').should('have.text', '(' + apiVersion + ')');
    cy.get('[data-testid=resources-container]').should('be.visible', true);
});

Cypress.Commands.add('updateDesignConfiguration', () => {
    cy.log('Visiting design configuration and update data');
    cy.get('[data-testid="Design Configurations"]').click();
    cy.get('[data-testid=toggle-edit-description]').click();
    cy.get('[data-testid=description-input]').type('Description by automated test runner');
    cy.get('[data-testid=toggle-add-tags]').click();
    cy.get('[data-testid=tag-input]').type('testTag{enter}');
    cy.get('[data-testid=design-config-save-btn]').click();
    cy.get('#circular-loader', { timeout: STANDARD_TIME_OUT }).should('not.exist');
});


Cypress.Commands.add('updateRuntimeConfiguration', () => {
    cy.get('[data-testid="Runtime Configurations"]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid=switch-cors-config]').click();
    cy.wait(2000);
    cy.get('[data-testid=cors-config-label]').click();
    cy.wait(2000);
    cy.get('[data-testid=checkbox-allow-all-origins]').click();
    cy.wait(2000);
    cy.get('[data-testid=addBtn-origin]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid="type and press enter to add origins"]').type('localhost{enter}');
    cy.get('[data-testid=addBtn-header]').click();
    cy.get('[data-testid="type and press enter to add headers"]').type('tenantId{enter}');
    cy.get('[data-testid=addBtn-method]').click();
    cy.get('[data-testid=access-control-method-select]').click();
    cy.get('[data-testid=access-c-method-TRACE]').click();
    cy.get('[data-testid=access-control-method-select]').click();
    cy.get('[data-testid=access-c-method-CONNECT]').click();
    cy.get('[data-testid=runtime-config-save-btn]').click();
    cy.get('[id="circular-loader"]', { timeout: 1000 * 30 }).should('not.exist');
});

Cypress.Commands.add('addApiDocument', () => {
    cy.log('Visiting Documents page');
    cy.get('[data-testid=Documents]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid=add-new-doc]').click();
    cy.get('[data-testid=page-header]').contains('Add New Document').should('be.visible', true);
    cy.get('[data-testid=document-name]').type('Test document by test runner');
    cy.get('[data-testid=document-summary]').type('Test summary by test runner');
    cy.get('[data-testid=document-url]').type('https://www.example.com/how-to');
    cy.get('[data-testid=create-document]').click();
    cy.get('#circular-loader', { timeout: STANDARD_TIME_OUT }).should('not.exist');
    cy.get('[data-testid=document-name]').should('have.value', '');
    cy.get('[data-testid=document-summary]').should('have.value', '');
    cy.get('[data-testid=document-url').should('have.value', '');
});

Cypress.Commands.add('deployInitialRevision', () => {
    cy.log('Visiting deployment tab');
    cy.get('[data-testid=deployments]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid=create-deploy-revision-btn]').click();
    cy.get('.MuiDialogContent-root').within(() => {
        cy.get('button').contains('Deploy').click();
    });
    cy.get('.MuiDialogContent-root', { timeout: STANDARD_TIME_OUT }).should('not.exist');
    cy.get('[data-testid=deployment-loader]', { timeout: DEPLOYMENT_TIME_OUT }).should('not.exist');
});

Cypress.Commands.add('publishApi', () => {
    cy.log('Visiting api publishing');
    cy.get('[data-testid=lifecycle-management]', { timeout: STANDARD_TIME_OUT }).click();
    cy.get('[data-testid=Publish-lc-btn]').click();
});

Cypress.Commands.add('checkApiListAvailabilityAndVisitCreate', () => {
    cy.get('[data-testid="apis-list-table-loader"]').should('not.exist');
    cy.get('#container').then((container) => {
        if (container.find('[data-testid="create-api-btn"]').length > 0)  {
            cy.log("API list available");
            cy.get('[data-testid="create-api-btn"]', { timeout: MEDIUM_TIME_OUT }).click();
        }
    });
});
