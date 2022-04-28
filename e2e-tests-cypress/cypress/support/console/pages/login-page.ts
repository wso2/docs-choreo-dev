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
  static acceptInviteAsInvitedUser(timestamp: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPInvitedUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPInvitedPassword"), {
      log: false,
    });
    cy.get('button[type="submit"]').click();
    cy.intercept({
      method: "POST",
      url: `${Cypress.env("apimSvcURL")}/oauth2/token`,
      times: 1,
    }).as("token");

    let token: string;
    cy.wait("@token", { timeout: 180000 }).then((interceptions) => {
      token = interceptions.response.body.access_token;
      Utils.acceptEmailInviteToOrg(token, timestamp);
      cy.reload(); // Reload in order to get updated orgs
    });
  }

  static reLoginToChoreo() {
    const componentURL = Cypress.env(`componentURL`);
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

  static navigateToCodespace() {
    const csurl = Cypress.env(`accessURL`);
    cy.visit(csurl);

    if (Cypress.env("loginURL").includes("dev")) {
      cy.intercept(csurl).then(() => {
        cy.setCookie("opbs", Cypress.env(`asgardeo_opbs`), {
          path: "/t/a/",
          domain: Cypress.env("asgardeoDomain"),
          secure: true,
          httpOnly: true,
          sameSite: "no_restriction",
        });
        cy.setCookie("commonAuthId", Cypress.env(`asgardeo_commonauth`), {
          path: "/t/a/",
          domain: Cypress.env("asgardeoDomain"),
          secure: true,
          httpOnly: true,
          sameSite: "no_restriction",
        });
      });
    }
  }

  static login() {
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
    cy.get("#password").type(Cypress.env("choreoIDPPassword"), { log: false });
    cy.get('button[type="submit"]').click();

    cy.setCookie("fidpId", "choreoe2etest");

    this.persistCommonAuth();
    this.persistOrgs();
    this.persistApimToken();
    this.persistLogoutURL();
    this.persistCookies();

    cy.get('[data-testid="header-user-profile-menu"]', {
      timeout: 180000,
    }).should("be.visible");
    cy.url().then((url) => {
      if (url.includes("sample=true")) {
        const { handle } = Cypress.env("userData");
        const tmpURL = `${Cypress.env("baseUrl")}/organizations/${handle}/home`;
        cy.wait(5000);
        cy.visit(tmpURL);
      }
    });
  }

  private static persistLogoutURL() {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => {
        Cypress.env("sign_out_url", url);
      });
  }
  private static persistCookies() {
    cy.log("persistCookies()");
    cy.get('[alt="Choreo Logo"]', { timeout: 120000 });
    cy.request(`${Cypress.env("idpURL")}/commonauth`).then((res) => {
      const cookies = res.requestHeaders["cookie"].split(";");
      cookies.forEach((c) => {
        if (c.trim().includes("commonAuthId")) {
          const commonAuthId = c.replace("commonAuthId=", "").trim();
          Cypress.env(`commonAuthId`, commonAuthId);
          return;
        }
      });
    });
  }

  private static persistOrgs() {
    cy.intercept("GET", Cypress.env("appSvcURL") + "/validate-user").as("org");
    cy.wait("@org", { timeout: 180000 }).then((res) => {
      let userOrg;
      const handle = Cypress.env("choreoOrgHandle");
      if (handle) {
        userOrg = res.response.body.organizations.find(
          (o) => o.handle === handle
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
      const displayName = res.response.body.displayName;
      const userEmail = res.response.body.userEmail;
      const userData = {
        displayName: displayName,
        userEmail: userEmail,
        orgId: userOrg.id,
        handle: userOrg.handle,
        uuid: userOrg.uuid,
      };
      cy.log("userData: ", JSON.stringify(userData));
      Cypress.env("userData", userData);
    });
  }

  private static persistApimToken() {
    cy.intercept("GET", `${Cypress.env("appSvcURL")}/orgs/*`).as("orgs");
    cy.wait("@orgs", { timeout: 150000 }).then((intercept) => {
      const { orgId, handle } = Cypress.env("userData");
      const header = intercept.request.headers["authorization"] as string;
      const token = header.replace("Bearer", "").trim();
      Cypress.env("apim_token", token);
      GraphQL.deleteProjectsCreatedByTests(orgId, handle, token);
    });
  }

  private static persistCommonAuth() {
    if (Cypress.env("loginURL").includes("dev")) {
      // should remove after enabling sso in stg
      cy.intercept("GET", Cypress.env("asgardeoTokenURL")).as("asgCommonAuth");

      cy.wait("@asgCommonAuth", { timeout: 180000 }).then((intercept) => {
        const cookies = intercept.response.headers["set-cookie"] as string[];

        const asg_cookie = intercept.request.headers["cookie"] as string;
        const asg_cookies = asg_cookie.split(";");

        cookies.forEach((c) => {
          if (c.includes("opbs") || c.includes("commonAuthId")) {
            const obps = c.split(";")[0].replace("opbs=", "").trim();
            Cypress.env("asgardeo_opbs", obps);
          }
        });

        asg_cookies.forEach((c) => {
          if (c.includes("commonAuthId")) {
            const asgardeo_commonAuth = c.replace("commonAuthId=", "").trim();
            cy.log(`Asgardeo Common Auth ID :: ${asgardeo_commonAuth}`);
            Cypress.env("asgardeo_commonauth", asgardeo_commonAuth);
          }
        });
      });
    }
  }
}
