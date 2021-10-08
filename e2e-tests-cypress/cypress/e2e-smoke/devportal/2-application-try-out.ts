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

/// <reference types="cypress" />

import { generateAppName } from "../../support/common/utils";
import { getApiName } from "../../support/devportal/utils";
import { SwaggerComponent } from '../../support/console/common/swagger-ui-component';

describe('Application tryout scenario', () => {
    const appName = generateAppName('-e2etest');
    const apiName = getApiName();

    beforeEach(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    after(() => {
        cy.devportalLogout();
    });

    it('Create a consumer application and tryout an API', () => {
        cy.get('[data-testid="applications-appbar-btn"]').click();

        // Create application
        cy.wait(3000);
        cy.get('[data-testid="create-application-btn"]').click();
        cy.get('[data-testid="app-name"]').type(appName);
        cy.get('[data-testid="application-description"]').type('Application for e2e testing');
        cy.get('[data-testid="create-button"]').click({ force: true });

        cy.get('[data-testid="application-description"]').should('have.text', 'Application for e2e testing');
        cy.get('[data-testid="application-throttling-policy"]').should('have.text',
            '10PerMin (Allows 10 request per minute)');
        cy.get('[data-testid="application-token-type"]').should('have.text', 'JWT');
        cy.log('Application created successfully!');
        cy.wait(2000);

        // Create OAuth tokens
        cy.get('[data-testid="oauth-key"]').click();
        cy.wait(2000);
        cy.get('[data-testid="generate-token-btn"]').should('not.exist');
        cy.get('[data-testid="generate-oauth-key"]').click();
        cy.get('[data-testid="generate-token-btn"]').should('exist');
        cy.wait(2000);

        // Add an API to the application
        cy.get('[data-testid="subscriptions"]').click();
        cy.get('[data-testid="create-subscription-btn"]').click();
        cy.wait(2000);
        cy.log("Search API " + apiName + " to subscribe");
        cy.get('.MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input').type(apiName);
        cy.get('[data-testid="add-api-' + apiName + '"]').click();
        cy.get('[data-testid="subscription-dialog-close-btn"]').click();
        cy.log('Subscribed to the API successfully');

        cy.log("Check whether to re-subscribe to the API -  " + apiName + " ,that has already subscribed ");
        cy.get('[data-testid="create-subscription-btn"]').click();
        cy.wait(2000);
        cy.log("Search API " + apiName + " to subscribe");
        cy.get('.MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input').type(apiName);
        cy.wait(2000);
        cy.get('[index="0"] > :nth-child(3) > :nth-child(1)').contains('button', 'Add').should('be.disabled');
        cy.get('[data-testid="subscription-dialog-close-btn"]').click();

        // Tryout the added API
        cy.findByText(apiName).click();
        cy.wait(2000);
        cy.get('[data-testid="tryout-item-link"]').click();
        cy.wait(5000);
        cy.findByRole('button', { name: /​/i }).click();
        cy.findByRole('option', { name: appName }).click();
        cy.get('[data-testid="get-test-key-btn"]').should('not.be.disabled');
        cy.get('[data-testid="get-test-key-btn"]').click();
        cy.wait(4000);

        cy.log("Invoking the API");
        SwaggerComponent.SelectResource("GET","/v3/covid-19/states");
        SwaggerComponent.TryoutAPI();
        
        cy.get('tr[data-param-name="sort"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('todayCases').should('have.value', 'todayCases');
            });
        });
        cy.get('tr[data-param-name="yesterday"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('true').should('have.value', 'true');
            });
        });
        cy.get('tr[data-param-name="allowNull"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('0').should('have.value', '0');
            });
        });
        SwaggerComponent.ExecuteResourceFunction();
        SwaggerComponent.GetResponse();
    });

    it('Delete a consumer application', () => {
        cy.get('[data-testid="applications-appbar-btn"]').click();
        cy.findByText(appName).trigger('mouseover');
        cy.findByRole('button', { name: /delete/i });
        cy.findByRole('button', { name: /delete/i }).click();
        cy.get('[data-testid="delete-dialog-ok-button"]').click();
    });
});
