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

import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";
import { Enums } from "../../../support/commons/enums";
import { ConfigEntryStep } from "../../../support/commons/types";
import { OK } from "../../../support/commons/http";
import { TestIds } from "../../../support/console/constants/TestIds";

describe("Verify Component visibility functionality", () => {
  const PROJECT_DESCRIPTION = "Component Visibility Test";
  const ENDPOINT_NAME = "Readinglist";
  const RESOURCE = "/books";

  let project: Project;
  let service: Service;
  let trigger: Service;

  // This step is only encountered the first time a service component with a config is promoted.
  // However if due to an error the step is retried by Cypress this step will not be encountered.
  // Therefore this is handled as an optional step.
  function useDeployConfigsIfPrompted() {
    cy.contains(/^Step/).should("be.visible");
    cy.get("body").then((body) => {
      if (body.find(TestIds.nextButton).length > 0) {
        cy.get(TestIds.nextButton).click();
      }
    });
  }

  function addConfiguration(args: string[] | undefined) {
    if (args === undefined || args.length === 0) {
      throw new Error("args is undefined or empty");
    }
    cy.get('[data-cyid="invoke_url"]>input').type(args[0]);
    cy.get('[data-cyid="invoke_resource"]>input').type(args[1]);
    cy.get('[data-cyid="btn-submit-configform"]').click();
  }

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
        service = serviceComponent;
      });
  });

  it("Build the service", () => {
    service.build();
  });

  it("Deploying the service with Project level visibility", () => {
    service.deployProjectLevelAccessibility();
  });

  it("Promoting the service with Project level visibility", () => {
    service.promoteProjectLevelAccessibility();
  });

  it("Return to Project", () => {
    service.goBackToProject();
  });

  it("Verify Service Trigger component creation", () => {
    project
      .createServiceComponent(
        Enums.Accessibility.EXTERNAL,
        {
          url: "https://github.com/choreo-test-apps/service-to-service",
          branch: "main",
        },
        ENDPOINT_NAME
      )
      .then((comp: Service) => {
        project.visitComponent(comp.getName());
        trigger = comp;
      });
  });

  it("Build the trigger", () => {
    trigger.build();
  });

  it("Deploying the trigger", () => {
    const devServiceConfigs: string[] = [];
    devServiceConfigs.push(service.getDevEndpointUrl());
    devServiceConfigs.push(RESOURCE);

    trigger.deployPublicLevelAccessibilityWithConfigs([
      new ConfigEntryStep(addConfiguration, devServiceConfigs),
    ]);
  });

  it("Promote the trigger", () => {
    const prodServiceConfigs: string[] = [];
    prodServiceConfigs.push(service.getProdEndpointUrl());
    prodServiceConfigs.push(RESOURCE);

    trigger.promotePublicLevelAccessibility([
      new ConfigEntryStep(useDeployConfigsIfPrompted),
      new ConfigEntryStep(addConfiguration, prodServiceConfigs),
    ]);
  });

  it("Invoke trigger in Dev", () => {
    trigger
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "invoke",
        method: "get",
        parentComponentId: "operations-default-getInvoke",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Invoke trigger in Prod", () => {
    trigger
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "invoke",
        method: "get",
        parentComponentId: "operations-default-getInvoke",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });
});
