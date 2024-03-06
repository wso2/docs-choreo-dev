import { GRAPHQL_URL } from "../../../../support/commons/urls";
import { Enums } from "../../../../support/commons/enums";
import { Service } from "../../../../support/console/entities/component/service-component";
import { login } from "../../../../support/console/entities/login/login";
import { Project } from "../../../../support/console/entities/project/project";
import { console } from "../../../../support/console/console";
import { CSVWriter } from "../../../../support/commons/csvwriter";

describe("Multiple User Logins", () => {
  let project: Project;
  const PROJECT_DESCRIPTION = "Cypress Perf Test Project";
  const ENDPOINT_NAME = "Readinglist";
  let component: Service;
  const username = Cypress.env('perfUsername');
  let csv;

  before(() => {
    csv = new CSVWriter(username);
  });

  it("Login with multiple users concurrently", () => {
    login.perfLogin();
  });

  it("Creating a project", () => {
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1, 
    }, (req) => { 
      if (req.body.includes("createProject")) { 
        req.alias = "createProjectRequest"; 
      }
    });
  
    project = console.createNewProject(PROJECT_DESCRIPTION);
  
    cy.wait("@createProjectRequest").then((interception) => {
      try {
        expect(interception).to.have.property("response");
        expect(interception.response?.statusCode).to.equal(200);
  
        const responseBody = interception.response?.body;
        expect(responseBody).to.have.property("data");
  
        const createProject = responseBody?.data?.createProject;
        expect(createProject, "createProject is missing in the response").to.exist;
        expect(createProject).to.have.property("region").that.equals("US-CDP-2");
        expect(createProject).to.have.property("name");
      } catch (error: any) {
        // Log assertion errors and proceed
        cy.log(`Assertion error: ${error.message}`);
        csv.writeInterceptionResultsToCsv("Creating a project", error.message);
      }
    });
  });
  

  it("Verify Ballerina service component creation", () => {
    cy.intercept({
      method: "POST",
      url: GRAPHQL_URL,
      times: 1,
    }, (req) => { 
      if (req.body.includes("createComponent")) {
      req.alias = "componentCreationRequest"; } 
    });
  
    // Create a Ballerina service component
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
  
    cy.wait("@componentCreationRequest").then((interception) => {
      try {
        expect(interception).to.have.property("response");
        expect(interception.response?.statusCode).to.equal(200);
        const responseBody = interception.response?.body;
        expect(responseBody).to.have.property("data");
  
        const createComponent = responseBody?.data?.createComponent;
        expect(createComponent, "createComponent is missing in the response").to.exist;
        expect(createComponent).to.have.property("orgId");
        expect(createComponent).to.have.property("projectId");
        expect(createComponent).to.have.property("handler");
      } catch (error: any) {
        cy.log(`Assertion error: ${error.message}`);
        csv.writeInterceptionResultsToCsv("Verify Ballerina service component creation", error.message);
      }
    });
  });
  

it("Build the component", () => {

  cy.intercept({
    method: "POST",
    url: GRAPHQL_URL,
    times: 1,
  }, (req) => { 
    if (req.body.includes("deployComponent")) {
    req.alias = "buildComponentRequest"; } 
  });

  component.build();
  cy.wait("@buildComponentRequest").then((interception) => {
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
  });

});

});

