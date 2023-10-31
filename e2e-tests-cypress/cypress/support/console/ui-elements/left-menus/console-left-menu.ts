import { LeftMenu } from "./left-menu";

export class ConsoleLeftMenu extends LeftMenu {
  navigateToComponents() {
    this.navigateToMenuItem('[data-cyid="listing"]');
  }

  navigateToComponentUsageInsights() {
    this.navigateToMenuItem('[data-cyid="usage-insights"]');
    cy.contains("Coming Soon").should("be.visible");
  }

  navigateToProjectUsageInsights() {
    this.navigateToMenuItem('[data-cyid="project-usage-insights-button"]');
  }
}
