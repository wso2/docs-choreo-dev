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


import { Utils } from "../utils";

export class LoginPage {


  static loginToInvitedUser(fileID: string, timestamp: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPInvitedUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPInvitedPassword"), {
      log: false,
    });

    cy.get('button[type="submit"]').click();

    const apimSvcURL = Cypress.env("apimSvcURL");
    cy.intercept({
      method: "POST",
      url: `${apimSvcURL}/oauth2/token`,
      times: 1,
    }).as("token");

    let token: string;
    cy.wait("@token", { timeout: 180000 }).then((interceptions) => {
      token = interceptions.response.body.access_token;
      const invitationId = Utils.getInvitationId(token, timestamp);
    });
  }

  static reLoginToChoreo(fileID: string) {
    const componentURL = Cypress.env(`${fileID}_componentURL`);
    cy.visit(componentURL);
    cy.intercept(componentURL).then(() => {
      cy.setCookie("commonAuthId", Cypress.env(`commonAuthId`), {
        path: "/",
        domain: "id.dv.choreo.dev",
        secure: true,
        httpOnly: true,
        sameSite: "no_restriction",
      });
    });
  }

  static navigateToCodespace(fileID: string) {
    cy.visit(Cypress.env(`${fileID}_accessURL`));
  }

  



}
