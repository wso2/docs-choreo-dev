Cypress.Commands.add('devportalLogin', () => {
    cy.log('Logging into Devportal');
    cy.visit(Cypress.env('devportalLoginURL'));
    cy.wait(5000);
    cy.get('#usernameUserInput').type(Cypress.env('devportalIdpUsername'));
    cy.get('#password').type(Cypress.env('devportalIdpPassword'));
    cy.get('.right > .ui').click();
    cy.log('Successfully logged into Devportal');
}),

Cypress.Commands.add('navigateToOverviewInDevportal', () => {
    cy.log('Navigating to Overview');
    cy.wait(5000);
    cy.get('[data-testid="apis-appbar-btn"]').click();
    cy.get('[data-testid="apiCard-e2eTestApiDv1"]').click();
    cy.log('Successfully navigated to Overview');
})
