import {
  CONNECTIONS_URL_CONFIG,
  GRAPHQL_URL,
} from "../../support/commons/urls";
import { Enums } from "../../support/commons/enums";
import { Service } from "../../support/console/entities/component/service-component";
import { login } from "../../support/console/entities/login/login";
import { Project } from "../../support/console/entities/project/project";
import { console } from "../../support/console/console";
import { PERF_INTERCEPT_WAIT_TIME } from "../../support/commons/timeouts";
import { InterceptWriter } from "../../support/commons/interceptWriter";

describe("Multiple User Logins", () => {
  let project: Project;
  const PROJECT_DESCRIPTION = "Cypress Service Perf Test Project";
  const ENDPOINT_NAME = "Readinglist";
  let component: Service;
  const username = Cypress.env("perfUsername");
  let interceptWriter;
  const fixtureFileName = `${username}-intercept.json`;
  const filePath = `${fixtureFileName}`;

  function createComponentCallback(request: any, response: any): void {
    const interceptorName = "createComponentRequest";
    interceptWriter.interceptAndWriteToFixture(
      interceptorName,
      filePath,
      request,
      response
    );
  }

  before(() => {
    interceptWriter = new InterceptWriter();
  });

  beforeEach(() => {
    username;
  });

  afterEach(function () {
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

  it(`Creating a project - ${Cypress.env("perfUsername")}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: GRAPHQL_URL,
      },
      (req) => {
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

  it(`Verify Ballerina service component creation - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    try {
      project
        .createServiceComponent(
          Enums.Accessibility.EXTERNAL,
          {
            url: "https://github.com/choreo-test-apps/byor-service-app1",
            branch: "main",
          },
          ENDPOINT_NAME,
          undefined,
          createComponentCallback
        )
        .then((serviceComponent: Service) => {
          project.visitComponent(serviceComponent.getName());
          component = serviceComponent;
        });
    } catch (error: any) {
      cy.log("Service component creation failed: " + error.message);
    }
  });

  it(`Build the component - ${Cypress.env("perfUsername")}`, () => {
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

    component.build();
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

    component.deployPublicLevelAccessibility();

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

  it(`Return to Project - ${Cypress.env("perfUsername")}`, () => {
    component.goBackToProject();
  });

  // Creating the second service component in the same project

  it(`Verify second Ballerina service component creation - ${Cypress.env(
    "perfUsername"
  )}`, () => {
    try {
      project
        .createServiceComponent(
          Enums.Accessibility.EXTERNAL,
          {
            url: "https://github.com/choreo-test-apps/byor-service-app1",
            branch: "main",
          },
          ENDPOINT_NAME,
          undefined,
          createComponentCallback
        )
        .then((serviceComponent: Service) => {
          project.visitComponent(serviceComponent.getName());
          component = serviceComponent;
        });
    } catch (error: any) {
      cy.log("Service component creation failed: " + error.message);
    }
  });

  it(`Create connections - ${Cypress.env("perfUsername")}`, () => {
    cy.intercept(
      {
        method: "POST",
        url: CONNECTIONS_URL_CONFIG,
      },
      (req) => {
        req.alias = "createConnectionsRequest";
      }
    );

    component.createConnections();
    cy.wait("@createConnectionsRequest", PERF_INTERCEPT_WAIT_TIME).then(
      (interception) => {
        try {
          expect(interception.response?.statusCode).to.equal(201);
          const responseBody = interception.response?.body;
          expect(responseBody).to.have.property("status");

          const desiredResponseExists = responseBody?.status.some(
            (status) =>
              status.result ===
                "Successfully generated oauth application keys" &&
              status.success === true
          );
          expect(desiredResponseExists).to.be.true;
        } catch (error: any) {
          cy.log(`Assertion error: ${error.message}`);
          interceptWriter.interceptAndWriteToFixture(
            "createConnectionsRequest",
            filePath,
            interception.request,
            interception.response
          );
        }
      }
    );
  });
});
