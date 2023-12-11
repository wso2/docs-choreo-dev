import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";
import { Enums } from "../../../../commons/enums";

export enum UsagePlan {
  Gold = "Gold",
  Silver = "Silver",
  Bronze = "Bronze",
  Unlimited = "Unlimited",
}

export class _ServiceManagement {
  private sideMenu = new ServiceLeftMenu();

  changeLifeCycleState(component: Service, state: Enums.LifeCycleState) {
    this.sideMenu.navigateToLifecycle();

    switch (state) {
      case Enums.LifeCycleState.Publish:
        this.publish(component);
        break;
      default:
        expect.fail(`Unhandled lifecycle state: ${state}`);
    }
  }

  updateUsagePlans(component: Service, plans: UsagePlan[]) {
    this.sideMenu.navigateToUsagePlan();
    this.saveUsagePlans(component, plans);
  }

  enableCors(component: Service) {
    this.sideMenu.navigateToSettings();
    this.toggleCors(component, true);
  }

  private publish(component: Service) {
    const version = component.getLatestVersion();
    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    cy.get(TestIds.publishLifecycle).click();
    cy.get(TestIds.blockLifecycle).should("be.visible");
    cy.get(TestIds.prereleaseLifecycle).should("be.visible");
    cy.get(TestIds.demoteLifecycle).should("be.visible");
    cy.get(TestIds.deprecateLifecycle).should("be.visible");
  }

  private saveUsagePlans(component: Service, plans: UsagePlan[]) {
    const version = component.getLatestVersion();
    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    const unlimitedPlan = `[data-testid="checkbox-${UsagePlan.Unlimited}"]`;
    cy.get(`${unlimitedPlan}`).click();
    plans.forEach((plan) => {
      cy.get(`[data-testid="checkbox-${plan}"]`).click();
    });
    cy.get(TestIds.usagePlanSave).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(unlimitedPlan).within(() => {
      cy.get("input").should("not.be.checked");
    });
    plans.forEach((plan) => {
      cy.get(`[data-testid="checkbox-${plan}"]`).within(() => {
        cy.get("input").should("be.checked");
      });
    });
  }

  private toggleCors(component: Service, isEnabled: boolean) {
    const version = component.getLatestVersion();
    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    cy.get(TestIds.editSettings).click();
    cy.get(TestIds.corsConfig).click();

    cy.get(TestIds.saveSettings).should("be.visible");
    cy.get(TestIds.corsConfig).within(() => {
      if (isEnabled) {
        cy.get("input").should("be.checked");
      } else {
        cy.get("input").should("not.be.checked");
      }
    });

    this.applySettingChanges();
  }

  private applySettingChanges() {
    cy.get(TestIds.saveSettings).click();
    cy.get(TestIds.apply).should("be.visible").click();
    cy.get(TestIds.apply).should("not.exist");
    cy.get(TestIds.componentLoader).should("not.exist");
    cy.get(TestIds.backdropLoader).should("not.exist");
    // Wait short time for setting changes to be applied.
    // UI seems to work in a slightly async manner giving a misleading indication that the action has completed
    cy.get(TestIds.editSettings).should("be.enabled").wait(3000);
  }
}
