/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { GitHub } from "../../../support/github/github";
import { Enums } from "../../../support/commons/enums";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";

describe("Create Greeting sample in Choreo", () => {
  const PROJECT_DESCRIPTION = "sample greeting service";

  const REPO_NAME = "choreo-samples";
  const ENDPOINT_NAME = "Endpoint 8090";
  const subPath = "greeting-service";
  const sampleName = "Greeting Service";

  let project: Project;
  let component: Service;

  after(() => {
    console.logout();
  });

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify sample search", () => {
    project.searchSampleService(sampleName);
  });

  it("Verify Hello World sample creation", () => {
    GitHub.syncForkWithUpstream(REPO_NAME, "main");

    project
      .createServiceComponent(
        Enums.Accessibility.EXTERNAL,
        {
          url: "https://github.com/choreo-test-apps/choreo-samples",
          branch: "main",
          subPath: subPath,
        },
        ENDPOINT_NAME
      )
      .then((serviceComponent: Service) => {
        project.visitComponent(serviceComponent.getName());
        component = serviceComponent;
      });
  });

  it("Build the sample", () => {
    component.build();
  });

  it("Deploy sample", () => {
    component.deployPublicLevelAccessibility();
  });

  it("Verify test functionality of sample resource in dev on swagger", () => {
    component
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "",
        method: "get",
        key: "name",
        value: "dasun",
        parentComponentId: "operations-default-get",
      })
      .then((res) => {
        expect(res.statusCode).to.be.eq("200");
        expect(res.response).to.contain("dasun");
      });
  });

  it("Verify suspending Dev deployed component", () => {
    component.stopDeployment();
  });

  it("Return to Project", () => {
    component.goBackToProject();
  });

  it("Verify component deletion", () => {
    project.deleteComponent(component.getName());
  });
});
