/// <reference types="cypress" />

describe('login-logout flow', () => {

    it('login-logout flow with gmail', () => {
        cy.log("Logging into Choreo using google");
        cy.userLoginWithGmail();
        cy.get('.user-profile-name').should('exist');

        cy.userLogout();
    })
})
