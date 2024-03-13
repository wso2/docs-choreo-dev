import { GRAPHQL_URL } from "../../../../support/commons/urls";
import { Enums } from "../../../../support/commons/enums";
import { login } from "../../../../support/console/entities/login/login";
import {
  Project,
  RepoInfo,
  WebAppInfo,
} from "../../../../support/console/entities/project/project";
import { console } from "../../../../support/console/console";
import { CSVWriter } from "../../../../support/commons/csvwriter";
import { WebApp } from "../../../../support/console/entities/component/webapp-component";
import { PERF_INTERCEPT_WAIT_TIME } from "../../../../support/commons/timeouts";

describe("Multiple User Logins", () => {
  let project: Project;
  let webApp: WebApp;
  const PROJECT_DESCRIPTION = "Cypress WebApp Perf Test Project";
  const username = Cypress.env("perfUsername");
  let csv;

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

  before(() => {
    csv = new CSVWriter(username);
  });

  it("Login with multiple users concurrently", () => {
    login.perfLogin();
  });

  it("Creating a project", () => {
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
    csv.writeInterceptionResultsToCsv("Creating a project");

    cy.wait("@createProjectRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception).to.have.property("response");
          expect(interception.response?.statusCode).to.equal(200);
          csv.writeInterceptionResultsToCsv("Creating a project");

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
          csv.writeInterceptionResultsToCsv(
            "Creating a project",
            error.message
          );
        }
      }
    );
  });

  it("Creating a Web App", () => {
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

  it("Build the component", () => {
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
          csv.writeInterceptionResultsToCsv(error.message);
        }
      }
    );
  });

  it("Deploying to dev", () => {
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
          csv.writeInterceptionResultsToCsv(error.message);
        }
      }
    );
  });
});
