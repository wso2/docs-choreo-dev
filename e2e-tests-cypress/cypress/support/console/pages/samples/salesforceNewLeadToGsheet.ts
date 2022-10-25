/// <reference types="cypress-xpath" />
import "cypress-xpath";

export class SalesforceNewLeadToGsheet {
  static selectSample() {
    cy.get(".choreo-sample-list .MuiGrid-item h3").should(
      "have.length.greaterThan",
      2
    );
    cy.get(".choreo-sample-list .MuiPaper-elevation1").eq(0).realHover();
    cy.wait(2000);
    cy.get(
      '[type="button"]'
    ).contains("View all").realClick();
  }

  static searchSample() {
    cy.wait(2000);
    cy.get('[data-cyid="sfdc_lead_to_gsheet_row"]').realHover();
    cy.wait(2000);
    cy.get('[data-cyid="sfdc_lead_to_gsheet_row"]').realClick();
  }
}
