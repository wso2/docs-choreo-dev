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

import { Enums } from "../../../../support/commons/enums";
import { console } from "../../../../support/console/console";
import { ManualTrigger } from "../../../../support/console/entities/component/manual-trigger-component";
import { Project } from "../../../../support/console/entities/project/project";

describe("Verify Reusable Manual Trigger creation functionality", () => {
  const MANUAL_NAME = "create-manualTrigger-1.5.1";
  const PROJECT_NAME = "Default Project";
  let project: Project;
  let component: ManualTrigger;

  it("Login to Console", () => {
    console.login();
  });

  it("Search reuse component project", () => {
    project = console.searchProject(PROJECT_NAME);
  });

  it("Navigate to existing Manual Trigger component", () => {
    if (!project.isComponentExists(MANUAL_NAME)) {
      project
        .createManualTriggerComponent(
          Enums.Accessibility.EXTERNAL,
          {
            url: "https://github.com/choreo-test-apps/manual-trigger",
            branch: "main",
          },
          MANUAL_NAME
        )
        .then((comp: ManualTrigger) => {
          project.visitComponent(MANUAL_NAME);
          component = comp;
        });
    } else {
      project.visitComponent(MANUAL_NAME);
      component = new ManualTrigger(MANUAL_NAME);
    }
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
