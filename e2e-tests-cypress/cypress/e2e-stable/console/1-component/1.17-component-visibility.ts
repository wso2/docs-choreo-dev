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
import { ManualTrigger } from "../../../support/console/entities/component/manual-trigger-component";
import { ConfigEntryStep } from "../../../support/commons/types";

describe("Verify Component visibility functionality", () => {
  const PROJECT_DESCRIPTION = "Component Visibility Test";
  const ENDPOINT_NAME = "Readinglist";
  const LOG_MESSAGE = "MATCHING RESPONSE";

  let project: Project;
  let service: Service;
  let trigger: ManualTrigger;

  function addConfiguration(args: string[] | undefined) {
    if (args === undefined || args.length === 0) {
      throw new Error("args is undefined or empty");
    }
    cy.get('[data-cyid="invke_url"]>input').type(args[0]);
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

  it("Verify Manual Trigger component creation", () => {
    project
      .createManualTriggerComponent(Enums.Accessibility.EXTERNAL, {
        url: "https://github.com/choreo-test-apps/book-service-manual-trigger",
        branch: "main",
      })
      .then((comp: ManualTrigger) => {
        project.visitComponent(comp.getName());
        trigger = comp;
      });
  });

  it("Build the trigger", () => {
    trigger.build();
  });

  it("Deploying the trigger", () => {
    const devUrlOfService: string[] = [];
    devUrlOfService.push(service.getDevEndpointUrl());

    trigger.deployToDevWithConfigs([
      new ConfigEntryStep(addConfiguration, devUrlOfService),
    ]);
  });

  it("Execute trigger in Dev", () => {
    trigger.executeComponent(Enums.Environment.DEVELOPMENT);
  });

  it("Promote the trigger", () => {
    const prodUrlOfService: string[] = [];
    prodUrlOfService.push(service.getProdEndpointUrl());

    trigger.promoteToProdWithConfigs([
      new ConfigEntryStep(),
      new ConfigEntryStep(addConfiguration, prodUrlOfService),
    ]);
  });

  it("Execute trigger in Prod", () => {
    trigger.executeComponent(Enums.Environment.PRODUCTION);
  });

  it("Verify dev env logs", () => {
    trigger.verifyObservabilityMetricsLogs(
      Enums.Environment.DEVELOPMENT,
      LOG_MESSAGE
    );
  });

  it("Verify prod env logs", () => {
    trigger.verifyObservabilityMetricsLogs(
      Enums.Environment.PRODUCTION,
      LOG_MESSAGE
    );
  });
});
