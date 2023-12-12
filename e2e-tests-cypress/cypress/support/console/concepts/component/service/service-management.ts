/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";
import { Enums } from "../../../../commons/enums";
import { ServiceUtils } from "./service-utils";

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
    ServiceUtils.validateDeploymentTrack(component);

    cy.get(TestIds.publishLifecycle).click();
    cy.get(TestIds.blockLifecycle).should("be.visible");
    cy.get(TestIds.prereleaseLifecycle).should("be.visible");
    cy.get(TestIds.demoteLifecycle).should("be.visible");
    cy.get(TestIds.deprecateLifecycle).should("be.visible");
  }

  private saveUsagePlans(component: Service, plans: UsagePlan[]) {
    ServiceUtils.validateDeploymentTrack(component);

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
    ServiceUtils.validateDeploymentTrack(component);

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
