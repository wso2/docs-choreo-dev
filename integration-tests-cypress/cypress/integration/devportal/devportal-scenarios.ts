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

import { generateAppName } from "../../support/common/choreo-utils";

describe('Devportal', () => {
    const appName = generateAppName('-e2etest');
    const apiName = 'e2eTestApiDev';

    beforeEach(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    it('Adding and deleting comment for the API', () => {
        cy.get('button').contains('Add comment').click({ force: true });
        cy.log('Opened the comment box');
        cy.get('[name="newComment"]').type('Test comment from Cypress Test Runner');
        cy.get('[type="submit"]').contains('Add Comment').click({ force: true });
        cy.wait(2000);
        cy.get('[data-testid="txt-comments-count"]').should("have.text", "Comments (1)");
        cy.get('[data-testid=txt-no-comments]').should('not.exist');
        cy.log('Comment added successfully');

        cy.log('Deleting the comment');
        cy.get('table > tbody > tr:first').within(() => {
            cy.get('[data-testid="btn-delete-comment"]').click({ force: true });
        });
        cy.get('[class="MuiPopover-root"]').get('button').contains('Yes').click({ force: true });
        cy.get('[data-testid="txt-comments-count"]').should("have.text", "Comments (0)");
        cy.get('[data-testid=txt-no-comments]').should('exist');
        cy.log('Successfully deleted the comment');
    });

    it('Adding and modifying the ratings of the API', () => {
        cy.log('Opening the rating box');
        cy.get('[class="MuiGrid-root MuiGrid-item MuiGrid-grid-xs-12 MuiGrid-grid-sm-12 MuiGrid-grid-md-12 MuiGrid-grid-lg-4"]').within(() => {
            cy.get('button')
                .first()
                .click();
        })

        cy.log('Adding 4 star rating');
        cy.get('[for="hover-feedback-4"]').trigger('focus');
        cy.wait(3000);
        cy.get('[for="hover-feedback-4"]').click({ force: true });
        cy.wait(3000);
        cy.get('[class="MuiPopover-root"]').click({ force: true });
        cy.log('Added 4 star');

        cy.log('Changing 4 star rating to 3 star');
        cy.get('[for="hover-feedback-3"]').trigger('focus');
        cy.wait(3000)
        cy.get('[for="hover-feedback-3"]').click({ force: true });
        cy.wait(3000);
        cy.get('[class="MuiPopover-root"]').click({ force: true });
        cy.log('Changed the rate to 3 stars');
    })

    it('Generate credentials and tryout the API', () => {
        cy.log("Navigating to credentials tab");
        cy.wait(5000);
        cy.get('[data-testid="credentials-item-link"]').click();
        cy.url().should('include', '/credentials');
        cy.log("Successfully navigated to credentials tab");

        cy.log("Generating credentials")
        cy.get('.MuiSelect-root').click({ force: true });
        cy.get('.MuiList-root > [tabindex="0"]').click()
        cy.get('[data-testid="generate-creds-btn"]').click();
        cy.wait(6000);
        cy.get('[data-testid="generate-access-token-btn"]').should('exist');
        cy.log("Successfully generated credentials");

        cy.log("Navigating to Tryout tab");
        cy.get('[data-testid="tryout-item-link"]').click();
        cy.url().should('include', '/tryout');
        cy.log("Successfully navigated to tryout tab");

        cy.wait(4000);
        cy.log("Generating an access token");
        cy.get('[data-testid="get-test-key-btn"]').click();
        cy.get('[data-testid=accessTokenInput]').should('not.be.empty');
        cy.log("Successfully generated an access token");

        cy.wait(4000);
        cy.log("Invoking the API");
        cy.get('.opblock-summary').click();
        cy.get('.btn').click();
        cy.get('.execute-wrapper > .btn').click();
        cy.wait(4000);
        cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
        cy.log("Successfully invoked the API");
    });

    it('Remove generated credentials', () => {
        cy.log("Navigating to Credentials tab to remove credentials");
        cy.wait(5000);
        cy.get('[data-testid="credentials-item-link"]').click();
        cy.url().should('include', '/credentials');
        cy.get('[data-testid="remove-creds-btn"]').click();
        cy.get('[data-testid="remove-creds-confirmation-ok"]').click();
        cy.wait(4000);
        cy.get('[data-testid="keys-info-cell"]').should('have.text', 'Production Key and Secret is not  generated for this application');
        cy.log("Successfully removed credentials");
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

        // Create OAuth tokens and API Key
        cy.get('[data-testid="oauth-key"]').click();
        cy.wait(2000);
        cy.get('[data-testid="generate-token-btn"]').should('not.exist');
        cy.get('[data-testid="generate-oauth-key"]').click();
        cy.get('[data-testid="generate-token-btn"]').should('exist');
        cy.get('[data-testid="apikey"]').click();
        cy.get('[data-testid="generate-apikey-button"]').click();
        cy.get('[data-testid="apikey-dialog-generate-btn"]').click();
        cy.get('[data-testid="apikey-dialog-close-btn"]').click();
        cy.log('Keys generated successfully');
        cy.wait(2000);

        // Add an API to the application
        cy.get('[data-testid="subscriptions"]').click();
        cy.get('[data-testid="create-subscription-btn"]').click();
        cy.wait(2000);
        cy.get('[data-testid="add-api-' + apiName + '"]').click();
        cy.get('[data-testid="subscription-dialog-close-btn"]').click();
        cy.log('Subscribed to the API successfully');

        // Tryout the added API
        cy.findByText(apiName).click();
        cy.wait(2000);
        cy.get('[data-testid="tryout-item-link"]').click();
        cy.findByRole('button', { name: /​/i }).click();
        cy.findByRole('option', { name: appName }).click();
        cy.get('[data-testid="get-test-key-btn"]').should('not.be.disabled');
        cy.get('[data-testid="get-test-key-btn"]').click();
        cy.wait(4000);
        cy.log("Invoking the API");
        cy.get('.opblock-summary').click();
        cy.get('.btn').click();
        cy.get('.execute-wrapper > .btn').click();
        cy.wait(4000);
        cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
        cy.log('API Tryout was successful!');
    });

    it('Delete a consumer application', () => {
        cy.get('[data-testid="applications-appbar-btn"]').click();
        cy.findByText(appName).trigger('mouseover');
        cy.findByRole('button', { name: /delete/i });
        cy.findByRole('button', { name: /delete/i }).click();
        cy.get('[data-testid="delete-dialog-ok-button"]').click();
    });
});
