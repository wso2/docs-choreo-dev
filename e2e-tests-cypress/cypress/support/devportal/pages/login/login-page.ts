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

import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
const handle = Cypress.env("choreoOrgHandle");
const idpParam = "?fidp=choreoe2etest";
const devportalLoginURL = Cypress.env("devportalLoginURL") + "/" + handle + idpParam;

export class LoginPage {
  static loginToDevportal(devportalUrl = ''): void {
    const loginURL = devportalUrl ? devportalUrl + "/" + handle + idpParam : devportalLoginURL;
    cy.visit(loginURL);
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cy.get('button[type="submit"]', VERY_SHORT_TIME).should("be.visible");
          cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
          cy.get("#password").type(Cypress.env("choreoIDPPassword"), { log: false });
          cy.get('button[type="submit"]').click();
        }
      });
    cy.get("[data-testid=home-appbar-btn]", VERY_SHORT_TIME).should("be.visible");
  }

  static visitToDevportalOrgPublicApis(): void {
    const loginURL = Cypress.env("devportalLoginURL") + "/" + handle;
    cy.visit(loginURL);
    cy.get('[data-testid="home-appbar-btn"]').should("exist");
    cy.log("Successfully navigated to public devportal");
  }
}
