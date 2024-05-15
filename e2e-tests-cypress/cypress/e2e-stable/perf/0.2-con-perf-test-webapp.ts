import { GRAPHQL_URL } from "../../support/commons/urls";
import { Enums } from "../../support/commons/enums";
import { login } from "../../support/console/entities/login/login";
import {
  Project,
  RepoInfo,
  WebAppInfo,
} from "../../support/console/entities/project/project";
import { console } from "../../support/console/console";
import { WebApp } from "../../support/console/entities/component/webapp-component";
import { PERF_INTERCEPT_WAIT_TIME } from "../../support/commons/timeouts";
import { InterceptWriter } from "../../support/commons/interceptWriter";
import { Service } from "../../support/console/entities/component/service-component";
import { GraphQL } from "../../support/console/apis/graphql";

describe("Multiple User Logins", () => {
  let project: Project;
  let webApp: WebApp;
  let service: Service;
  let connectionUrl: string;
  const username = Cypress.env("perfUsername");
  let interceptWriter;
  const fixtureFileName = `${username}-intercept.json`;
  const filePath = `${fixtureFileName}`;
  const PROJECT_DESCRIPTION = "Cypress WebApp Perf Test Project";

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

  const BACKEND_SERVICE_PROJECT_NAME = "Default Project";
  const BACKEND_SERVICE_ENDPOINT_NAME = "Readinglist";
  const BACKEND_SERVICE_COMPONENT_NAME = "managedauthbackend";
  const BACKEND_CONNECTION_NAME = "Managed Auth BE Connection";

  const backendServiceRepoInfo: RepoInfo = {
    url: "https://github.com/choreo-test-apps/choreo-examples",
    branch: "main",
    subPath: "cloud-native-app-developer/reading-list-service",
  };

  function createComponentCallback(request: any, response: any): void {
    const interceptorName = "createComponentRequest";
    interceptWriter.interceptAndWriteToFixture(
      interceptorName,
      filePath,
      request,
      response
    );
  }

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
  }

  function logout() {
    cy.contains("button", "Logout").click();
  }

  function verifyLogout() {
    cy.contains("button", "Login").should("be.visible");
  }

  function registerApiIntercepts() {
    const urlRegex = new RegExp(`.*${connectionUrl}.*`);
    cy.intercept("GET", urlRegex).as("getReadingList");
  }

  function verifyApiCall() {
    cy.wait("@getReadingList").its("response.statusCode").should("eq", 200);
  }

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

  before(() => {
    cy.recordHar();
    interceptWriter = new InterceptWriter();
  });

  beforeEach(() => {
    cy.recordHar();
    username;
  });

  afterEach(function () {
    cy.saveHar();
    if (this.currentTest && this.currentTest.state === "failed") {
      const testName = this.currentTest.title;
      cy.screenshot(`failure_${testName}`);
    }
  });

  it(`Login with multiple users concurrently - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    login.perfLogin();
  });

  it(`Create default project if not exists - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    GraphQL.isProjectExists(BACKEND_SERVICE_PROJECT_NAME).then((isExists) => {
      if (!isExists) {
        console.createNewProject(BACKEND_SERVICE_PROJECT_NAME);
      } else {
      }
      project = console.searchProject(BACKEND_SERVICE_PROJECT_NAME);
    });
  });

  it(`Create backend service if not exists - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    project
      .isComponentExists(BACKEND_SERVICE_COMPONENT_NAME)
      .then((isExists) => {
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
          service = new Service(
            BACKEND_SERVICE_COMPONENT_NAME,
            BACKEND_SERVICE_ENDPOINT_NAME
          );
        }
      });
  });

  it(`Build backend service if not built previously - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    service.isSuccessfulBuildExists().then((isExists) => {
      if (!isExists) {
        service.build();
      }
    });
  });

  it(`Deploy backend service once to access endpoint configurations - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    service.isDevDeploymentExists().then((isExists) => {
      if (!isExists) {
        service.deployPublicLevelAccessibility();
      }
    });
  });

  it(`Enable Pass User Context To Backend - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    service.enablePassUserContextToBackend();
  });

  it(`Deploying the component with Public level visibility - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: GRAPHQL_URL,
      },
      (req) => {
        const requestBodyString = JSON.stringify(req.body);
        if (requestBodyString.includes("deployDeploymentTrack")) {
          req.alias = "deployComponentRequest";
        }
      }
    );

    service.deployPublicLevelAccessibility();

    cy.wait("@deployComponentRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception).to.have.property("response");
          expect(interception.response?.statusCode).to.equal(200);
          const responseBody = interception.response?.body;
          expect(responseBody).to.have.property("data");

          expect(responseBody)
            .to.have.property("deploymentStatusV2")
            .that.equals("ACTIVE");

          const deploymentResponse = responseBody?.data?.deployDeploymentTrack;
          expect(deploymentResponse).to.equal("Sucessfully deployed");
        } catch (error: any) {
          cy.log(`Assertion error: ${error.message}`);
          interceptWriter.interceptAndWriteToFixture(
            "deployComponentRequest",
            filePath,
            interception.request,
            interception.response
          );
        }
      }
    );
  });

  it(`Add users for E2E tests - ${Cypress.env("perfUsername")}`, () => {
    console.addUserStore("users.csv", Enums.Environment.DEVELOPMENT);
  });

  it(`creating a project - ${Cypress.env("perfUsername")}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: GRAPHQL_URL,
      },
      (req) => {
        // Convert req.body to a string
        const requestBodyString = JSON.stringify(req.body);
        if (requestBodyString.includes("createProject")) {
          req.alias = "createProjectRequest";
        }
      }
    );

    project = console.createNewProject(PROJECT_DESCRIPTION);

    cy.wait("@createProjectRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception).to.have.property("response");
          expect(interception.response?.statusCode).to.equal(200);

          const responseBody = interception.response?.body;
          expect(responseBody).to.have.property("data");

          const createProject = responseBody?.data?.createProject;
          expect(createProject, "createProject is missing in the response").to
            .exist;
          expect(createProject)
            .to.have.property("region")
            .that.equals("US-CDP-2");
          expect(createProject).to.have.property("name");
        } catch (error: any) {
          // Log assertion errors and proceed
          cy.log(`Assertion error: ${error.message}`);
          interceptWriter.interceptAndWriteToFixture(
            "createProjectRequest",
            filePath,
            interception.request,
            interception.response
          );
        }
      }
    );
  });

  it(`Creating a Web App - ${Cypress.env("perfUsername")}`, () => {
    try {
      project
        .createWebAppComponent(
          Enums.Accessibility.EXTERNAL,
          repoInfo,
          webAppInfo
        )

        .then((app: WebApp) => {
          project.visitComponent(app.getName());
          webApp = app;
        });
    } catch (error: any) {
      cy.log("Webapp Component creation failed: " + error.message);
    }
  });

  it(`Create a connection to backend service - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    webApp.createConnection(
      BACKEND_SERVICE_COMPONENT_NAME,
      BACKEND_CONNECTION_NAME
    );
    webApp.copyConnectionUrl(BACKEND_CONNECTION_NAME).then((url: string) => {
      connectionUrl = url;
    });
  });

  it(`Build the web app component - ${Cypress.env("perfUsername")}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: GRAPHQL_URL,
      },
      (req) => {
        const requestBodyString = JSON.stringify(req.body);
        if (requestBodyString.includes("deployComponent")) {
          req.alias = "buildComponentRequest";
        }
      }
    );

    webApp.build();
    cy.wait("@buildComponentRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception).to.have.property("response");
          expect(interception.response?.statusCode).to.equal(200);
          const responseBody = interception.response?.body;
          expect(responseBody).to.have.property("data");

          const deploymentStatuses =
            responseBody?.data?.deploymentStatusByVersion;
          expect(
            deploymentStatuses,
            "deploymentStatuses is missing or empty"
          ).to.exist.and.to.be.an("array").and.not.empty;

          deploymentStatuses.forEach((status) => {
            expect(status).to.have.property("status").that.equals("completed");
            expect(status)
              .to.have.property("conclusion")
              .that.equals("success");
          });
        } catch (error: any) {
          cy.log(`Assertion error: ${error.message}`);
          interceptWriter.interceptAndWriteToFixture(
            "buildComponentRequest",
            filePath,
            interception.request,
            interception.response
          );
        }
      }
    );
  });

  it(`Deploying webapp to dev - ${Cypress.env("perfUsername")}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: GRAPHQL_URL,
      },
      (req) => {
        const requestBodyString = JSON.stringify(req.body);
        if (requestBodyString.includes("deployDeploymentTrack")) {
          req.alias = "deployComponentRequest";
        }
      }
    );

    const customConfig = new Map<string, string>();
    customConfig.set("apiUrl", connectionUrl);
    webApp.deployToDevWithAuthConfiguration();

    cy.wait("@deployComponentRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception).to.have.property("response");
          expect(interception.response?.statusCode).to.equal(200);

          const responseBody = interception.response?.body;
          expect(responseBody).to.have.property("data");

          const deploymentResponse = responseBody?.data?.deployDeploymentTrack;
          expect(deploymentResponse).to.equal("Sucessfully deployed");
        } catch (error: any) {
          cy.log(`Assertion error: ${error.message}`);
          interceptWriter.interceptAndWriteToFixture(
            "deployComponentRequest",
            filePath,
            interception.request,
            interception.response
          );
        }
      }
    );
  });

  it(`Verify web app functionality in Dev - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    verifyWebAppFunctionality(webApp.getDevWebAppUrl());
  });
});
