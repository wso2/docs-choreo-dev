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

import { TestIds } from "./constants/TestIds";
import { SHORT_TIME, VERY_SHORT_TIME } from "../commons/timeouts";

const handle = Cypress.env("choreoOrgHandle");
const idpParam = "?fidp=choreoe2etest";
const devportalLoginURL = Cypress.env("devportalLoginURL") + "/" + handle + idpParam;

export class DevPortal {
  
  searchApi(apiName: string, version?: string) {
    cy.get(TestIds.apiBar).click();
    cy.contains("All").should("be.visible");

    cy.get(TestIds.apiSearch)
      .should("be.visible")
      .focus()
      .type(`${apiName}{enter}`);

    if (version) {
      cy.get(TestIds.apiCard(apiName), VERY_SHORT_TIME)
        .should("be.visible")
        .contains(`Version : ${version}`)
        .click();
    } else {
      cy.get(TestIds.apiCard(apiName), VERY_SHORT_TIME)
        .should("be.visible")
        .click();
    }

    // Ensure API Overview page is loaded
    cy.get(TestIds.apiOverviewDevPortal).should("be.visible");
    cy.get(TestIds.apiNameDevPortal).contains(apiName).should("be.visible");
  }

  static searchApp(applicationName: string) {
    cy.get('[data-testid="applications-appbar-btn"]').click();
    cy.get('[data-testid="search-btn"]').trigger("mouseover");
    cy.get('[data-testid="search-app"] [placeholder="Search"]').type(applicationName);
    cy.get('[data-testid="application-list-a"] > [value="a"]').should("exist");
  }

  static loginToDevportalAsInvitedUser(devportalUrl = ''): void {
    const loginURL = devportalUrl ? devportalUrl + "/" + handle + idpParam : devportalLoginURL;
    cy.visit(loginURL);
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cy.get('button[type="submit"]', VERY_SHORT_TIME).should("be.visible");
          cy.get("#usernameUserInput").type(Cypress.env("choreoIDPInvitedUsername"));
          cy.get("#password").type(Cypress.env("choreoIDPInvitedPassword"), { log: false });
          cy.get('button[type="submit"]').click();
        }
      });
    cy.get("[data-testid=home-appbar-btn]", VERY_SHORT_TIME).should("be.visible");
  }
}

// Singleton instance of Choreo Dev Portal
export const devPortal = new DevPortal();
