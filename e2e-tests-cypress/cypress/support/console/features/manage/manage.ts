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

import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { ApiVisibility, Enums } from "../../../commons/enums";
import { DeploymentTrack } from "../deployment-track/deployment-track";
import { UsagePlan } from "../../../commons/enums";
import { Types } from "../../../commons/types";
import { Component } from "../../entities/component/component";
import { Utils } from "../../../commons/utils";
import { LONG_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { Service } from "../../entities/component/service-component";

export interface ManageFeature {
  _changeLifeCycleState(component: Component, state: Enums.LifeCycleState);
  _updateUsagePlans(component: Component, plans: UsagePlan[]);
  _enableCors(component: Component, environment: Enums.Environment);
  _addPermissions(component: Component, permissions: string[]);
  _applyAllPermissionsToResources(component: Component, permissions: string[]);
  _deleteAllPermissionsFromResources(
    component: Component,
    permissions: string[]
  );
  _disableSecurity(
    component: Component,
    env: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  );
  _applyPermissionToResources(component: Component, permission: string);
  _verifyConsumer(appName: string);
  _updateAccessMode(component: Component, accessMode: Enums.Accessibility);
  _updateApiVisibility(component: Component, visibility: ApiVisibility);
}

export function mixinManage<T extends Types.Constructor>(
  base: T
): Types.Constructor<ManageFeature> & T {
  return class extends base {
    private sideMenu = new ServiceLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    _changeLifeCycleState(component: Component, state: Enums.LifeCycleState) {
      this.sideMenu.navigateToLifecycle();

      this.deploymentTrack.validate(component);

      switch (state) {
        case Enums.LifeCycleState.Publish:
          cy.get(TestIds.publishLifecycle).click();
          cy.get(TestIds.blockLifecycle).should("be.visible");
          cy.get(TestIds.prereleaseLifecycle).should("be.visible");
          cy.get(TestIds.demoteLifecycle).should("be.visible");
          cy.get(TestIds.deprecateLifecycle).should("be.visible");
          break;
        default:
          expect.fail(`Unhandled lifecycle state: ${state}`);
      }
    }

    _updateUsagePlans(component: Component, plans: UsagePlan[]) {
      this.sideMenu.navigateToUsagePlan();
      this.saveUsagePlans(component, plans);
    }

    _enableCors(component: Component, environment: Enums.Environment) {
      this.sideMenu.navigateToDeploy();

      let envCardSelector = TestIds.devEnvCard;
      let componentSettings = TestIds.apiConfiguration;

      if (environment === Enums.Environment.PRODUCTION) {
        envCardSelector = TestIds.prodEnvCard;
      }

      if (component instanceof Service) {
        componentSettings = TestIds.availableEndpoints;
      }

      cy.get(envCardSelector)
        .should("be.visible")
        .find(componentSettings)
        .find(TestIds.viewArtifact)
        .click();

      if (component instanceof Service) {
        cy.get(TestIds.endpointSettings).should("be.visible").click();
      }

      cy.get(TestIds.applyApiConfig, VERY_SHORT_TIME).should("be.enabled");
      cy.get(TestIds.manageSecurity).should("be.visible").click();
      cy.get(TestIds.corsCheckbox).should("be.visible").click();
      cy.get(TestIds.applyApiConfig).scrollIntoView().click();
      cy.get(TestIds.applyApiConfig, VERY_SHORT_TIME).should("be.enabled");
      cy.get(TestIds.cancelApiConfig).click();
      cy.get(TestIds.applyApiConfig).should("not.exist");
    }

    _addPermissions(component: Component, permissions: string[]) {
      this.sideMenu.navigateToDeploy();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.buildCard)
        .should("be.visible")
        .find(TestIds.viewArtifact)
        .click();

      cy.get(TestIds.addScopeBtnV2).should("be.visible").click();

      permissions.forEach((permission) => {
        this.addPermissionV2(permission);
      });
    }

    _applyAllPermissionsToResources(
      component: Component,
      permissions: string[]
    ) {
      cy.get(TestIds.applyScopesToAllV2).should("be.disabled");
      cy.get(TestIds.selectAllScopesV2).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAllV2).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAllV2).should("be.disabled");
      cy.get(TestIds.securitySettingsFirstResource)
        .should("be.visible")
        .click();

      permissions.forEach((permission) => {
        cy.get(TestIds.permissionTag(permission)).should("be.visible");
      });

      this.saveAndDeployPermissions(component.getName());
    }

    _deleteAllPermissionsFromResources(
      component: Component,
      permissions: string[]
    ) {
      this.sideMenu.navigateToDeploy();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.buildCard)
        .should("be.visible")
        .find(TestIds.viewArtifact)
        .click();

      cy.get(TestIds.deleteAllScopesV2).should("be.visible").click();
      cy.get(TestIds.securitySettingsFirstResource)
        .should("be.visible")
        .click();

      permissions.forEach((permission) => {
        cy.get(TestIds.permissionTag(permission)).should("not.exist");
      });

      this.saveAndDeployPermissions(component.getName());
    }

    _applyPermissionToResources(component: Component, permission: string) {
      this.sideMenu.navigateToDeploy();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.buildCard)
        .should("be.visible")
        .find(TestIds.viewArtifact)
        .click();

      cy.get(TestIds.scopeItemCheckBoxV2(permission))
        .should("be.visible")
        .click();
      cy.get(TestIds.applyScopesToAllV2).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAllV2).should("be.disabled");
      cy.get(TestIds.securitySettingsFirstResource)
        .should("be.visible")
        .click();

      cy.get(TestIds.permissionTag(permission)).should("be.visible");

      this.saveAndDeployPermissions(component.getName());
    }

    _verifyConsumer(appName: string) {
      this.sideMenu.navigateToConsumers();
      cy.get(TestIds.value(appName)).should("be.visible");
      cy.get(TestIds.progressBar).should("not.exist");
    }

    _disableSecurity(
      component: Component,
      env: Enums.Environment,
      method: Enums.HTTPMethod,
      resource: string
    ) {
      this.sideMenu.navigateToDeploy();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.buildCard)
        .should("be.visible")
        .find(TestIds.viewArtifact)
        .click();
      this.toggleResourceSecurity(method, resource);
    }

    _updateAccessMode(component: Component, accessMode: Enums.Accessibility) {
      this.sideMenu.navigateToSettings();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.accessMode, LONG_TIME).should("be.visible").click();
      cy.contains(accessMode, { matchCase: false }).should("exist").realClick();
      cy.get(TestIds.warningBanner).should("be.visible");
      cy.get(TestIds.dialogPrimaryAction).should("exist").click();
      cy.contains(
        `Successfully converted to an ${accessMode.toLowerCase()} API.`
      ).should("be.visible");
    }

    _updateApiVisibility(component: Component, visibility: ApiVisibility) {
      this.sideMenu.navigateToManage();
      cy.get(TestIds.apiInfo).should("be.visible").click();
      cy.get(TestIds.apiInfoDevPortal).should("be.visible").click();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.apiVisibility)
        .should("be.visible")
        .find("input")
        .invoke("val")
        .then((val) => {
          if (val !== visibility) {
            cy.get(TestIds.apiVisibility).should("be.visible").click();
            cy.contains(visibility, { matchCase: false })
              .should("exist")
              .click();

            cy.get(TestIds.apiInfoSave).should("be.enabled").click();
            cy.get(TestIds.backdropLoader).should("not.exist");
            cy.get(TestIds.apiInfoSave).should("be.disabled");

            cy.get(TestIds.apiVisibility)
              .should("be.visible")
              .find("input")
              .invoke("val")
              .should("eq", visibility);
          }
        });
    }

    private saveAndDeployPermissions(componentName: string) {
      this.applySecuritySettings();
    }

    private addPermissionV2(permission: string) {
      cy.get(TestIds.addNewScopeV2).should("be.disabled");
      cy.get(TestIds.scopeTextInputV2).type(permission);
      cy.get(TestIds.addNewScopeV2).should("be.enabled").click().wait(1000);
      cy.contains("Permission(Scope) created successfully");
      cy.get(TestIds.selectAllScopesV2).should("be.visible");
      cy.get(TestIds.scopeItem(permission)).should("be.visible");
    }

    private saveUsagePlans(component: Component, plans: UsagePlan[]) {
      this.deploymentTrack.validate(component);

      const unlimitedPlan = `[data-testid="checkbox-${UsagePlan.Unlimited}"]`;

      Utils.unCheckIfChecked(unlimitedPlan);

      plans.forEach((plan) => {
        const planLocator = `[data-testid="checkbox-${plan}"]`;
        Utils.checkIfUnchecked(planLocator);
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

    private toggleResourceSecurity(method: Enums.HTTPMethod, resource: string) {
      let methodString = method.toString();

      cy.get(`[id="panel-/${resource}/${methodString}-header"]`)
        .scrollIntoView()
        .click();

      cy.get(TestIds.security).scrollIntoView().click();

      this.applySecuritySettings();
    }

    private applySecuritySettings() {
      cy.get(TestIds.storyButton)
        .contains("Apply")
        .should("be.visible")
        .click();
      cy.get(TestIds.backdropLoader).should("not.exist");

      cy.get(TestIds.storyButton).should("not.exist");
    }
  };
}
