import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { Project } from "../../../../support/console/entities/project/project";
import { console } from "../../../../support/console/console";
import { GRAPHQL_URL } from "../../../../support/commons/urls";
import { SHORT_TIME } from "../../../../support/commons/timeouts";
import { Enums } from "../../../../support/commons/enums";
import { Service } from "../../../../support/console/entities/component/service-component";
import { login } from "../../../../support/console/entities/login/login";

after(() => {
  console.logout();
});

describe("Multiple User Logins", () => {
  let project: Project;
  const PROJECT_DESCRIPTION = "Cypress Perf Test Project";
  const ENDPOINT_NAME = "Readinglist";
  let component: Service;


  it("Login with multiple users concurrently", () => {
    login.perfLogin();
  });

  it("Creating a project", () => {
    try {
      // Intercept the GraphQL request
      cy.intercept({
        method: "POST",
        url: GRAPHQL_URL,
        times: 1,
      }).as("projectCreationRequest");
  
      project = console.createNewProject(PROJECT_DESCRIPTION);
  
      // Wait for the intercepted request to complete
      cy.wait("@projectCreationRequest").then((interception) => {
        // Assertions
        expect(interception).to.have.property("request");
        expect(interception).to.have.property("response");
        expect(interception.response?.statusCode).to.equal(200);
  
        // Validate fields from the response
        const responseBody = interception.response?.body;
        expect(responseBody).to.have.property("data");
  
        const createProject = responseBody?.data?.createProject;
        if (createProject) {
          // Assert only if createProject exists
          expect(createProject).to.have.property("region").that.equals("US-CDP-2");
          expect(createProject).to.have.property("name");
        } else {
          // Log a message if createProject is missing
          cy.log("createProject is missing in the response");
        }
      });
    } catch (error: any) {
      cy.log(`Error occurred: ${error.message}`);
      throw error;
    }
  });
  

  it("Verify Ballerina service component creation", () => {
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
  });

  it("Build the component", () => {
    try {
      cy.intercept({
        method: "POST",
        url: GRAPHQL_URL,
        times: 1,
      }).as("buildComponentRequest");
  
      component.build();
  
      // Wait for the intercepted response to complete
      cy.wait("@buildComponentRequest").then((interception) => {
        // Assertions
        expect(interception).to.have.property("response");
        expect(interception.response?.statusCode).to.equal(200);
  
        // Validate fields from the response
        const responseBody = interception.response?.body;
        expect(responseBody).to.have.property("data");
  
        // Ensure deploymentStatuses is not undefined
        const deploymentStatuses = responseBody?.data?.deploymentStatusByVersion;
        expect(deploymentStatuses, "deploymentStatuses is missing or empty").to.exist.and.to.be.an("array").and.not.empty;
  
        // Validate each deployment status and success
        deploymentStatuses.forEach((status) => {
          expect(status).to.have.property("status").that.equals("completed");
          expect(status).to.have.property("conclusion").that.equals("success");
        });
      });
    } catch (error: any) {
      cy.log(`Error occurred: ${error.message}`);
      throw error;
    }
  });
  

  it("Deploying the component with Public level visibility", () => {
    component.deployPublicLevelAccessibility();
  });


});
