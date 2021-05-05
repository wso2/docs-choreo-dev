/// <reference types="cypress" />

import { generateAppName } from "../../support/common/choreo-utils";

describe('Devportal', () => {
    const appName = generateAppName('-e2etest');
    const apiName = 'e2eTestApiDv';

    beforeEach(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal();
    });

    it('Credentials & Tryout', () => {
        cy.log("Navigating to credentials tab");
        cy.wait(5000);
        cy.get('[data-testid="credentials-item-link"]').click();
        cy.url().should('include', '/credentials');
        cy.log("Successfully navigated to credentials tab");

        cy.log("Generating credentials")
        cy.get('.MuiSelect-root').click({force: true});
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

        cy.log("Navigating to Credentials tab to remove credentials");
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
        cy.get('[data-testid="create-application-btn"]').click();
        cy.get('[data-testid="app-name"]').type(appName);
        cy.get('[data-testid="application-description"]').type('Application for e2e testing');
        cy.get('[data-testid="create-button"]').click({ force : true });

        cy.get('[data-testid="application-description"]').should('have.text', 'Application for e2e testing');
        cy.get('[data-testid="application-throttling-policy"]').should('have.text',
            '10PerMin (Allows 10 request per minute)');
        cy.get('[data-testid="application-token-type"]').should('have.text', 'JWT');
        cy.log('Application created successfully!');

        // Create OAuth tokens and API Key
        cy.get('[data-testid="oauth-key"]').click();
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
        cy.get('[class="opblock-summary opblock-summary-get"]').click();
        cy.get('.btn').click();
        cy.get('.execute').click();
        cy.get('[class="response-col_status"]').should('have.text', 200);
        cy.log('API Tryout was successful!');
    });

    it('Delete a consumer application', () => {
        cy.get('[data-testid="applications-appbar-btn"]').click();
        cy.findByText(appName).trigger('mouseover');
        cy.findByRole('button', {  name: /delete/i});
        cy.findByRole('button', {  name: /delete/i}).click();
        cy.get('[data-testid="delete-dialog-ok-button"]').click();
    });
});
