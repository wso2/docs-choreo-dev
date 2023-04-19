/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums } from "../../enums";
import { Utils } from "../../utils";


export class ComponentAPILifecycle {
  static devportl_btn = '[data-testid="go-to-dev-portal-btn"]';

  static republishConnector() {
    cy.get('[data-testid="republish-connector-btn"]').scrollIntoView().click();
    cy.get(".MuiDialog-paper div>button>span").contains("Republish").click();
    cy.intercept({
      method: "POST",
      url: `${Cypress.env("appSvcURL")}/user-connectors/*/*/republish`,
    }).as("publish");
    cy.wait("@publish", { timeout: 80000 }).then((res) => {
      const { success } = res.response.body;
      expect(success).to.be.equal("ok");
    });
  }

  static manageLifecycle() {
    cy.get('[data-testid="Lifecycle"]').click();
  }
  static verifyDevRevision() {
    return cy
      .get('.MuiBox-root >div>div>span[class*="MuiChip-label"]')
      .eq(0)
      .invoke("text");
  }

  static publish(audience: Enums.ConnectorAudience) {
    this.publishToMarketplace(audience);
    return cy
      .get(ComponentAPILifecycle.devportl_btn)
      .focus()
      .should("be.visible");
  }

  static publishRestApiWithoutConnector() {
    cy.get('[data-testid="Publish-lc-btn"]').click();
    cy.get("body").then((body) => {
      if (body.find('[aria-labelledby="confirmation-dialog"]').length > 0) {
        cy.contains("No, Thanks").should("be.enabled").click();
      }
    });
  }

  static publishWithoutConnector() {
    this.publishToDevportal();
    return cy.get(ComponentAPILifecycle.devportl_btn).should("be.visible");
  }

  static demoteToCreated() {
    cy.get('[data-testid="Demote to Created-lc-btn"]').click();
    cy.get(ComponentAPILifecycle.devportl_btn).should("not.be.enabled");
  }


  static goToDeveloperPortalWithoutLogin(idpUser: string = "") {
    const { latestAPIVersionId } = Cypress.env("apiInfo");
    const loginUrl = Cypress.env("devportalLoginURL");
    const { uuid, handle } = Cypress.env("userData");
    let devportalURL = `${loginUrl}/${handle}/apis/${latestAPIVersionId}?fidp=${idpUser}&orgUuid=${uuid}`;
    cy.visit(devportalURL);
  }

  static selectUsagePlans(...plans) {
    cy.get('[data-testid="Usage plans"]').click();
    cy.get('[data-testid="checkbox-Unlimited"]').click();
    plans.forEach((plan) => {
      const pln = `[data-testid="checkbox-${plan}"]`;
      cy.get(pln).click();
    });
    cy.get("button > span").contains("Save").click();
    cy.get('[data-testid="checkbox-Unlimited"]');
  }


  static publishToMarketplace(connectorAudience: Enums.ConnectorAudience) {
    cy.get('[data-testid="Publish-lc-btn"]').click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should("be.visible");
    cy.contains("Yes, Please").should("be.enabled").click();
    cy.get('[data-testid="publish-btn"]').should("be.enabled");
    cy.get(`[data-testid="radio-audience-${connectorAudience}"]`).click();
    cy.get('[data-testid="publish-btn"]').should("be.enabled").click();
    cy.wait(1000);
    cy.get('[data-testid="published-connector-info"]').contains(
      "You have already published a connector for this API.",
      { timeout: 300000 }
    );
    cy.get('[data-testid="connector-publish-wizard-title"]').should(
      "not.exist"
    );
  }

  static publishToDevportal() {
    cy.get('[data-testid="Publish-lc-btn"]').click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should("be.visible");
    cy.contains("No, Thanks").should("be.enabled").click();
  }

  static configureSecuritySettings(
    isCORSenable: boolean,
    isAllOriginsAllowed: boolean,
    allowedOrigins: string[],
    allowedHeaders: string[],
    allowedMethods: string[]
  ) {
    cy.get('[data-testid="Settings"]').click();
    cy.get('[data-testid="switch-cors-config"]');
    if (isCORSenable) {
      cy.contains("Edit").click();
      cy.get('[data-testid="switch-cors-config"]').click();
      if (!isAllOriginsAllowed) {
        cy.get('[data-testid="checkbox-allow-all-origins"]').click();
      }
      this.addAllowedOrigins(allowedOrigins);
      this.addAllowedAccessControlHeaders(allowedHeaders);
      this.addAllowedAccessMethods(allowedMethods);
      cy.get('[data-cyid="btn-save-settings"]')
        .should("exist")
        .click({ force: true });
      cy.get("button").contains("Apply").click();
      cy.contains("Delete").should("be.visible");
      cy.wait(6000);
    }
  }

  private static addAllowedOrigins(origins: string[]) {
    if (origins.length > 0) {
      origins.forEach((ori) =>
        cy
          .get('[data-testid="cors-config-origins-input"]>div>div>input')
          .type(`${ori}`)
          .wait(1000)
          .type("{enter}")
      );
    }
  }

  private static addAllowedAccessControlHeaders(headers: string[]) {
    if (headers.length > 0) {
      headers.forEach((meth) => {
        cy.get('[data-testid="cors-config-header-input"]>div>div').click();
        cy.get('[data-testid="cors-config-header-input"]>div>div>input')
          .type(`${meth}`)
          .wait(1000)
          .type("{enter}");
      });
    }
  }

  private static addAllowedAccessMethods(methods: string[]) {
    if (methods.length > 0) {
      methods.forEach((meth) => {
        cy.get(
          '[data-testid="cors-config-methods-input"]>div>div>div.MuiAutocomplete-endAdornment>button'
        )
          .eq(1)
          .click();
        cy.contains(meth.toUpperCase()).click();
      });
    }
  }

  static selectSetting() {
    cy.get('[data-testid="Settings"]').click();
  }

  static selectResources() {
    cy.get('[data-cyid="tab-resource-settings"]').click({ force: true });
  }

  static editResource() {
    cy.get('[data-cyid="btn-edit-settings"]').click();
  }

  static selectEnvironment(env: Enums.Environment) {
    cy.get('[data-cyid="environment-selector"]').should("be.visible").click();
    cy.get(`[data-value="${env}"]`).click();
    cy.get('[data-cyid="environment-selector"]>div>div')
      .invoke("text")
      .then((text) => {
        expect(text).equal(env);
      });
  }

  static selectRevision(env: Enums.Environment) {
    cy.get('[data-cyid="selected-revision"]').click();
    cy.get('[data-testid="revision-history-header"]').should("be.visible");
    cy.get('[data-cyid*="revision-list-item"]')
      .contains(env)
      .click({ force: true });
  }

  static disableResourceSecurity(resource: string) {
    cy.get(`[id="panel-/${resource}/get-header"]`).scrollIntoView().click();
    cy.get(`[id="panel-/${resource}/get-content"] [data-testid="security"]`)
      .scrollIntoView()
      .click();
  }

  static applyConfiguration() {
    cy.get('[data-cyid="btn-save-settings"]').click();
    cy.get("button").contains("Apply").click().wait(2000);
    cy.get('[data-cyid="btn-delete-settings"]').should("be.visible");
    cy.get("#panel1a-header").should("be.visible");
    cy.get('[data-cyid="btn-delete-settings"]').should("be.visible");
    cy.wait(4000);
  }

  static selectConsumers() {
    cy.get('[data-cyid="Consumers"]').click();
  }

  static verifyConsumer(appName: string) {
    return cy.get(`[value=${appName}]`);
  }

  static verifyAPIVisibility(visibility: string) {
    cy.get('div[role="combobox"]>div>div>input')
      .eq(1)
      .invoke("val")
      .should("eq", visibility);
    cy.log("Successfully verified the API visibility", visibility);
  }

  static updateAPIVisibility(visibility: string) {
    cy.get('[data-cyid="tab-security-settings"]').should("be.visible");
    cy.wait(5000);
    cy.get('div[role="combobox"]').eq(1).click();
    cy.get(`ul[id="Select List-popup"]>li`).contains(visibility).click();
    cy.get('[data-testid="info-banner"]').should("be.visible");
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]').wait(100).realClick();
    this.verifyAPIVisibility(visibility);
    cy.log("Successfully updated the API visibility");
  }



  static updateAPIAccessMode(accessMode: string) {
    Utils.pollElement('[data-testid="access-mode"]').click();
    cy.get(`li[id*="Select"]`)
      .contains(accessMode)
      .should("exist")
      .click({ force: true });
    cy.get('[data-testid="warning-banner"]').should("be.visible");
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]')
      .should("exist")
      .click();
    cy.contains(
      `Successfully converted to an ${accessMode.toLowerCase()} API.`
    ).should("be.visible");
  }

  static managePermissions(permissions: string[], componentName: string) {
    permissions.forEach((permission) => {
      this.addPermission(permission);
    });
    this.applyAllPermissionsToResources(permissions);
    this.saveAndDeployPermissions(componentName);
    this.deleteAllPermissionsFromReources();
    this.saveAndDeployPermissions(componentName);
    this.selectPermission(permissions[0]);
    this.saveAndDeployPermissions(componentName);
  }

  static selectPermissions() {
    cy.get('[data-testid="Permissions"]').click();
  }

  static navigatePermissionManagementWindow() {
    cy.get("h5").contains(
      "You don't have any permissions (scopes) defined as yet"
    );
    cy.get('[data-testid="scope-add-icon-button"]').click();
  }

  static addPermission(permissionName: string) {
    cy.get('[data-testid="scope-add-new-btn"]').should("be.disabled");
    cy.get('[data-testid="scope-text-input"]').type(permissionName);
    cy.get('[data-testid="scope-add-new-btn"]')
      .should("be.enabled")
      .click()
      .wait(1000);
    cy.contains("Permission(Scope) created successfully");
    cy.get('[data-testid="scope-select-all-btn"]').should("be.visible");
    cy.get(`[data-testid="scope-item-${permissionName}"]`).should("be.visible");
  }

  static applyAllPermissionsToResources(permissions: string[]) {
    cy.get('[data-testid="scope-apply-to-all-btn"]').should("be.disabled");
    cy.get('[data-testid="scope-select-all-btn"]').should("be.enabled").click();
    cy.get('[data-testid="scope-apply-to-all-btn"]')
      .should("be.enabled")
      .click();
    this.verifyApplyAllPermissionsToResources(permissions);
  }

  static verifyApplyAllPermissionsToResources(permissions: string[]) {
    cy.get('[data-testid="autocomplete-textfield"]>div')
      .find(".MuiChip-root")
      .should("have.length", permissions.length * 3);
  }

  static deleteAllPermissionsFromReources() {
    cy.get('[data-testid="scope-delete-all-btn"]').click();
    // This can be enabled after fixing the bug in the autocomplete
    // https://github.com/wso2-enterprise/choreo/issues/17547

    // this.verifyDeleteAllPermissionsFromReources();
  }



  static selectPermission(permissionName: string) {
    cy.get(`[data-testid="scope-item-checkbox-${permissionName}"]`).click();
    cy.get('[data-testid="scope-apply-to-all-btn"]')
      .should("be.enabled")
      .click();
    // This can be enabled after fixing the bug in the autocomplete
    // https://github.com/wso2-enterprise/choreo/issues/17521

    // cy.get('[data-testid="autocomplete-textfield"]').click();
    // cy.get('li[data-option-index="0"]').contains(permissionName).then((option) => {
    //   option[0].click();
    // });
  }

  static deletePermission(permissionName: string) {
    cy.get(`[data-testid="scope-delete-btn-${permissionName}"]`).click();
    cy.get('[data-testid="scope-delete-description"]').contains(
      `Are you sure you want to Delete the permission (scope) "${permissionName}"?`
    );
    // Verify scope being used by how many resources
    cy.get('[data-testid="scope-delete-delete-button"]').click();
    cy.contains("Permission(Scope) deleted successfully");
  }

  static saveAndDeployPermissions(componentName: string) {
    cy.get('[data-testid="scope-save-and-deploy-btn"]').click();
    cy.contains("Permissions(Scopes) assigned successfully").wait(1000);
    cy.contains(`Deployed the component ${componentName}`).wait(10000);
  }
}
