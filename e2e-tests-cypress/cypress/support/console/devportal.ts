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

import { TestIds } from "./constants/TestIds";
import { SHORT_TIME, VERY_SHORT_TIME } from "../commons/timeouts";
import { DEV_PORTAL_APIS_SEARCH_URL } from "../commons/urls";
import { UserDetails } from "../commons/types";
import { SELF_SIGNUP_REQUEST_PENDING_USER_MSG, SELF_SIGNUP_REQUEST_REJECTED_USER_MSG } from "../devportal/constants";

export class DevPortal {
  loginToDevPortal(devPortalUrl?: string): void {
    const resourcePath = `/${Cypress.env(
      "choreoOrgHandle"
    )}?fidp=choreoe2etest`;

    let loginURL = Cypress.env("devportalLoginURL") + resourcePath;

    if (devPortalUrl !== undefined) {
      loginURL = devPortalUrl + resourcePath;
    }

    cy.visit(loginURL);
    cy.wait(3000)
      .url(SHORT_TIME)
      .then((url) => {
        if (url.includes(Cypress.env("idpURL") + "/authenticationendpoint")) {
          cy.get('button[type="submit"]', VERY_SHORT_TIME).should("be.visible");
          cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
          cy.get("#password").type(Cypress.env("choreoIDPPassword"), {
            log: false,
          });
          cy.get('button[type="submit"]').click();
        }
      });
    cy.get(TestIds.devPortalHome, VERY_SHORT_TIME).should("be.visible");
  }

  selfSignupToDevPortal(userDetails: UserDetails, devPortalUrl?: string) {
    const resourcePath = `/${Cypress.env("selfSignupOrgHandle")}`;
    let loginURL = Cypress.env("devportalLoginURL") + resourcePath;
    if (devPortalUrl !== undefined) {
      loginURL = devPortalUrl + resourcePath;
    }
    cy.visit(loginURL).then(() => {
      cy.get(TestIds.backdropLoader).should("not.exist");
      cy.get('body').then($body => {
        const loginLinkExists = $body.find(TestIds.devPortalLoginLink).length > 0;
        if (loginLinkExists) {
          cy.get(TestIds.devPortalLoginLink)
            .should('be.visible')
            .click();
        }
      });
    });
    cy.get(TestIds.devPortalRegisterLink).should("be.visible");
    cy.get(TestIds.devPortalRegisterLink).click();
    cy.get(TestIds.devPortalRegistrationSubmitButton).should("be.visible");
    cy.get(TestIds.devPortalRegisterPageUsernameInput).type(userDetails.email)
    cy.get(TestIds.devPortalRegisterPagePasswordInput).type(userDetails.password, {
      log: false,
    })
    cy.get(TestIds.devPortalRegisterPageFirstNameInput).type(userDetails.firstName)
    cy.get(TestIds.devPortalRegisterPageLastNameInput).type(userDetails.lastName)
    cy.get(TestIds.devPortalRegistrationSubmitButton).click();
  }

  checkDevPortalAccessForPendingUser() {
    cy.wait(5000);
    cy.contains('p', SELF_SIGNUP_REQUEST_PENDING_USER_MSG)
      .should('be.visible');
  }

  checkDevPortalAccessForRejectedUser() {
    cy.wait(5000);
    cy.contains('p', SELF_SIGNUP_REQUEST_REJECTED_USER_MSG)
      .should('be.visible');
  }

  checkDevPortalAccessForApprovedUser() {
    cy.wait(5000);
    cy.get(TestIds.devPortalHome, VERY_SHORT_TIME).should("be.visible");
  }

  signInToDevPortalWithApprovedUser(userDetails: UserDetails, devPortalUrl?: string) {
    const resourcePath = `/${Cypress.env("selfSignupOrgHandle")}`;
    let loginURL = Cypress.env("devportalLoginURL") + resourcePath;
    if (devPortalUrl !== undefined) {
      loginURL = devPortalUrl + resourcePath;
    }
    cy.visit(loginURL);
    cy.wait(5000);
    cy.get(TestIds.devPortalLoginPageContinueLoginButton).should("be.visible");
    cy.get(TestIds.devPortalLoginPageUsernameInput).type(userDetails.email)
    cy.get(TestIds.devPortalLoginPagePasswordInput).type(userDetails.password, {
      log: false,
    })
    cy.get(TestIds.devPortalLoginPageContinueLoginButton).click();
    this.checkDevPortalAccessForApprovedUser();
  }

  signInToDevPortalWithRejectedUser(userDetails: UserDetails, devPortalUrl?: string) {
    const resourcePath = `/${Cypress.env("selfSignupOrgHandle")}`;
    let loginURL = Cypress.env("devportalLoginURL") + resourcePath;
    if (devPortalUrl !== undefined) {
      loginURL = devPortalUrl + resourcePath;
    }
    cy.visit(loginURL);
    cy.wait(5000);
    cy.get(TestIds.devPortalLoginPageContinueLoginButton).should("be.visible");
    cy.get(TestIds.devPortalLoginPageUsernameInput).type(userDetails.email)
    cy.get(TestIds.devPortalLoginPagePasswordInput).type(userDetails.password, {
      log: false,
    })
    cy.get(TestIds.devPortalLoginPageContinueLoginButton).click();
    this.checkDevPortalAccessForRejectedUser();
  }

  searchApi(name: string, version?: string) {
    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(),
      times: 1,
    }).as("searchAllApis");

    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(name),
      times: 1,
    }).as("searchApi");

    cy.get(TestIds.apiBar).click();

    cy.wait("@searchAllApis", SHORT_TIME).then(() => {
      cy.get(TestIds.apiSearch)
        .should("be.visible")
        .focus()
        .type(`${name}{enter}`);
    });

    cy.wait("@searchApi", VERY_SHORT_TIME).then(() => {
      if (version !== undefined) {
        cy.get(TestIds.apiCard(name), VERY_SHORT_TIME)
          .should("be.visible")
          .contains(`Version : ${version}`)
          .click();
      } else {
        cy.get(TestIds.apiCard(name), VERY_SHORT_TIME)
          .should("be.visible")
          .click();
      }

      // Ensure API Overview page is loaded
      cy.get(TestIds.apiOverviewDevPortal).should("be.visible");
      cy.get(TestIds.apiNameDevPortal).contains(name).should("be.visible");
    });
  }

  verifyApiNotFound(name: string) {
    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(),
      times: 1,
    }).as("searchAllApis");

    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(name),
      times: 1,
    }).as("searchApi");

    cy.get(TestIds.apiBar).click();

    cy.wait("@searchAllApis", SHORT_TIME).then(() => {
      cy.get(TestIds.apiSearch)
        .should("be.visible")
        .focus()
        .type(`${name}{enter}`);
    });

    cy.wait("@searchApi", VERY_SHORT_TIME).then(() => {
      cy.get(TestIds.apiCard(name)).should("not.exist");
    });
  }
}

// Singleton instance of Choreo Dev Portal
export const devPortal = new DevPortal();
