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

import { GraphQL } from "../apis/graphql";
import { Utils } from "../utils";

export class LoginPage {
  static loginToChoreo(fileID: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPPassword"), { log: false });

    cy.get('button[type="submit"]').click();
    this.persistOrgs(fileID);
    this.persistCookies(fileID);
    this.persistLogoutURL();
  }

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
      cy.setCookie("commonAuthId", Cypress.env(`${fileID}_commonAuthId`), {
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

  private static persistLogoutURL() {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => {
        Cypress.env("sign_out_url", url);
      });
  }

  // private static testSetup(fileID: string) {
  //   cy.log("testSetup()");
  //   let token: string;
  //   cy.wait("@org", { timeout: 180000 }).then(
  //     (interceptions) => {
  //       token = interceptions.response.body.access_token;

  //       const userOrg = this.persistOrgs(interceptions, fileID);

  //       //    GraphQL.deleteProjectsCreatedByTests(userOrg.id, userOrg.handle, token);
  //     }
  //   );
  // }

  private static persistOrgs(fileID: string) {
    cy.intercept("GET", Cypress.env("appSvcURL") + "/validate-user").as("org");
    cy.wait("@org", { timeout: 180000 }).then((res) => {
      let userOrg: any;
      const handle = Cypress.env("choreoOrgHandle");
      if (handle) {
        userOrg = res.response.body.organizations.find(
          (o: { handle: any }) => o.handle === handle
        );
        if (userOrg === undefined) {
          throw new Error(
            `Configured org handle ${handle} does not exist for current user`
          );
        }
        cy.log(`Configured org handle ${userOrg.handle} selected`);
      } else {
        [userOrg] = res.response.body.organizations;
        cy.log(`First available org ${userOrg.handle} selected`);
      }
      const orgData = {
        orgId: userOrg.id,
        handle: userOrg.handle,
      };
    });
  }

  private static persistCookies(fileID: string) {
    cy.log("persistCookies()");
    cy.get('[alt="Choreo Logo"]', { timeout: 120000 });
    cy.request(`${Cypress.env("idpURL")}/commonauth`).then((res) => {
      const cookies = res.requestHeaders["cookie"].split(";");
      cookies.forEach((c) => {
        if (c.trim().includes("commonAuthId")) {
          const commonAuthId = c.replace("commonAuthId=", "").trim();
          Cypress.env(`${fileID}_commonAuthId`, commonAuthId);
          return;
        }
      });
    });
  }
}
