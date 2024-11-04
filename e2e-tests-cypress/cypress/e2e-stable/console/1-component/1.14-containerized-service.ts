/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { BuildPacks, Enums } from "../../../support/commons/enums";
import { Project } from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";
import { OK } from "../../../support/commons/http";
import { ConfigEntryStep, createDefaultSteps } from "../../../support/commons/types";
import { TestIds } from "../../../support/console/constants/TestIds";
import { Service } from "../../../support/console/entities/component/service-component";
import { Utils } from "../../../support/commons/utils";

after(() => {
  console.logout();
});

describe("Verify containerized service functionality", () => {
  const PROJECT_DESCRIPTION = "Containerized service scenario";
  const ENDPOINT_NAME = "Go Greeter";
  const CONFIG_KEY = "config";
  const CONFIG_VALUE = "config-value";
  const SECRET_KEY = "secret";
  const SECRET_VALUE = "secret-value";
  const MOUNT_PATH = "/app/configs/config.json";
  const CONFIG_FILE = '{\n\t"name": "testUser"';
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "greeting-service-go";

  let project: Project;
  let byoc: Service;

  function addConfiguration() {
    cy.get(TestIds.addConfig).click();
    cy.wait(1000);
    cy.log(`Typing CONFIG_KEY: ${CONFIG_KEY}`);
    cy.get(TestIds.addConfigKey).should("be.visible").type(CONFIG_KEY);
    cy.get(TestIds.addConfigValue).should("be.visible").type(CONFIG_VALUE);
    cy.get(TestIds.configSave).click();
    cy.get(TestIds.addConfig).click();
    cy.get(TestIds.addConfigKey).type(SECRET_KEY);
    cy.get(TestIds.addConfigValue).type(SECRET_VALUE);
    cy.get(TestIds.keyValueCheckBox).click();
    cy.get(TestIds.configSave).click();
    cy.get(TestIds.nextButton).click();
    cy.get(TestIds.fileMount).click();
    cy.get(TestIds.mountPath).type(MOUNT_PATH);
    cy.get(TestIds.formConfigField).type(CONFIG_FILE);
    cy.get(TestIds.nextButton).click();
  }

  // This step is only encountered the first time a service component with a config is promoted.
  // However if due to an error the step is retried by Cypress this step will not be encountered.
  // Therefore this is handled as an optional step.
  function addConfigurationProd() {
    cy.contains(/^Step/).should("be.visible");
    cy.get("body").then((body) => {
      if (body.find(TestIds.copyConfigs).length > 0) {
        cy.get(TestIds.copyConfigs).click();
        cy.get(TestIds.next).should("be.visible").click();
      }
    });
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });


  it("Creating a containerized service from choreo samples", () => {
    project
      .createContainerizedServiceComponentUI({
        displayName: "",
        repoUrl: REPO_URL,
        buildPack: BuildPacks.DOCKER,
        directoryInfo: { directoryName: REPO_NAME, directoryTestid: REPO_NAME },
      }, 
      ENDPOINT_NAME)
      .then((comp) => {
        byoc = comp;
      });
  });

  it("Build the Component", () => {
    byoc.build();
  });

  it("Deploying to Dev with public level endpoint", () => {
    byoc.deployPublicLevelAccessibilityWithConfigs([new ConfigEntryStep(addConfiguration)], false);
  });

  it("Verify component promotion to Prod", () => {
    let configSteps = createDefaultSteps(3);
    configSteps[0] = new ConfigEntryStep(addConfigurationProd);
    byoc.promotePublicLevelAccessibility(configSteps, false);
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    byoc
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "greeter/greet",
        method: "",
        parentComponentId: "operations-greeting-get_greeter_greet",
      })
      .then((res) => {
        expect(Utils.replaceLineBreaks(res.response)).to.be.eq("Hello, Stranger!");
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    byoc
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "greeter/greet",
        method: "",
        parentComponentId: "operations-greeting-get_greeter_greet",
      })
      .then((res) => {
        expect(Utils.replaceLineBreaks(res.response)).to.be.eq("Hello, Stranger!");
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verifying component insights", () => {
    byoc.verifyUsageInsights();
  });

  it("Verify API insights for dev env", () => {
    project.verifyUsageInsights(Enums.Environment.DEVELOPMENT, {
      expectedTraffic: 1,
    });
  });

  it("Verify API insights for prod env", () => {
    project.verifyUsageInsights(Enums.Environment.PRODUCTION, {
      expectedTraffic: 1,
    });
  });
});
