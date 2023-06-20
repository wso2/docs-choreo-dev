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

import { MENU_RENDERING_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";
import { LoginPage } from "../login-page";

export class ChoreoHomePage {
  static navigateToHome() {
    cy.get('[data-cyid="organization-home"]').click();
  }

  static navigateToProjects() {
    cy.get('[data-testid="main-left-nav-item-Project"]').click();
  }

  static navigateToComponents() {
    if (Utils.isUnifiedMenuEnabled()) {
      cy.get('[data-cyid="listing"]')
        .realHover({ position: "left" })
        .wait(MENU_RENDERING_TIME)
        .click()
        .wait(MENU_RENDERING_TIME);
      Utils.moveMouseAwayFromLeftMenu();
    } else {
      cy.get('[data-testid="main-left-nav-item-Components"]').click();
    }
  }

  static navigateToInsights() {
    if (Utils.isUnifiedMenuEnabled()) {
      cy.get('[data-cyid="usage-insights"]')
        .realHover({ position: "left" })
        .wait(MENU_RENDERING_TIME)
        .click()
        .wait(MENU_RENDERING_TIME);
      Utils.moveMouseAwayFromLeftMenu();
      cy.contains("Coming Soon").should("be.visible");
      cy.get('[data-cyid="project-usage-insights"]')
        .should("be.visible")
        .click();
      cy.get('[id="backdrop-loader"]').should("not.exist");
    } else {
      cy.get('[data-testid="main-left-nav-item-Insights"]').click();
    }
  }

  static isOrgHandleVisible(orgHandle: string) {
    cy.get('[id="org-picker"]').click();
    cy.get('[data-value="' + orgHandle + '"]');
  }

  static logout() {
    cy.request(Cypress.env("sign_out_url"));
    cy.clearAllSessionStorage();
    cy.clearLocalStorage();
    cy.clearAllCookies();
  }

  static navigateToSettings() {
    if (Utils.isUnifiedMenuEnabled()) {
      cy.get("#backdrop-loader").should("not.exist");
      this.navigateToHome();
      cy.get("#backdrop-loader").should("not.exist");
      cy.get('[data-cyid="settings"]').should("be.visible").click();
    } else {
      cy.get("#backdrop-loader").should("not.exist");
      cy.get('[data-testid="header-user-profile-menu"]').click();
      cy.get('[data-testid="header-user-profile-item-settings"]')
        .should("be.visible")
        .contains("Settings")
        .click();
    }
  }

  static switchOrganization() {
    if (Cypress.env("isPrivateOrg")) {
      cy.get("#org-picker").click();
      cy.get(`[data-value="${Cypress.env("privateOrgName")}"]`).click();
      LoginPage.persistApimToken();
    }
  }

  static changeToAPIPerspective() {
    cy.get("#perspective-picker").click();
    cy.get(".MuiList-root")
      .should("be.visible")
      .get(`[data-value="apim"]`)
      .click();
  }

  static changeToIDevPerspective() {
    cy.get('[data-testid="perspective-pickerapim"]').click();
    cy.get(".MuiList-root")
      .should("be.visible")
      .get(`[data-value="idevp"]`)
      .click();
  }

  static goToMarketplacePage() {
    const Url = Cypress.env("baseUrl");
    const { handle } = Cypress.env("userData");
    
      let marketplaceURL = `${Url}/organizations/${handle}/marketplace`;
      cy.visit(marketplaceURL);
    
  }

}
