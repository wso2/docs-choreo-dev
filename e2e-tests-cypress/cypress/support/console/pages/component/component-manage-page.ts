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

  static manageLifecycle() {
    cy.get('[data-testid="Lifecycle"]').click();
  }
  static verifyDevRevision() {
    this.selectSetting();
    return cy
      .get(".MuiChip-outlined")
      .eq(1)
      .should("be.visible")
      .invoke("text");
  }

  static verifyProdRevision() {
    this.selectSetting();
    return cy
      .get(".MuiChip-outlined")
      .eq(3)
      .should("be.visible")
      .invoke("text");
  }
  static publish(audience: ConnectorAudience) {
    this.publishToMarketplace(audience);
    return cy.get(ComponentAPILifecycle.devportl_btn).should("be.visible");
  }

  static publishWithoutConnector() {
    this.publishToDevportal();
    return cy.get(ComponentAPILifecycle.devportl_btn).should('be.visible');
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
    cy.get('[data-testid="Deploy as a Prototype-lc-btn"]')
      .should("be.visible")
      .click();
  }

  static goToDevportal() {
    cy.get(ComponentAPILifecycle.devportl_btn).click();
  }

  static goToDeveloperPortalWithoutLogin(idpUser: string) {
    cy.get('[data-cyid=go-to-dev-portal-btn]').parent()
      .invoke('attr', 'href')
      .then((href) => {
        cy.visit(href + '&fidp=' + idpUser)
      })
  }

  static selectUsagePlans(...plans) {
    cy.get('[data-testid="Usage plans"]').click();
    cy.get('[data-testid="checkbox-Unlimited"]').should("be.visible").click();
    plans.forEach((plan) => {
      const pln = `[data-testid="checkbox-${plan}"]`;
      cy.get(pln).click();
    });
    cy.get("button > span").contains("Save").click();
    cy.get('[data-testid="checkbox-Unlimited"]').should("be.visible");
  }

  static addDocument(
    documentName: string,
    documentSummary: string,
    documentType: DocumentType,
    documentSourceType: DocumentSourceType,
    documentSource: string
  ) {
    cy.get('[data-testid="Documents"]').should("be.visible").click();
    cy.get('[data-testid="add-new-doc"]').should("be.visible").click();
    cy.get('[data-testid="document-name"]>div>input').type(documentName);
    cy.get('[data-testid="document-summary"]>div>textarea').type(
      documentSummary
    );

    cy.get('[data-testid="document-type-selector"]').click();
    cy.get(`dta-value=${documentType}`).click();
    cy.get('[data-testid="document-source-selector"]').click();
    cy.get(`dta-value=${documentSourceType}`).click();
    cy.get('[data-testid="document-url"]>div>input').type(documentSource);
    cy.contains("Save").click();
  }

  static publishToMarketplace(connectorAudience: ConnectorAudience) {
    cy.get('[data-testid="Publish-lc-btn"]').should("be.visible").click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should("be.visible");
    cy.contains("Yes, Please").should("be.enabled").click();
    cy.get(`[data-testid="radio-audience-${connectorAudience}"]`).click();
    cy.get('[data-testid="publish-btn"]').should("be.visible").click();
    cy.get('[data-testid="marketplace-btn"]',{timeout:180000}).should('be.visible')
    cy.get('[data-testid="connector-publish-wizard-title"]',{timeout:180000}).should('not.exist')
  }


  static publishToDevportal() {
    cy.get('[data-testid="Publish-lc-btn"]').should('be.visible').click();
    cy.get('[aria-labelledby="confirmation-dialog"]').should('be.visible');
    cy.contains('No, Thanks').should('be.enabled').click();
  }

  static configureSecuritySettings(
    isCORSenable: boolean,
    isAllOriginsAllowed: boolean,
    allowedOrigins: string[],
    allowedHeaders: string[],
    allowedMethods: string[]
  ) {
    cy.get('[data-testid="Settings"]').click();
    cy.get('[data-testid="switch-cors-config"]').should("be.visible");
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
      cy.get('[data-testid="addBtn-origin"]').click();
      origins.forEach((ori) =>
        cy
          .get('[placeholder="Type and press enter to add Origins"]')
          .type(`${ori}`)
          .wait(1000)
          .type("{enter}")
      );
    }
  }

  private static addAllowedAccessControlHeaders(headers: string[]) {
    if (headers.length > 0) {
      cy.get('[data-testid="addBtn-header"]').click();
      headers.forEach((meth) =>
        cy
          .get('[placeholder="Type and press enter to add Headers"]')
          .type(`${meth}`)
          .wait(1000)
          .type("{enter}")
      );
    }
  }

  private static addAllowedAccessMethods(methods: string[]) {
    if (methods.length > 0) {
      cy.get('[data-testid="addBtn-method"]').click();
      methods.forEach((meth) => {
        cy.get('[aria-labelledby="demo-mutiple-name-label"]').click();
        cy.get(`[data-value=${meth.toUpperCase()}]`).click();
        cy.wait(1000);
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
    cy.get(`[data-testid="resource-${resource}"]>div`).eq(1).click();
    cy.get(`[data-testid="resource-${resource}"] [data-testid="security"]`)
      .should("be.visible")
      .click();
  }

  static applyConfiguration(env: Environment) {
    cy.get('[data-cyid="btn-save-settings"]').should("be.enabled").click();
    cy.get(`[aria-label="environment"]`).contains(env).click();
    cy.get("button").contains("Apply").click();
    cy.wait(2000);
    cy.intercept({
      method: "POST",
      url: `${Cypress.env('apimSvcURL')}/api/am/publisher/v2/apis/*/revisions?organizationId=*`,
      
    }).as("revision");

    cy.get('[data-cyid="btn-delete-settings"]').should("be.visible");
  }
  static getLatestRevision() {
    return cy.wait("@revision").then((revision) => {
     return cy.wrap(revision.response.body.displayName);
   });
 }

}
