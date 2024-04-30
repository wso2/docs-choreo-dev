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
import { Service } from "../../../support/console/entities/component/service-component";
import {
  Project,
  RepoInfo,
  WebAppInfo,
} from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";


describe("Create Web App", () => {
  const WEB_APP_PROJECT_DESCRIPTION = "Web App";

  const webAppRepoInfo: RepoInfo = {
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

  const BACKEND_SERVICE_PROJECT_NAME = "Default Project";
  const BACKEND_SERVICE_ENDPOINT_NAME = "Readinglist";
  const BACKEND_SERVICE_COMPONENT_NAME = "managedauthbackend";
  const BACKEND_CONNECTION_NAME = "Managed Auth BE Connection";

  const backendServiceRepoInfo: RepoInfo = {
    url: "https://github.com/choreo-test-apps/choreo-examples",
    branch: "main",
    subPath: "cloud-native-app-developer/reading-list-service",
  };

  let project: Project;
  let webApp: WebApp;
  let service: Service;
  let connectionUrl: string;

  /**
   *
   * The following functions are specific to the particular web app used in this spec.
   * Hence they are not implemented in a reusable way & are maintained at spec level.
   */
  function visitSampleWebsite(url: string) {
    cy.origin(url, () => {
      cy.visit("/");
    });
  }

  function clickLoginButton() {
    cy.contains("button", "Login").click();
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

  function verifyLogin() {
    cy.get("[data-cyid=welcome-msg-box]").contains("john1@acme.org");
  };

  function logout() {
    cy.contains("button", "Logout").click();
  };

  function verifyLogout() {
    cy.contains("button", "Login").should("be.visible");
  };

  function registerApiIntercepts() {
    const urlRegex = new RegExp(`.*${connectionUrl}.*`);
    cy.intercept("GET", urlRegex).as("getReadingList");
  };

  function verifyApiCall() {
    cy.wait("@getReadingList").its("response.statusCode").should("eq", 200);
  };

  function verifyWebAppFunctionality(url: string) {
    registerApiIntercepts();
    visitSampleWebsite(url);
    clickLoginButton();
    submitLoginCredentials();
    verifyLogin();
    verifyApiCall();
    logout();
    verifyLogout();
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Search backend service project", () => {
    project = console.searchProject(BACKEND_SERVICE_PROJECT_NAME);
  });

  it("Create backend service if not exists", () => {
    project.isComponentExists(BACKEND_SERVICE_COMPONENT_NAME).then((isExists) => {
      if (!isExists) {
        project
          .createServiceComponent(
            Enums.Accessibility.EXTERNAL,
            backendServiceRepoInfo,
            BACKEND_SERVICE_ENDPOINT_NAME,
            BACKEND_SERVICE_COMPONENT_NAME
          )
          .then((serviceComponent: Service) => {
            project.visitComponent(BACKEND_SERVICE_COMPONENT_NAME);
            service = serviceComponent;
          });
      } else {
        project.visitComponent(BACKEND_SERVICE_COMPONENT_NAME);
        service = new Service(BACKEND_SERVICE_COMPONENT_NAME, BACKEND_SERVICE_ENDPOINT_NAME);
      }
    });
  });

  it("Build backend service if not built previously", () => {
    service.isSuccessfulBuildExists().then((isExists) => {
      if (!isExists) {
        service.build();
      }
    });
  });

  it("Deploy backend service once to access endpoint configurations", () => {
    // we have to deploy once to access endpoint configurations
    service.isDevDeploymentExists().then((isExists) => {
      if (!isExists) {
        service.deployPublicLevelAccessibility(); 
      }
    });
  })

  it("Enable Pass User Context To Backend", () => {
      service.enablePassUserContextToBackend();
  });

  it("Deploy backend service to Dev", () => {
    service.deployPublicLevelAccessibility();
  });

  it("Promote backend service to Prod", () => {
    service.promotePublicLevelAccessibility();
  });

  it("Add users for E2E tests", () => {
    console.addUserStore("users.csv", Enums.Environment.DEVELOPMENT);
    console.addUserStore("users.csv", Enums.Environment.PRODUCTION);
  });

  it("Creating a project", () => {
    project = console.createNewProject(WEB_APP_PROJECT_DESCRIPTION);
  });

  it("Creating a Web App", () => {
    project
      .createWebAppComponent(Enums.Accessibility.EXTERNAL, webAppRepoInfo, webAppInfo)
      .then((app: WebApp) => {
        project.visitComponent(app.getName());
        webApp = app;
      });
  });

  it("Create a connection to backend service", () => {
    webApp.createConnection(BACKEND_SERVICE_COMPONENT_NAME, BACKEND_CONNECTION_NAME);
    webApp.copyConnectionUrl(BACKEND_CONNECTION_NAME).then((url: string) => {
      connectionUrl = url;
    });
  });

  it("Build the Web App", () => {
    webApp.build();
  });

  it("Deploying to Dev", () => {
    const customConfig = new Map<string, string>();
    customConfig.set("apiUrl", connectionUrl);
    webApp.deployToDevWithAuthConfiguration(customConfig);
  });

  it("Promote to Prod", () => {
    const customConfig = new Map<string, string>();
    customConfig.set("apiUrl", connectionUrl);
    webApp.promoteToProdWithAuthConfiguration(customConfig);
  });

  it("Verify test page is disabled", () => {
    webApp.verifyTestPageIsDisabled();
  });

  it("Verify manage page is disabled", () => {
    webApp.verifyManagePageIsDisabled();
  });

  it("Verify web app functionality in Dev", () => {
    verifyWebAppFunctionality(webApp.getDevWebAppUrl());
  });

  it("Verify web app functionality in Prod", () => {
    verifyWebAppFunctionality(webApp.getProdWebAppUrl());
  });
});
