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

import { BuildPacks, Enums } from "../../../support/commons/enums";
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
  const ENDPOINT_NAME = "Endpoint 8090";
  let project: Project;
  let component: Service;
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "greeting-service";

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
        repoName: REPO_NAME,
        repoTestid: "greeting-service",
        ENDPOINT_NAME,
      })
      .then((comp) => {
        component = comp;
      });
  });

  it("Build the component", () => {
    component.build();
  });

  it.skip("Deploying the component with Project level visibility", () => {
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
        resourcePath: "",
        method: "get",
        key: "name",
        value: "User",
        parentComponentId: "operations-default-get",
      })
      .then((res) => {
        expect(res.statusCode).to.be.eq(OK.toString());
        expect(res.response).to.contain("User");
      });
  });

  it("Testing the component in Prod", () => {
    component
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "",
        method: "get",
        key: "name",
        value: "User",
        parentComponentId: "operations-default-get",
      })
      .then((res) => {
        expect(res.statusCode).to.be.eq(OK.toString());
        expect(res.response).to.contain("User");
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

  it("Testing the component in Dev", () => {
    component
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "",
        method: "get",
        key: "name",
        value: "User",
        parentComponentId: "operations-default-get",
      })
      .then((res) => {
        expect(res.statusCode).to.be.eq(OK.toString());
        expect(res.response).to.contain("User");
      });
  });

  it("Testing the component in Prod", () => {
    component
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "",
        method: "get",
        key: "name",
        value: "User",
        parentComponentId: "operations-default-get",
      })
      .then((res) => {
        expect(res.statusCode).to.be.eq(OK.toString());
        expect(res.response).to.contain("User");
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
