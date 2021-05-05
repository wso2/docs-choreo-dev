/// <reference types="cypress" />

describe('Devportal', () => {
    beforeEach(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal();
    })

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
});
