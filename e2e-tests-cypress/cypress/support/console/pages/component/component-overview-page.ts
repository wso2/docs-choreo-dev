
import { MEDIUM_TIME } from "../../../commons/timeouts";

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
export class ComponentOverviewPage {
  static goBack() {
    cy.get('[data-testid="main-left-nav-item-Project"]').should("be.visible").click();
  }

  static navigateToDeploy() {
    cy.get("[data-cyid=link-deploy]").click();
  }

  static navigateToTest(navigateViaLink:boolean=false) {
    if(navigateViaLink){
      cy.get('[data-cyid="link-test"]').eq(0).click()
    }else{
      cy.contains("Test").should("be.visible").click({ force: true });
    }
   
  }

  static navigateToOverview(){
    cy.get("[data-cyid=link-overview]").should("be.visible").click();
    cy.intercept({
      method: "POST",
      url: `/insights/1.0.0/query-api`,
      times: 1,
    }).as("insights");
    cy.wait("@insights", { timeout: 180000 }).then(() => {
      cy.get("[data-cyid=copy-release-details-btn]").should('be.visible');
    });
  
  }

    static navigateToManage() {
      cy.get('[data-cyid="link-manage"]').should("be.visible").click({ force: true });
  }

  static navigateToObserve() {
    cy.get("[data-cyid=link-observe]").click();
  }

  static navigateToDevelop() {
    cy.get('[data-cyid="link-develop"]').click();
  }

  static navigateToDevPortal() {
    cy.get("[data-cyid='developer-portal-link']")
      .invoke("attr", "href")
      .then((href) => cy.visit(href));
    return cy.get("header>div>div>p").invoke("text");
  }



  static createNewVersion(version: string, newBranch: string) {
    cy.get('[data-cyid="version-picker"]').click();
    cy.get("[data-cyid=btn-create-version]").click();

    if (newBranch) {
      this.createNewVersionRestApi(version, newBranch);
    } else {
      this.createNewVersionApiProxy(version);
    }
  }

  private static createNewVersionApiProxy(version: string) {
    cy.contains("Create new version", MEDIUM_TIME);
    
    cy.get('[data-cyid="text-field-new-version"]').within(() => { cy.get("input").clear().type(version) })
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }

  private static createNewVersionRestApi(version: string, branch: string) {
    cy.get('[data-testid="dialog-close-icon"]').should("exist");
    cy.get('div>[aria-label="Without label"]').click();
    cy.get(`[data-value=${branch}]`).click();
    cy.get('[data-cyid="text-field-new-version"]>div>input')
      .clear()
      .type(version);
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }
}
