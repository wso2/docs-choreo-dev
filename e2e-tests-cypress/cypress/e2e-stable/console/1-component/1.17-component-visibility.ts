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
import { BuildPacks, EndpointAccessibility, Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";
import { ConfigEntryStep } from "../../../support/commons/types";
import { TestIds } from "../../../support/console/constants/TestIds";
import { Utils } from "../../../support/commons/utils";

describe("Verify Component visibility functionality", () => {
  const PROJECT_DESCRIPTION = "Component Visibility Test";
  const PROJECT_EXPOSED_ENDPOINT_NAME = "Endpoint 8090";
  const PUBLIC_EXPOSED_ENDPOINT_NAME = "Endpoint 9090";
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const PROJECT_EXPOSED_REPO_NAME = "greeting-service";
  const PUBLIC_EXPOSED_REPO_NAME = "dynamic-endpoint-passthrough";

  let project: Project;
  let projectExposedService: Service;
  let publicExposedService: Service;

  const OPERATION = "greeting";

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
    cy.get('[data-cyid="btn-submit-configform"]').click();
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a ballerina service from choreo samples", () => {
    project
      .createServiceComponentUI({
        displayName: "",
        repoUrl: REPO_URL,
        buildPack: BuildPacks.Ballerina,
        directoryInfo: { directoryName: PROJECT_EXPOSED_REPO_NAME, directoryTestid: PROJECT_EXPOSED_REPO_NAME }
      },
      PROJECT_EXPOSED_ENDPOINT_NAME)
      .then((comp) => {
        projectExposedService = comp;
      });
  });

  it("Build the service with Project level visibility", () => {
    projectExposedService.build();
  });

  it("Deploying the service with Project level visibility", () => {
    projectExposedService.deployProjectLevelAccessibility();
  });

  it("Promoting the service with Project level visibility", () => {
    projectExposedService.promoteProjectLevelAccessibility();
  });

  it("Return to Project", () => {
    projectExposedService.goBackToProject();
  });

  it("Creating passthrough ballerina service", () => {
    project
    .createServiceComponentUI({
      displayName: "",
      repoUrl: REPO_URL,
      buildPack: BuildPacks.Ballerina,
      directoryInfo: { directoryName: PUBLIC_EXPOSED_REPO_NAME, directoryTestid: PUBLIC_EXPOSED_REPO_NAME }
    },
    PUBLIC_EXPOSED_ENDPOINT_NAME)
      .then((comp) => {
        publicExposedService = comp;
      });
  });

  it("Build the service with Public level visibility", () => {
    publicExposedService.build();
  });

  it("Deploy the service with Public level visibility", () => {
    const devServiceConfigs: string[] = [];
    const url = Utils.replaceTrailingSlash(projectExposedService.getDevEndpointUrl(EndpointAccessibility.Project));
    devServiceConfigs.push(url);

    publicExposedService.deployPublicLevelAccessibilityWithConfigs([
      new ConfigEntryStep(addConfiguration, devServiceConfigs),
    ]);
  });

  it("Promote the service with Public level visibility", () => {
    const prodServiceConfigs: string[] = [];
    const url = Utils.replaceTrailingSlash(projectExposedService.getProdEndpointUrl(EndpointAccessibility.Project));
    prodServiceConfigs.push(url);

    publicExposedService.promotePublicLevelAccessibility([
      new ConfigEntryStep(useDeployConfigsIfPrompted),
      new ConfigEntryStep(addConfiguration, prodServiceConfigs),
    ]);
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    publicExposedService
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: PUBLIC_EXPOSED_ENDPOINT_NAME,
        resourcePath: OPERATION,
        method: "get",
        key: "name",
        value: "User",
        parentComponentId: "operations-default-getGreeting"
      })
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain("User");
      });
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    publicExposedService
    .testConsole({
      env: Enums.Environment.PRODUCTION,
      endpoint: PUBLIC_EXPOSED_ENDPOINT_NAME,
      resourcePath: OPERATION,
      method: "get",
      key: "name",
      value: "User",
      parentComponentId: "operations-default-getGreeting"
    })
    .then((res) => {
      expect(res.statusCode).to.be.equal(OK.toString());
      expect(res.response).to.contain("User");
    });
  });
});
