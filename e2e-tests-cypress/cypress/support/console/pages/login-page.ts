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

import { cyGet } from "../../commons/cy";
import { AUTH_HEADER, OK } from "../../commons/http";
import { MEDIUM_TIME, SHORT_TIME } from "../../commons/timeouts";
import {
  EP_USER_HOME_URL,
  VALIDATE_USER_URL,
  GRAPHQL_URL,
} from "../../commons/urls";
import { Utils } from "../../commons/utils";
import { GraphQL } from "../apis/graphql";
import { OnPremKeyService } from "../apis/on-prem-key-service";

export class LoginPage {
  static username = "#username";
  static password = "#password";

  static acceptInviteAsInvitedUser(timestamp: string) {
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1,
    }).as("token");

    this.enterUserCredentials(
      "choreoIDPInvitedUsername",
      "choreoIDPInvitedPassword"
    );
    cy.wait("@token", MEDIUM_TIME).then((intercept) => {
      const header = intercept.request.headers["authorization"] as string;
      const token = header.replace("Bearer", "").trim();
      Utils.acceptEmailInviteToOrg(token, timestamp);
      cy.reload(); // Reload in order to get updated orgs
    });
  }

  static login(doCleanup: boolean = false) {
    window.localStorage.setItem("seen", Date.now().toString());
    Utils.setBrowserCookie();
    this.registerNetworkCallsForInterception();
    this.enterUserCredentials("choreoIDPUsername", "choreoIDPPassword");
    this.persistOrgs();
    this.persistLogoutURL();
    this.persistApimToken(doCleanup);
    this.persistCookies(`${Cypress.env("idpURL")}/commonauth`);
    cy.get('[data-testid="header-user-profile-menu"]', MEDIUM_TIME).should(
      "be.visible"
    );
    cy.get('[id="backdrop-loader"]').should("not.exist");
    this.handleTermsOfUse();
  }

  private static handleTermsOfUse() {
    for (let i = 0; i < 5; i++) {
      cy.get("body", { log: false }).then((body) => {
        if (body.find('[data-testid="Welcome to Choreo!"]').length > 0) {
          cy.get(
            '[data-cyid="confirmation-dialog-primary-action-button"]'
          ).click();
          cy.get(
            '[data-cyid="confirmation-dialog-primary-action-button"]'
          ).should("not.exist");
          return;
        } else {
          cy.wait(1000, { log: false });
        }
      });
    }
  }

  private static rejectCookies() {
    const handler = "#onetrust-reject-all-handler";
    cy.wait(5000);
    cy.get("body").then((b) => {
      if (b.find(handler).length > 0) {
        cy.wrap(handler).click();
      }
    });
  }
  static reLoginToChoreo(isEPLogin: boolean = false) {
    let componentURL;
    if (isEPLogin) {
      componentURL = EP_USER_HOME_URL;
    } else {
      componentURL = Cypress.env("componentURL");
    }
    const common =
      Cypress.env(`commonAuthId`) != null
        ? Cypress.env(`commonAuthId`)
        : "authtoken";
    window.localStorage.setItem("seen", Date.now().toString());
    Utils.setBrowserCookie();
    this.setCookie(componentURL, "commonAuthId", common);
    cy.visit(componentURL);
    this.rejectCookies();
    cyGet('[data-testid="header-user-profile-menu"]').should("be.visible");
    cy.get('[id="backdrop-loader"]').should("not.exist");
    this.handleTermsOfUse();
  }

  static enterpriseLogin() {
    const signInButton = 'button[id="enterprise-sign-in"]';
    window.localStorage.setItem("seen", Date.now().toString());
    Utils.setBrowserCookie();
    cy.visit(Cypress.env("enterpriseLoginUrl"));
    cy.get(signInButton).should("be.visible", MEDIUM_TIME);
    cy.get(signInButton).click();
    cy.get("[data-cyid=sign-in-with-enterprise]").type(Cypress.env("enterpriseIDPUsername"));
    cy.contains("Continue").click();

    cy.get('input[id="username"]').should("be.visible", MEDIUM_TIME);
    cy.get(LoginPage.username).type(Cypress.env("enterpriseIDPUsername"));
    cy.get(LoginPage.password).type(Cypress.env("enterpriseIDPPassword"), {
      log: false,
    });
    cy.contains("Continue").click({ force: true });
    cy.get('[data-testid="header-user-profile-menu"]', MEDIUM_TIME).should(
      "be.visible"
    );
    this.persistLogoutURL();
  }

  private static persistLogoutURL() {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => Cypress.env("sign_out_url", url));
  }

  private static persistCookies(url: string) {
    cy.log("persistCookies()");
    cy.get('[alt="Choreo Logo"]', MEDIUM_TIME);
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

  private static registerNetworkCallsForInterception() {
    cy.intercept("GET", VALIDATE_USER_URL).as("org");
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1,
    }).as("gql");
  }

  private static persistOrgs() {
    cy.wait("@org", MEDIUM_TIME).then((res) => {
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
        displayName,
        userEmail,
        orgId: userOrg.id,
        handle: userOrg.handle,
        uuid: userOrg.uuid,
      };
      Cypress.env("userData", userData);
    });
  }

  static persistApimToken(doCleanup: boolean = false) {
    cy.wait("@gql", MEDIUM_TIME).then((intercept) => {
      const header = intercept.request.headers["authorization"] as string;
      const token = header.replace("Bearer", "").trim();
      const { orgId, handle } = Cypress.env("userData");
      Cypress.env("apim_token", token);
      if (doCleanup) {
        GraphQL.deleteProjectsCreatedByTests(orgId, handle, token);
        this.deleteOnPremKeys();
      }
    });
  }

  static visitToHomePage() {
    Utils.setBrowserCookie();
    cy.visit(Cypress.env("loginURL"));
  }

  private static enterUserCredentials(
    envUsername: string,
    envPassword: string
  ) {
    Utils.setBrowserCookie();
    cy.visit(Cypress.env("loginURL"));
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        cy.log(`URL after login page load: ${url}`);
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cyGet('button[type="submit"]').should("be.visible", MEDIUM_TIME);
          cyGet("#usernameUserInput").type(Cypress.env(envUsername));
          cyGet(LoginPage.password).type(Cypress.env(envPassword), {
            log: false,
          });
          cyGet('button[type="submit"]').click();
        }
      });
  }

  private static setCookie(
    url: string,
    cookieKey: string,
    cookieValue: string
  ) {
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

  private static deleteOnPremKeys() {
    const { handle } = Cypress.env("userData");
    this.getOnPremKeys(handle, AUTH_HEADER()).then((keys) => {
      keys.forEach((key) => {
        Utils.sendPostRequest(
          OnPremKeyService.deleteOnPremKey(handle, key.handle),
          AUTH_HEADER(),
          ""
        );
      });
    });
  }

  private static getOnPremKeys(handle: string, header: any) {
    const url = OnPremKeyService.getOnPremKeys(handle);
    return Utils.sendGetRequest(url, header).then((res) => {
      if (res.status == OK) {
        return res.body as { handle: string }[];
      } else {
        throw new Error("Error While Getting On Prem Keys");
      }
    });
  }
}
