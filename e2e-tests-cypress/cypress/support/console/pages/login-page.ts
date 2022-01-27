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
  static loginToChoreo(fileID: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible");
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPPassword"));

    this.interceptRequiredApiCalls();

    cy.get('button[type="submit"]').click();
    Cypress.Cookies.debug(true);

    this.persistCookies(fileID);
    this.testSetup(fileID);
  }

  static reloginToChoreo(fileID: string) {
    const file = `${Cypress.env("tempfile")}${fileID}.json`;

    cy.readFile(file).then((d) => {
      cy.visit(d.componentURL);
      cy.intercept(d.componentURL).then(() => {
        cy.readFile(file).then((data) => {
          cy.setCookie("commonAuthId", data.commanAuthId, {
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
    cy.readFile(`${Cypress.env("tempfile")}${fileID}.json`).then((data) => {
      cy.visit(data.accessURL);
    });
  }

  private static interceptRequiredApiCalls() {
    const appSvcURL = Cypress.env("appSvcURL");
    const idpURL = Cypress.env("idpURL");
    const apimSvcURL = Cypress.env("apimSvcURL");
    const balRegistryURL = Cypress.env("balRegistryURL");

    cy.intercept("POST", `${idpURL}/commonauth`).as("cookies");
    cy.intercept({
      method: "POST",
      url: `${apimSvcURL}/oauth2/token`,
      times: 1,
    }).as("token");
    cy.intercept("GET", Cypress.env("appSvcURL") + "/validate-user").as("org");
    cy.intercept({
      method: "GET",
      url: `${balRegistryURL}/packages?*`,
      times: 1,
    }).as("balRegistry"); // Ensure ballerina registry call completes before interacting with UI
  }

  private static persistOrgs(interceptor: any, fileID: string) {
    const handle = Cypress.env("choreoOrgHandle");
    let userOrg: any;
    if (handle) {
      userOrg = interceptor.response.body.organizations.find(
        (o: { handle: any }) => o.handle === handle
      );

      if (userOrg === undefined) {
        throw new Error(
          `Configured org handle ${handle} does not exist for current user`
        );
      }
      cy.log(`Configured org handle ${userOrg.handle} selected`);
    } else {
      [userOrg] = interceptor.response.body.organizations;
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

    return userOrg;
  }

  private static testSetup(fileID: string) {
    cy.log("testSetup()");
    let token: string;
    cy.wait(["@token", "@org", "@balRegistry"], { timeout: 120000 }).then(
      (interceptions) => {
        token = interceptions[0].response.body.access_token;

        const userOrg = this.persistOrgs(interceptions[1], fileID);

        GraphQL.createDefaultProjectIfNotExists(
          userOrg.id,
          userOrg.handle,
          token
        );

        //    GraphQL.deleteProjectsCreatedByTests(userOrg.id, userOrg.handle, token);
      }
    );
  }

  private static persistCookies(fileID: string) {
    cy.log("persistCookies()");
    cy.wait("@cookies").then((interceptor) => {
      const cookies = interceptor.response.headers["set-cookie"];
      if (Array.isArray(cookies)) {
        cookies.forEach((element) => {
          if (element.includes("commonAuthId")) {
            const commonauId = element
              .split(";")[0]
              .replace("commonAuthId=", "");
            cy.log(`Common Auth ID :: ${commonauId}`);
            cy.task("writeTestData", {
              fileName: fileID,
              key: "commanAuthId",
              value: commonauId,
            });
          }
        });
      }
    });
  }
}
