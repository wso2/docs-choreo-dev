import { MEDIUM_TIME } from "../../../../commons/timeouts";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";

export class _ServiceStats {
  private sideMenu = new ServiceLeftMenu();

  viewUsageInsights() {
    this.sideMenu.navigateToUsageInsights();
    this.verifyUsageInsights();
  }

  navigateFromComponentToProjectInsights() {
    this.sideMenu.navigateToUsageInsights();
    this.navigateToProjectInsights();
  }

  private verifyUsageInsights() {
    cy.contains("Coming Soon").should("be.visible");
  }

  private navigateToProjectInsights() {
    cy.get(TestIds.projectInsights).should("be.visible").click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }
}
