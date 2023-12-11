import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "./proxy-component";
import { Enums } from "../../../../commons/enums";
import { TestIds } from "../../../constants/TestIds";
import { ProxyUtils } from "./proxy-utils";
import { VERY_SHORT_TIME } from "../../../../commons/timeouts";

export class _ProxyManagement {
  private sideMenu = new ProxyLeftMenu();

  changeLifeCycleState(component: Proxy, state: Enums.LifeCycleState) {
    this.sideMenu.navigateToLifecycle();

    switch (state) {
      case Enums.LifeCycleState.Publish:
        this.publish(component);
        break;
      default:
        expect.fail(`Unhandled lifecycle state: ${state}`);
    }
  }

  private publish(component: Proxy) {
    ProxyUtils.validateDeploymentTrack(component);

    cy.get(TestIds.publishLifecycle).click();

    cy.get(TestIds.dialogPrimaryAction)
      .should("be.enabled", VERY_SHORT_TIME)
      .click();
    cy.get(TestIds.dialogPrimaryAction).should("not.exist");

    cy.get(TestIds.publishBtn, VERY_SHORT_TIME).should("be.visible");

    cy.get(TestIds.blockLifecycle).should("be.visible");
    cy.get(TestIds.prereleaseLifecycle).should("be.visible");
    cy.get(TestIds.demoteLifecycle).should("be.visible");
    cy.get(TestIds.deprecateLifecycle).should("be.visible");
  }
}
