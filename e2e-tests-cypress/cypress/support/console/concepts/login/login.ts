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

  private displayName: string;
  private userEmail: string;
  private orgId: number;
  private orgHandle: string;
  private orgUuid: string;
  private accessToken: string;
  private signOutUrl: string;

  login() {
    this.setBrowserLocalStorage();
    this.setBrowserCookie();
    this.registerNetworkCallsForInterception();
    this.enterUserCredentials("choreoIDPUsername", "choreoIDPPassword");
    this.persistOrgs();
    this.persistLogoutURL();
    this.persistAccessToken();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.userProfile, MEDIUM_TIME).should("be.visible");
    this.handleTermsOfUse();
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

  private enterUserCredentials(envUsername: string, envPassword: string) {
    cy.visit(Cypress.env("loginURL"));
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        cy.log(`URL after login page load: ${url}`);
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cy.get('button[type="submit"]').should("be.visible", MEDIUM_TIME);
          cy.get("#usernameUserInput").type(Cypress.env(envUsername));
          cy.get(Login.password).type(Cypress.env(envPassword), {
            log: false,
          });
          cy.get('button[type="submit"]').click();
        }
      });
  }

  private registerNetworkCallsForInterception() {
    cy.intercept("GET", VALIDATE_USER_URL).as("org");
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1,
    }).as("gql");
  }

  private persistOrgs() {
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
    if (Utils.isKubeConFeaturesEnabled()) {
      window.localStorage.setItem(
        "features",
        JSON.stringify({
          "API Configuration": false,
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
      .then((url) => (this.signOutUrl = url));
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
