/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { ManualTrigger } from "../../../support/console/entities/component/manual-trigger-component";

after(() => {
  console.logout();
});

describe("Verify manual trigger creation functionality", () => {
  const PROJECT_DESCRIPTION = "Manual Trigger";
  let project: Project;
  let component: ManualTrigger;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify Manual Trigger component creation", () => {
    project
      .createManualTriggerComponent(Enums.Accessibility.EXTERNAL, {
        url: "https://github.com/choreo-test-apps/manual-trigger",
        branch: "main",
      })
      .then((comp: ManualTrigger) => {
        project.visitComponent(comp.getName());
        component = comp;
      });
  });

  it("Build the component", () => {
    component.build();
  });

  it("Deploying to Dev", () => {
    component.deployToDevWithoutSplitButton();
  });

  it("Verify component promotion to Prod", () => {
    component.promoteToProd();
  });

  it("Verify execution in dev", () => {
    component.executeComponent(Enums.Environment.DEVELOPMENT);
  });

  it("Verify execution in prod", () => {
    component.executeComponent(Enums.Environment.PRODUCTION);
  });
});
