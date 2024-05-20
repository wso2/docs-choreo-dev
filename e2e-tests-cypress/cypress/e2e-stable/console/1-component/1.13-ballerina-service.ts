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

import { Enums } from "../../../support/commons/enums";
import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";
import { UsagePlan } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";
import { SHORT_TIME } from "../../../support/commons/timeouts";

after(() => {
  console.logout();
});

describe("Verify Ballerina service functionality", () => {
  const PROJECT_DESCRIPTION = "sample ballerina service scenario";
  const ENDPOINT_NAME = "Readinglist";
  let project: Project;
  let component: Service;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
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
    component.build();
  });

  it("Deploying the component with Project level visibility", () => {
    component.deployProjectLevelAccessibility();
  });

  it("Deploying the component with Public level visibility", () => {
    component.deployPublicLevelAccessibility();
  });

  it("Verifying component promotion to Prod", () => {
    component.promotePublicLevelAccessibility();
  });

  it("Testing the component in Dev", () => {
    component
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Testing the component in Prod", () => {
    component
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Adding a new version", () => {
    component.addVersion();
  });

  it("Build the new version", () => {
    component.build();
  });

  it("Deploying the new version", () => {
    component.deployPublicLevelAccessibility();
  });

  it("Promote new version to Prod", () => {
    component.promotePublicLevelAccessibility();
  });

  it("Testing new version in Dev", () => {
    component
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Testing new version in Prod", () => {
    component
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Updating the usage plans", () => {
    component.updateUsagePlans([UsagePlan.Gold, UsagePlan.Bronze]);
  });

  it("Enabling CORS", () => {
    component.enableCors(Enums.Environment.DEVELOPMENT);
  });

  it("Publishing the component", () => {
    component.publish();
  });

  it("Verifying component insights", () => {
    component.verifyUsageInsights();
  });

  it("Verifying project insights in Dev", () => {
    project.verifyUsageInsights(Enums.Environment.DEVELOPMENT);
  });

  it("Verifying project insights in Prod", () => {
    project.verifyUsageInsights(Enums.Environment.PRODUCTION);
  });
});
