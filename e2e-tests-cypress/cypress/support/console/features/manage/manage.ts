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
import { LONG_TIME } from "../../../commons/timeouts";

export interface ManageFeature {
  _changeLifeCycleState(component: Component, state: Enums.LifeCycleState);
  _updateUsagePlans(component: Component, plans: UsagePlan[]);
  _enableCors(component: Component);
  _addPermissions(component: Component, permissions: string[]);
  _applyAllPermissionsToResources(component: Component, permissions: string[]);
  _deleteAllPermissionsFromResources(
    component: Component,
    permissions: string[]
  );
  _disableSecurity(
    component: Component,
    env: Enums.Environment,
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

    _enableCors(component: Component) {
      this.sideMenu.navigateToSettings();
      this.toggleCors(component, true);
    }

    _addPermissions(component: Component, permissions: string[]) {
      this.sideMenu.navigateToPermissions();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.addScopeBtn).should("be.visible").click();

      permissions.forEach((permission) => {
        this.addPermission(permission);
      });
    }

    _applyAllPermissionsToResources(
      component: Component,
      permissions: string[]
    ) {
      cy.get(TestIds.applyScopesToAll).should("be.disabled");
      cy.get(TestIds.selectAllScopes).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAll).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAll).should("be.disabled");

      permissions.forEach((permission) => {
        cy.get(TestIds.permissionTag(permission)).should("be.visible");
      });

      this.saveAndDeployPermissions(component.getName());
    }

    _deleteAllPermissionsFromResources(
      component: Component,
      permissions: string[]
    ) {
      cy.get(TestIds.deleteAllScopes).click();
      this.saveAndDeployPermissions(component.getName());

      permissions.forEach((permission) => {
        cy.get(TestIds.permissionTag(permission)).should("not.exist");
      });
    }

    _applyPermissionToResources(component: Component, permission: string) {
      cy.get(TestIds.scopeItemCheckBox(permission)).click();
      cy.get(TestIds.applyScopesToAll).should("be.enabled").click();
      cy.get(TestIds.applyScopesToAll).should("be.disabled");
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
      resource: string
    ) {
      this.sideMenu.navigateToSettings();

      this.deploymentTrack.validate(component);

      this.selectResources();
      this.selectEnvironment(env);
      this.selectRevision(env);
      this.editResource();
      this.selectResources();
      this.toggleResourceSecurity(resource);
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
      this.sideMenu.navigateToSettings();

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
            cy.get(TestIds.dialogPrimaryAction).should("be.visible").click();
            cy.get(TestIds.dialogPrimaryAction).should("not.exist");

            cy.get(TestIds.apiVisibility)
              .should("be.visible")
              .find("input")
              .invoke("val")
              .should("eq", visibility);
          }
        });
    }

    private saveAndDeployPermissions(componentName: string) {
      cy.get(TestIds.scopeSaveAndDeploy).click();
      cy.get(TestIds.backdropLoader).should("not.exist");
      cy.get(TestIds.scopeSaveAndDeploy).should("be.disabled");
      cy.contains(`Deployed the component ${componentName}`).wait(10000); // Extra wait because the application is not updated immediately
    }

    private addPermission(permission: string) {
      cy.get(TestIds.addNewScope).should("be.disabled");
      cy.get(TestIds.scopeTextInput).type(permission);
      cy.get(TestIds.addNewScope).should("be.enabled").click().wait(1000);
      cy.contains("Permission(Scope) created successfully");
      cy.get(TestIds.selectAllScopes).should("be.visible");
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

    private toggleCors(component: Component, isEnabled: boolean) {
      this.deploymentTrack.validate(component);

      this.editResource();
      cy.get(TestIds.corsConfig).click({ force: true });

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

    private selectResources() {
      cy.get(TestIds.resourceTab).click({ force: true });
    }

    private selectEnvironment(env: Enums.Environment) {
      cy.get(TestIds.envSelector).should("be.visible").scrollIntoView().click();
      cy.get(`[data-value="${env}"]`).click();
      cy.get(TestIds.envSelectorItems)
        .invoke("text")
        .then((text) => {
          expect(text).equal(env);
        });
    }

    private selectRevision(env: Enums.Environment) {
      cy.get(TestIds.revision).click();
      cy.get(TestIds.revisionHistory).should("be.visible");
      cy.get(TestIds.revisionItem).contains(env).click({ force: true });
    }

    private editResource() {
      cy.get(TestIds.editSettings).click();
    }

    private toggleResourceSecurity(resource: string) {
      cy.get(`[id="panel-/${resource}/get-header"]`).scrollIntoView().click();
      cy.get(TestIds.security).scrollIntoView().click();

      this.applySettingChanges();
    }
  };
}
