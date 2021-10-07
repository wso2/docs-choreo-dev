export class Deploy{




static navigateDeploy(){
    cy.get('#deploy').click()
    cy.get('[data-testid="backdrop-loader"]').should('not.exist');
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
}



static deploy(){
    cy.get('#deploy-button').click()
    cy.get('#stop-button', { timeout: 1000 * 60 * 5 }).should('exist');
}

static undeloy(){
    cy.get('#stop-button').click()
    cy.get('#deploy-button', { timeout: 1000 * 60 * 5 }).should('exist');
}




}