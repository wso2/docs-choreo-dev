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

import { BuildPacks } from "../../../support/commons/enums";
import { console } from "../../../support/console/console";
import { TestRunner } from "../../../support/console/entities/component/test-runner-component";
import { Project } from "../../../support/console/entities/project/project";

after(() => {
  console.logout();
});

describe("Verify Test Runner Component functionality", () => {
  const PROJECT_DESCRIPTION = "TestRunner Go Component";

  let project: Project;
  let runner: TestRunner;
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "test-runner-go";

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a Test Runner from choreo samples", () => {
    project
      .createTestRunnerUI({
        displayName: "",
        repoUrl: REPO_URL,
        buildPack: BuildPacks.Go,
        repoName: REPO_NAME,
        repoTestid: "subPath-test-runner-go",
        languageVersion: "1.x",
      })
      .then((comp) => {
        runner = comp;
      });
  });




  // it("Verify test runner component creation", () => {
  //   project
  //     .createTestRunnerComponent(
  //       {
  //         url: "https://github.com/choreo-test-apps/buildPack-testrunner-Goapp",
  //         branch: "main",
  //       },
  //       {
  //         buildpackId: "F9E4820E-6284-11EE-8C99-0242AC120005",
  //         languageVersion: "1.x",
  //       }
  //     )
  //     .then((comp: TestRunner) => {
  //       project.visitComponent(comp.getName());
  //       runner = comp;
  //     });
  // });

  it("Build the component", () => {
    runner.build();
  });

  it("Deploying to Dev", () => {
    runner.deployToDev();
  });

  it("Verify component promotion to Prod", () => {
    runner.promoteProd();
  });

  it("Verify test page is disabled", () => {
    runner.verifyTestPageIsDisabled();
  });

  it("Verify manage page is disabled", () => {
    runner.verifyManagePageIsDisabled();
  });
});
