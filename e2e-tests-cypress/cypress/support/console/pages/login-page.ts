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

export class LoginPage {
  static loginToChoreo(fileID: string, enableIntercept: boolean = false) {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPPassword"), { log: false });

    cy.get('button[type="submit"]').click();
    this.persistOrgs(fileID);
    this.persistCookies(fileID);
  }

  static reloginToChoreo(fileID: string) {
    const file = `${Cypress.env("tempFile")}${fileID}.json`;

    cy.readFile(file).then((d) => {
      cy.visit(d.componentURL);
      cy.intercept(d.componentURL).then(() => {
        cy.readFile(file).then((data) => {
          cy.setCookie("commonAuthId", data.commonAuthId, {
            path: "/",
            domain: "id.dv.choreo.dev",
            secure: true,
            httpOnly: true,
            sameSite: "no_restriction",
          });
        });
      });
    });
  }

  static navigateToCodespace(fileID: string) {
    cy.readFile(`${Cypress.env("tempFile")}${fileID}.json`).then((data) => {
      cy.visit(data.accessURL);
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
      cy.task("writeTestData", {
        fileName: fileID,
        key: "orgData",
        value: orgData,
      });
    });
  }


  private static persistCookies(fileID: string) {
    cy.log("persistCookies()");
    cy.get('[alt="Choreo Logo"]', { timeout: 120000 });
    cy.request(`${ Cypress.env("idpURL")}/commonauth`).then((res) => {
      const cookies = res.requestHeaders["cookie"].split(";");
      cookies.forEach((c) => {
        if (c.trim().includes("commonAuthId")) {
          const commonAuthId = c.replace("commonAuthId=", "").trim();
          cy.task("writeTestData", {
            fileName: fileID,
            key: "commonAuthId",
            value: commonAuthId,
          });
          return;
        }
      });
    });
  }
}
