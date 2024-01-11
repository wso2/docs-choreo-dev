/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums } from "../../../support/commons/enums";
import { WebApp } from "../../../support/console/entities/component/webapp-component";
import {
  Project,
  RepoInfo,
  WebAppInfo,
} from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";

describe("Create Web App", () => {
  const PROJECT_DESCRIPTION = "Web App";

  const repoInfo: RepoInfo = {
    url: "https://github.com/choreo-test-apps/choreo-examples",
    branch: "main",
    dockerContext:
      "cloud-native-app-developer/reading-list-front-end-with-managed-auth",
  };

  const webAppInfo: WebAppInfo = {
    webAppType: "React",
    webAppBuildCommand: "npm install && npm run build",
    webAppPackageManagerVersion: "18",
    webAppOutputDirectory: "dist",
  };

  let project: Project;
  let webApp: WebApp;

  /**
   *
   * The following functions are specific to the particular web app used in this spec.
   * Hence they are not implemented in a reusable way & are maintained at spec level.
   */
  function visitSampleWebsite(url: string) {
    cy.origin(url, () => {
      cy.visit("/");
      cy.contains("button", "Login").click();
    });
  }

  function submitLoginCredentials() {
    cy.url().then((url) => {
      let uri = new URL(url.toString());
      let hostname = uri.hostname;
      cy.origin(hostname, () => {
        cy.get('input[id="username"]').should("be.visible");
        cy.get('input[id="username"]').type("e2e-webapp-user");
        cy.get('input[id="password"]').type("acme2@123");
        cy.contains("button", "Sign In").click();
      });
    });
  }

  function verifyLoginAndLogout(url: string) {
    cy.origin(url, () => {
      cy.get("[data-cyid=welcome-msg-box]").contains("john1@acme.org");
      cy.contains("button", "Logout").click();
    });
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Adding users for E2E tests", () => {
    console.addUserStore("users.csv", Enums.Environment.DEVELOPMENT);
    console.addUserStore("users.csv", Enums.Environment.PRODUCTION);
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a Web App", () => {
    project
      .createWebAppComponent(Enums.Accessibility.EXTERNAL, repoInfo, webAppInfo)
      .then((app: WebApp) => {
        webApp = app;
      });
  });

  it("Build the Web App", () => {
    webApp.build();
  });

  it("Deploying to Dev", () => {
    webApp.deployToDevWithAuthConfiguration();
  });

  it("Promote to Prod", () => {
    webApp.promoteToProdWithAuthConfiguration();
  });

  it("Verify test page is disabled", () => {
    webApp.verifyTestPageIsDisabled();
  });

  it("Verify manage page is disabled", () => {
    webApp.verifyManagePageIsDisabled();
  });

  it("Access webapp in Dev and login", () => {
    visitSampleWebsite(webApp.getDevWebAppUrl());
    submitLoginCredentials();
    verifyLoginAndLogout(webApp.getDevWebAppUrl());
  });

  it("Access webapp in Prod and login", () => {
    visitSampleWebsite(webApp.getProdWebAppUrl());
    submitLoginCredentials();
    verifyLoginAndLogout(webApp.getProdWebAppUrl());
  });
});
