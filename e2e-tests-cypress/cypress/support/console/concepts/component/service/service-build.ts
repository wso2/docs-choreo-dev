import { BUILD_SUCCESS } from "../../../../commons/constants";
import { LONG_TIME } from "../../../../commons/timeouts";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";

export class _ServiceBuild {
  private sideMenu = new ServiceLeftMenu();

  build(component: Service) {
    this.sideMenu.navigateToBuild();

    this.triggerBuild(component);
  }

  private triggerBuild(component: Service) {
    const version = component.getLatestVersion();
    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    cy.get(TestIds.build).should("be.enabled").click();
    cy.get(TestIds.next).should("be.visible").click();
    cy.get(TestIds.tableTitle).within(() => {
      cy.contains(BUILD_SUCCESS, LONG_TIME);
    });
  }
}
