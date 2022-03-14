export class VSSourceControl {
  static commitChanges(commitMessage: string) {
    cy.wait(2000)
    cy.get('[aria-label="Changes"] .resource-group').click()
    
    cy.get('[title="Stage All Changes"]').should('be.visible').click();
    cy.get('[aria-label="Changes"] div[class="count"] div')
      .invoke('text')
      .should('eq', '0');
    cy.get('div[class="view-line"]')
      .should('be.visible')
      .eq(0)
      .click()
      .type(`{backspace}{backspace}${commitMessage}`);
      cy.wait(40000)// wait till git process complete.
    cy.get('[title="Commit"]').should('be.visible').eq(0).click();
    cy.get('[aria-label="Staged Changes"]',{timeout:120000}).should('not.exist');
  }
}
