export class ComponentListingPage {
  static deleteComponent(componentName: string) {
    cy.get("tr p").contains(componentName).should('be.visible').realHover();
    cy.get("button>span").contains("Delete").click();
    cy.get('[data-testid="confirm-name"]>div>input')
      .should("be.visible")
      .type(componentName);
    cy.get(".MuiDialogActions-spacing button").eq(1).should("be.enabled").click();
    this.verifyDeletion();
  }

  private static verifyDeletion() {
    cy.intercept("POST", `${Cypress.env("appSvcURL")}/graphql`).as("delete");
    cy.wait("@delete",{timeout:180000}).then((i) => {
      const { status, canDelete } = i.response.body.data["deleteComponentV2"];
      expect(status).equal("success");
      expect(canDelete).to.true;
    });
  }
}
