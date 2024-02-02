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

import { Enums } from "../../../support/commons/enums";
import { Project } from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";
import { Byoc } from "../../../support/console/entities/component/byoc-component";
import { OK } from "../../../support/commons/http";
import { ConfigEntryStep } from "../../../support/commons/types";
import { TestIds } from "../../../support/console/constants/TestIds";


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

  let project: Project;
  let byoc: Byoc;

  function addConfiguration() {
    cy.get(TestIds.addConfig).click();
    cy.wait(1000);
    cy.log(`Typing CONFIG_KEY: ${CONFIG_KEY}`);
    cy.get(TestIds.addConfigKey).should('be.visible').type(CONFIG_KEY);
    cy.get(TestIds.addConfigValue).should('be.visible').type(CONFIG_VALUE);
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
    cy.get(TestIds.next).should("be.visible").click();
  }

  function addConfigurationProd() {
      cy.get(TestIds.byocPromote).click();
      cy.get(TestIds.next).should("be.visible").click();
      addConfiguration();
  }
  
  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify containerized service component creation", () => {
    project
      .createByocServiceComponent(
        {
          url: "https://github.com/choreo-test-apps/byoc-service-app",
          branch: "main",
        },
        {
          dockerfilePath: "Dockerfile",
          dockerContext: "",
        },
        ""
      )
      .then((comp: Byoc) => {
       // project.visitComponent(comp.getName());
        byoc = comp;
      });
  });

  it("Build the Component", () => {
    byoc.build();
  });

  it("Deploying to Dev with public level endpoint", () => {
    byoc.deployToDevWithConfigs([new ConfigEntryStep(addConfiguration)]);

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
      expect(res.response).to.be.eq("Hello, Stranger!\n\n");
      expect(res.statusCode).to.be.equal(OK.toString());
    });
  });

it("Verify component promotion to Prod", () => {
  byoc.promoteWithConfigs([new ConfigEntryStep(addConfigurationProd)]);
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
      expect(res.response).to.be.eq("Hello, Stranger!\n\n");
      expect(res.statusCode).to.be.equal(OK.toString());
    });
  });

  it("Stop component deployments", () => {
    byoc.stopDeployment();
    byoc.stopPromotion();
  });

  it("Verifying component insights", () => {
    byoc.verifyUsageInsights();
  });

  it("Verify API insights for dev env", () => {
    project.verifyUsageInsights(Enums.Environment.DEVELOPMENT, { expectedTraffic: 1 });
  });

  it("Verify API insights for prod env", () => {
    project.verifyUsageInsights(Enums.Environment.PRODUCTION, { expectedTraffic: 1 });
  });

});

