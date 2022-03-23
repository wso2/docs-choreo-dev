export class GreetingSample {
  static selectSample() {
    cy.get(".choreo-sample-list .MuiGrid-item h3")
      .should("have.length.greaterThan", 2)
         cy.get(".choreo-sample-list .MuiPaper-elevation1").eq(0).realHover()
      cy.wait(2000)
      cy.get(".choreo-sample-list .MuiPaper-elevation1").eq(0).realClick()
  }
}
