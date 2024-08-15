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

import { MEDIUM_TIME, SHORT_TIME } from "../../../commons/timeouts";
import { GRAPHQL_URL, VALIDATE_USER_URL } from "../../../commons/urls";
import { Utils } from "../../../commons/utils";
import { TestIds } from "../../constants/TestIds";

class Login {
  static username = "#username";
  static password = "#password";

  static enterpriseLogoutUrl = Cypress.env("auth0LogoutUrl");
  static enterpriseClientId = Cypress.env("auth0ClientID");
  static enterpriseLoginUrl = Cypress.env("enterpriseLoginUrl");

  private displayName: string = "";
  private userEmail: string = "";
  private orgId: number = 0;
  private orgHandle: string = "";
  private orgUuid: string = "";
  private accessToken: string = "";
  private signOutUrl: string = "";

  login() {
    this.setBrowserLocalStorage();
    this.setBrowserCookie();
    this.registerNetworkCallsForInterception();
    const { username, password } = this.readUserCredentialsFromEnv();
    this.enterUserCredentials(username, password);
    const handle = this.readConfiguredOrgHandle();
    this.persistOrgs(handle);
    this.persistLogoutURL();
    this.persistAccessToken();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.userProfile, MEDIUM_TIME).should("be.visible");
    this.handleTermsOfUse();
  }

  selfSignupOrgAdminlogin() {
    this.setBrowserLocalStorage();
    this.setBrowserCookie();
    this.registerNetworkCallsForInterception();
    const { username, password } = this.readSelfSignupAdminUserCredentialsFromEnv();
    this.enterUserCredentials(username, password);
    const handle = this.readConfiguredSelfSignupAdminUserOrgHandle();
    this.persistOrgs(handle);
    this.persistLogoutURL();
    this.persistAccessToken();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.userProfile, MEDIUM_TIME).should("be.visible");
    this.handleTermsOfUse();
  }

  enterpriseLogin() {
    this.setBrowserLocalStorage();
    this.setBrowserCookie();
    this.logoutOfPreviousEnterpriseSession();
    this.enterEnterpriseUserCredentials();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.userProfile, MEDIUM_TIME).should("be.visible");
    this.persistLogoutURL();
    this.handleTermsOfUse();
  }

  perfLogin() {
    const userName = Cypress.env("perfUsername");
    cy.log(`User: ${userName}`);
    this.setBrowserLocalStorage();
    this.setBrowserCookie();
    this.registerNetworkCallsForInterception();
    cy.visit(Cypress.env("baseUrl"));
    cy.get("[data-cyid=email-sign-in-button]")
      .should("be.visible", SHORT_TIME)
      .click();
    cy.get("#usernameUserInput").type(userName);
    cy.contains("Continue").click();
    cy.get("#password").type(Cypress.env("perfPassword"));
    cy.get("#sign-in-button").click();
    this.persistPerfOrgs();
    this.persistAccessToken();
    this.selectRegion();
    this.handleTermsOfUse();
    cy.wait(25000);
  }

  private selectRegion(retryCount: number = 0) {
    retryCount++;
    if (retryCount > 3) {
      return;
    }

    cy.get("body").then((body) => {
      if (
        body.find("[data-cyid=consent-code-challenge-check-box]").length > 0
      ) {
        cy.get("[data-cyid=consent-code-challenge-check-box]")
          .should("be.visible")
          .click();
        cy.get("[data-cyid=create-default-project-confirm-button]")
          .should("be.visible")
          .click();
      } else {
        cy.wait(5000);
        cy.log("Retry count: " + retryCount);
        this.selectRegion(retryCount);
      }
    });
  }

  getDisplayName() {
    return this.displayName;
  }

  getUserEmail() {
    return this.userEmail;
  }

  getOrgId() {
    return this.orgId;
  }

  getOrgHandle() {
    return this.orgHandle;
  }

  getOrgUuid() {
    return this.orgUuid;
  }

  getAccessToken() {
    return this.accessToken;
  }

  getSignOutUrl() {
    return this.signOutUrl;
  }

  private readUserCredentialsFromEnv(): { username: string; password: string; } {
    const username = Cypress.env("choreoIDPUsername");
    const password = Cypress.env("choreoIDPPassword");

    if (username === undefined || password === undefined) {
      throw new Error("Credentials have not been defined correctly");
    }

    return {  username, password };
  }

  private readSelfSignupAdminUserCredentialsFromEnv(): { username: string; password: string; } {
    let username = Cypress.env("choreoSelfSignupAdminIDPUsername");
    let password = Cypress.env("choreoSelfSignupAdminIDPPassword");

    if (username === undefined || password === undefined) {
      cy.log("Self signup admin credentials not defined separately. Using default credentials");

      username = Cypress.env("choreoIDPUsername");
      password = Cypress.env("choreoIDPPassword");

      if (username === undefined || password === undefined) {
        throw new Error("Credentials have not been defined correctly");
      }
    }

    return {  username, password };
  }

  private enterUserCredentials(username: string, password: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        cy.log(`URL after login page load: ${url}`);
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cy.get('button[type="submit"]').should("be.visible", MEDIUM_TIME);
          cy.get("#usernameUserInput").type(username);
          cy.get(Login.password).type(password, {
            log: false,
          });
          cy.get('button[type="submit"]').click();
        }
      });
  }

  private logoutOfPreviousEnterpriseSession() {
    cy.request(Login.enterpriseLogoutUrl, {
      client_id: Login.enterpriseClientId,
      returnTo: Login.enterpriseLoginUrl,
    });
  }

  private enterEnterpriseUserCredentials() {
    const signInButton = 'button[id="enterprise-sign-in"]';
    cy.visit(Login.enterpriseLoginUrl);
    cy.get(signInButton).should("be.visible", MEDIUM_TIME);
    cy.get(signInButton).click();
    cy.get("[data-cyid=sign-in-with-enterprise]").type(
      Cypress.env("enterpriseIDPUsername")
    );
    cy.contains("Continue").click();

    cy.get('input[id="username"]').should("be.visible", MEDIUM_TIME);
    cy.get(Login.username).type(Cypress.env("enterpriseIDPUsername"));
    cy.get(Login.password).type(Cypress.env("enterpriseIDPPassword"), {
      log: false,
    });
    cy.contains("Continue").click({ force: true });
  }

  private registerNetworkCallsForInterception() {
    cy.intercept("GET", VALIDATE_USER_URL).as("org");
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1,
    }).as("gql");
  }

  private readConfiguredOrgHandle() : string | undefined {
    return Cypress.env("choreoOrgHandle");
  }

  private readConfiguredSelfSignupAdminUserOrgHandle() : string | undefined {
    const orgHandle = Cypress.env("choreoSelfSignupAdminOrgHandle");

    if (orgHandle === undefined) {
      return this.readConfiguredOrgHandle();
    }

    return orgHandle;
  }

  private persistOrgs(handle: string | undefined) {
    cy.wait("@org", MEDIUM_TIME).then((res) => {
      if (res.response === undefined) {
        throw new Error("Failed to receive orgs response");
      }

      let userOrg;

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
      this.displayName = res.response.body.displayName;
      this.userEmail = res.response.body.userEmail;
      this.orgId = userOrg.id;
      this.orgHandle = userOrg.handle;
      this.orgUuid = userOrg.uuid;
    });
  }

  private persistPerfOrgs() {
    cy.wait("@org", MEDIUM_TIME).then((res) => {
      if (!res.response) {
        throw new Error("Failed to receive orgs response");
      }

      const userOrg = res.response.body.organizations[0];

      cy.log(`First available org ${userOrg.handle} selected`);

      this.displayName = res.response.body.displayName;
      this.userEmail = res.response.body.userEmail;
      this.orgId = userOrg.id;
      this.orgHandle = userOrg.handle;
      this.orgUuid = userOrg.uuid;
    });
  }

  private persistAccessToken() {
    cy.wait("@gql", MEDIUM_TIME).then((intercept) => {
      const header = intercept.request.headers["authorization"] as string;
      this.accessToken = header.replace("Bearer", "").trim();
    });
  }

  private setBrowserCookie() {
    const dateString = new Date().toISOString();
    document.cookie = `OptanonAlertBoxClosed=${dateString};SameSite=Lax;Secure`;
    cy.setCookie("OptanonAlertBoxClosed", dateString);
  }

  private setBrowserLocalStorage() {
    window.localStorage.setItem("seen", Date.now().toString());
    if (Utils.isApiConfigurationEnabled()) {
      window.localStorage.setItem(
        "features",
        JSON.stringify({
          "API Configuration": true,
          "Mono Repository": true,
          "Buildpack - Component Creation": true,
          "Project Architecture Diagram": false,
          "Test Runner Component Type": true,
          "Choreo built-in IdP": true,
          "Demo Organization": true,
          "Internal Marketplace": false,
          "Internal Endpoint Testing": true,
          "Decouple Build and Deploy": false,
          "Innovation Performance": false,
          "Choreo built-in Identity Provider": false,
          "Connection Management": false,
          "App Gateway Authentication": false,
          "App Gateway Settings": false,
          "Role Group Mapping": false,
          "Async Component Creation": false,
        })
      );
    } else {
      window.localStorage.removeItem("features");
    }
  }

  private persistLogoutURL() {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => {
        if (url === undefined || url === null) {
          throw new Error("Failed to retrieve sign out URL");
        }
        this.signOutUrl = url;
      });
  }

  private handleTermsOfUse() {
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
}

// Singleton instance of a Login, because only one login can exist at a time
export const login = new Login();
