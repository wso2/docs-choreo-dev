export class ComponentObservePage {
    static gotoOverview() {
        cy.get('[data-testid="panel-Overview-btn"]').should('be.visible').click();
    }

    static gotoLogs() {
        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible').click();
      }


  }
  