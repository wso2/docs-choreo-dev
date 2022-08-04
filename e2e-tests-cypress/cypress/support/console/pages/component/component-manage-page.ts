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

import { DocumentType } from "../enum/document-type";
import { DocumentSourceType } from "../enum/document-source";
import { ConnectorAudience } from "../enum/marketplace-connector-audience";
import { Environment } from "../enum/environment";



export class ComponentAPILifecycle {
  static devportl_btn = '[data-testid="go-to-dev-portal-btn"]';

  static republishConnector() {
    cy.get('[data-testid="republish-connector-btn"]').click();
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
    return cy.get('.MuiBox-root >div>div>span[class*="MuiChip-label"]').eq(0).invoke("text");
  }

  static verifyProdRevision() {
    return cy.get('.MuiBox-root >div>div>span[class*="MuiChip-label"]').eq(1).invoke("text");
  }
  static publish(audience: ConnectorAudience) {
    this.publishToMarketplace(audience);
    return cy.get(ComponentAPILifecycle.devportl_btn).should("be.visible");
  }

  static publishWithoutConnector() {
    this.publishToDevportal();
    return cy.get(ComponentAPILifecycle.devportl_btn).should("be.visible");
  }

  static demoteToCreated() {
    cy.get('[data-testid="Demote to Created-lc-btn"]').click();
    cy.get(ComponentAPILifecycle.devportl_btn).should("not.be.visible");
  }

  static deprecate() {
    cy.get('[data-testid="Deprecate-lc-btn"]').click();
  }

  static block() {
    cy.get('[data-testid="Block-lc-btn"]').click();
  }

  static deployAsPrototype() {
    cy.get('[data-testid="Deploy as a Prototype-lc-btn"]').click();
  }

  static goToDevportal() {
    cy.get(ComponentAPILifecycle.devportl_btn).click();
  }

  static goToDeveloperPortalWithoutLogin(idpUser: string) {
    cy.get("[data-cyid=go-to-dev-portal-btn]").parent().invoke("attr", "href").
      then((href) => cy.visit(href + "&fidp=" + idpUser));
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

  static addDocument(documentName: string, documentSummary: string, documentType: DocumentType, documentSourceType: DocumentSourceType,
    documentSource: string
  ) {
    cy.get('[data-testid="Documents"]').click();
    cy.get('[data-testid="add-new-doc"]').click();
    cy.get('[data-testid="document-name"]>div>input').type(documentName);
    cy.get('[data-testid="document-summary"]>div>textarea').type(documentSummary);
    cy.get('[data-testid="document-type-selector"]').click();
    cy.get(`dta-value=${documentType}`).click();
    cy.get('[data-testid="document-source-selector"]').click();
    cy.get(`dta-value=${documentSourceType}`).click();
    cy.get('[data-testid="document-url"]>div>input').type(documentSource);
    cy.contains("Save").click();
  }

  static publishToMarketplace(connectorAudience: ConnectorAudience) {
    cy.get('[data-testid="change-state-info"]').should('be.visible')
    cy.get('[data-testid="Publish-lc-btn"]').click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should('be.visible');
    cy.contains("Yes, Please").should("be.enabled").click();
    cy.get(`[data-testid="radio-audience-${connectorAudience}"]`).click();
    cy.get('[data-testid="publish-btn"]').click();

    cy.get('[data-testid="marketplace-btn"]').should('be.visible')
    cy.get('[data-testid="connector-publish-wizard-title"]').should("not.exist")
  }



  static publishToDevportal() {
    cy.get('[data-testid="Publish-lc-btn"]').click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should('be.visible');
    cy.contains("No, Thanks").should("be.enabled").click();
  }

  static configureSecuritySettings(isCORSenable: boolean, isAllOriginsAllowed: boolean, allowedOrigins: string[], allowedHeaders: string[],
    allowedMethods: string[]) {
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
      cy.get('[role="button"]>span').contains("Save").click();
      cy.get("button").contains("Apply").click();
      cy.contains("Delete").should("be.visible");
    }
  }

  private static addAllowedOrigins(origins: string[]) {
    if (origins.length > 0) {
      origins.forEach((ori) => cy.get('[data-testid="cors-config-origins-input"]>div>div>input').type(`${ori}`).wait(1000).type("{enter}"));
    }
  }

  private static addAllowedAccessControlHeaders(headers: string[]) {
    if (headers.length > 0) {
      headers.forEach((meth) => {
        cy.get('[data-testid="cors-config-header-input"]>div>div').click();
        cy.get('[data-testid="cors-config-header-input"]>div>div>input').type(`${meth}`).wait(1000).type("{enter}")
      });
    }
  }

  private static addAllowedAccessMethods(methods: string[]) {
    if (methods.length > 0) {

      methods.forEach((meth) => {
        cy.get('[data-testid="cors-config-methods-input"]>div>div>div.MuiAutocomplete-endAdornment>button').eq(1).click();
        cy.contains(meth.toUpperCase()).click();

      });
    }
  }

  static selectSetting() {
    cy.get('[data-testid="Settings"]').click();
  }

  static selectResources() {
    cy.get('[data-cyid="tab-resource-settings"]').click();
  }

  static editResource() {
    cy.get('[data-cyid="btn-edit-settings"]').click();
  }

  static disableResourceSecurity(resource: string) {
    cy.get(`[data-testid="resource-/${resource}"]>div`).eq(1).click();
    cy.get(`[data-testid="resource-/${resource}"] [data-testid="security"]`).click();
  }

  static applyConfiguration(env: Environment, revision: string = "") {
    cy.get('[data-cyid="btn-save-settings"]').click();
    cy.get(`[aria-label="environment"]`).contains(env).click();
    cy.get("button").contains("Apply").click().wait(2000);

    if(env===Environment.DEVELOPMENT){
      cy.intercept({
        method: "POST",
        url: `${Cypress.env("apimSvcURL")}/api/am/publisher/v2/apis/*/revisions?organizationId=*`,
      }).as("revision");
      cy.wait("@revision", { timeout: 180000 }).then((revision) => {
        // const revi = revision.response.body.displayName
        // expect(revi).equal(revision)
        // cy.log(revision.response.body.displayName)
      });
    }

    cy.get('[data-cyid="btn-delete-settings"]').should('be.visible');
    cy.reload()
    cy.get('[data-cyid="btn-delete-settings"]').should('be.visible');

  }


  static selectConsumers() {
    cy.get('[data-cyid="Consumers"]').click();
  }

  static verifyConsumer(appName: string) {
    return cy.get(`[value=${appName}]`);
  }
}
