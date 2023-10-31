import { TestIds } from "../../constants/TestIds";

export class DevPortalLeftMenu {
  navigateToOverview() {
    cy.get('[data-testid="overview-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToSandboxCredentials() {
    cy.get('[data-testid="sandbox-credentials-menu-item"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToProductionCredentials() {
    cy.get('[data-testid="production-credentials-menu-item"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToTryOut() {
    cy.get('[data-testid="tryout-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToDocuments() {
    cy.get('[data-testid="documents-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToSDKs() {
    cy.get('[data-testid="sdks-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  navigateToContracts() {
    cy.get('[data-testid="contracts-item-link"]').click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }
}
