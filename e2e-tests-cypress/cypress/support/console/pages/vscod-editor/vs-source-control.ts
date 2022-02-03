export class VSSourceControl {
  static commitChanges(commitMessage: string) {
    cy.contains('Changes').click({ force: true });
    cy.get('[title="Stage All Changes"]').should('be.visible').click();
    cy.get('[aria-label="Changes"] div[class="count"] div')
      .invoke('text')
      .should('eq', '0');
    cy.get('div[class="view-line"]')
      .should('be.visible')
      .eq(0)
      .click()
      .type(`{backspace}{backspace}${commitMessage}`);
    cy.get('[title="Commit"]').should('be.visible').eq(0).click();
    cy.get('.resource-group').should('have.length', 1);
  }
}
