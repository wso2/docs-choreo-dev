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
    this.enterUserCredentials("choreoIDPInvitedUsername", "choreoIDPInvitedPassword");
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

  static login() {
    this.enterUserCredentials("choreoIDPUsername", "choreoIDPPassword");
    this.persistOrgs();
    this.persistLogoutURL();
    this.persistApimToken();
    this.persistCookies(`${Cypress.env("idpURL")}/commonauth`);

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

  static reLoginToChoreo() {
    const componentURL = Cypress.env(`componentURL`);
    const common = Cypress.env(`commonAuthId`) != null ? Cypress.env(`commonAuthId`) : "authtoken";
    this.setBrowserCookie(false);
    this.setCookie(componentURL, "commonAuthId", common)
    cy.visit(componentURL);
  }

  static navigateToCodespaceEP() {
    const csurl = Cypress.env(`accessURL`);
    this.setBrowserCookie(true)
    cy.visit(csurl);
  }

  static navigateToCodespace() {
    const csurl = Cypress.env(`accessURL`);
    this.setBrowserCookie(false)
    cy.visit(csurl);
  }

  static enterpriseLogin() {
    this.setBrowserCookie(true)
    cy.visit(Cypress.env("enterpriseLoginUrl"));
    cy.get('button[id="enterprise-sign-in"]').should("be.visible", { timeout: 180000 });
    cy.get('button[id="enterprise-sign-in"]').click();
    cy.get("#outlined-basic").type(Cypress.env("enterpriseIDPUsername"));
    cy.contains("Continue").click();
    cy.get('input[id="username"]').should("be.visible", { timeout: 180000 });
    cy.get("#username").type(Cypress.env("enterpriseIDPUsername"));
    cy.get("#password").type(Cypress.env("enterpriseIDPPassword"), { log: false, });
    cy.contains("Continue").click();
    cy.get('[data-testid="header-user-profile-menu"]', { timeout: 180000, }).should("be.visible");
    this.persistLogoutURL();
  }

  private static persistLogoutURL() {
    cy.window().its("sessionStorage").invoke("getItem", "sign_out_url").then((url) => Cypress.env("sign_out_url", url));
  }

  private static persistCookies(url: string) {
    cy.log("persistCookies()");
    cy.get('[alt="Choreo Logo"]', { timeout: 120000 });
    cy.request(url).then((res) => {
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

  static persistApimToken() {
    cy.intercept("GET", `${Cypress.env("appSvcURL")}/orgs/*`).as("orgs");
    cy.wait("@orgs", { timeout: 150000 }).then((intercept) => {
      const header = intercept.request.headers["authorization"] as string;
      const token = header.replace("Bearer", "").trim();
      const { id, uuid, handle } = intercept.response.body.organization;
      const current_org = { id, uuid, handle };
      Cypress.env("apim_token", token);
      Cypress.env("current_org", current_org);
      GraphQL.deleteProjectsCreatedByTests(id, handle, token);
    });
  }


  private static enterUserCredentials(envUsername: string, envPassword: string) {
    this.setBrowserCookie(false);
    cy.visit(Cypress.env("loginURL"));
    cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
    cy.get("#usernameUserInput").type(Cypress.env(envUsername));
    cy.get("#password").type(Cypress.env(envPassword), { log: false });
    cy.get('button[type="submit"]').click();
  }

  private static setCookie(url: string, cookieKey: string, cookieValue: string) {
    cy.intercept(url).then(() => {
      cy.setCookie(cookieKey, cookieValue, {
        path: "/",
        domain: "id.dv.choreo.dev",
        secure: true,
        httpOnly: true,
        sameSite: "no_restriction",
      });
    });
  }

  private static setBrowserCookie(isEPLogin: boolean) {
    const dateString = new Date().toISOString();
    if (isEPLogin) {
      cy.setCookie("fidpId", "EnterpriseIDP")
    }
    cy.setCookie("fidpId", "choreoe2etest")
    cy.setCookie("OptanonAlertBoxClosed", dateString)
  }
}

