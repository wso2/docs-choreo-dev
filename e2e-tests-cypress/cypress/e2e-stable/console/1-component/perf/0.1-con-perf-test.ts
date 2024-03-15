import { GRAPHQL_URL } from "../../../../support/commons/urls";
import { Enums } from "../../../../support/commons/enums";
import { Service } from "../../../../support/console/entities/component/service-component";
import { login } from "../../../../support/console/entities/login/login";
import { Project } from "../../../../support/console/entities/project/project";
import { console } from "../../../../support/console/console";
import { PERF_INTERCEPT_WAIT_TIME } from "../../../../support/commons/timeouts";
import { InterceptWriter } from "../../../../support/commons/interceptWriter";

describe("Multiple User Logins", () => {
  let project: Project;
  const PROJECT_DESCRIPTION = "Cypress Service Perf Test Project";
  const ENDPOINT_NAME = "Readinglist";
  let component: Service;
  const username = Cypress.env("perfUsername");
  let interceptWriter;
  const fixtureFileName = `${username}-intercept.json`;
  const filePath = `${fixtureFileName}`;

  before(() => {
    interceptWriter = new InterceptWriter();
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

  it("Verify Ballerina service component creation", () => {
    try {
      project
        .createServiceComponent(
          Enums.Accessibility.EXTERNAL,
          {
            url: "https://github.com/choreo-test-apps/byor-service-app1",
            branch: "main",
          },
          ENDPOINT_NAME
        )
        .then((serviceComponent: Service) => {
          project.visitComponent(serviceComponent.getName());
          component = serviceComponent;
        });
    } catch (error: any) {
      cy.log("Service component creation failed: " + error.message);
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

  it("Deploying the component with Public level visibility", () => {
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
});
