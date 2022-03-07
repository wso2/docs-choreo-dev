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

import "cypress-file-upload";
import "cypress-xpath";
import { GraphQL } from "../support/console/apis/graphql";
import {ChoreoHomePage} from "../support/console/pages/home/home-page"

Cypress.on("uncaught:exception", (err, runnable) => {
  return false;
});

Cypress.on("window:confirm", (err, runnable) => {
  return true;
});

Cypress.on("window:alert", (err, runnable) => {
  return true;
});

Cypress.on("window:before:load", (win) => {
  Object.defineProperty(win, "onbeforeunload", {
    value: undefined,
    writable: false,
  });
});

before(() => {
  cy.visit(Cypress.env("loginURL"));
  cy.get('button[type="submit"]').should("be.visible", { timeout: 180000 });
  cy.get("#usernameUserInput").type(Cypress.env("choreoIDPUsername"));
  cy.get("#password").type(Cypress.env("choreoIDPPassword"), { log: false });

  cy.get('button[type="submit"]').click();
  persistOrgs();
  persistApimToken();
  persistLogoutURL();
  persistCookies();


});
after(()=>{
  ChoreoHomePage.logout()
})

const persistLogoutURL = () => {
  cy.window()
    .its("sessionStorage")
    .invoke("getItem", "sign_out_url")
    .then((url) => {
      Cypress.env("sign_out_url", url);
    });
};
const persistCookies = () => {
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
};

const persistOrgs = () => {
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
    };
    cy.log("userData: ", JSON.stringify(userData));
    Cypress.env("userData", userData);
  });
};

const persistApimToken = () => {
  cy.intercept("POST", `${appSvcURL}/graphql`).as(
    "gquery"
  );
  cy.wait("@gquery", { timeout: 150000 }).then((intercept) => {
    Cypress.env("apim_token", intercept.request.headers.authorization);
    const { orgId, handle } = Cypress.env("userData");
    const token = intercept.request.headers.authorization.replace('Bearer','').trim() 
    GraphQL.deleteProjectsCreatedByTests(orgId, handle, token);
  });
};
