/// <reference types="cypress" />

describe('Home page component visibility', () => {
    before(() => {
        cy.log("Login into Choreo");
        cy.consoleUserLogin();
    })

    after(() => {
        cy.userLogout();
    })

    it('Check components in home page', () => {
        cy.log('Starting checks on navigation bar elements');
        cy.get('[href="/"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'Home');
        cy.get('[href="/"]').trigger('mouseout');

        cy.get('[href="/marketplace"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'Marketplace');
        cy.get('[href="/marketplace"]').trigger('mouseout');

        cy.get('[href="/integrations"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'Integrations');
        cy.get('[href="/integrations"]').trigger('mouseout');

        cy.get('[href="/services"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'Services');
        cy.get('[href="/services"]').trigger('mouseout');

        cy.get('[href="/apis/"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'APIs');
        cy.get('[href="/apis/"]').trigger('mouseout');

        cy.get('[href="/devops"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'DevOps');
        cy.get('[href="/devops"]').trigger('mouseout');

        cy.get('[href="/user-settings/organization/members"]').trigger('mouseover');
        cy.get('.MuiTooltip-popper').should('contain.text', 'Settings');
        cy.get('[href="/user-settings/organization/members"]').trigger('mouseout');
        cy.log('Navigation bar check completed successfully');
        
        cy.log('Starting checks on UI containers');
        cy.contains('.MuiTypography-h4', 'Integrations').siblings('.button-section').should('have.text', 'Explore');
        cy.contains('.MuiTypography-h4', 'Services').siblings('.button-section').should('have.text', 'Explore');
        cy.contains('.MuiTypography-h4', 'APIs').siblings('.button-section').should('have.text', 'Get Started');
        cy.log('UI containers check completed successfully');

        cy.log('Starting checks on expanded navigation bar elements');
        cy.get('[aria-label="clipped drawer"]').click({force: true});
        cy.contains('p', 'Home').should('be.visible', 'Home icon is visible in Nav bar');
        cy.contains('p', 'Marketplace').should('be.visible');
        cy.contains('p', 'Integrations').should('be.visible');
        cy.contains('p', 'Services').should('be.visible');
        cy.contains('p', 'APIs').should('be.visible');
        cy.contains('p', 'DevOps').should('be.visible');
        cy.contains('p', 'Settings').should('be.visible');
        cy.log('Expanded navigation bar check completed successfully');

        cy.log('Starting checks on Help menu');
        cy.contains('Help').click();
        cy.contains('Help Center').should('be.visible');
        cy.contains('Learn Choreo').should('be.visible');
        cy.log('Help menu check completed successfully');

        cy.log('Starting checks on current user menu');
        cy.get('[id="current-user"]').click({force: true});
        cy.contains('Logout').should('be.visible');
        cy.log('Current user menu check completed successfully');
    })
})
