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

import { Enums } from "../../../../support/commons/enums";
import { MEDIUM_TIME, SHORT_TIME } from "../../../../support/commons/timeouts";
import { ScheduleTrigger } from "../../../../support/console/entities/component/schedule-trigger-component";
import { console } from "../../../../support/console/console";
import { Project } from "../../../../support/console/entities/project/project";

describe("Create Reusable Schedule Trigger", () => {

  let project: Project;
  let component: ScheduleTrigger;


  const SCHEDULE_NAME = "create-ReuseScheduleTrigger-1.7.1";
  const PROJECT_NAME = "Default Project";
  const MATCHING_STRING = "Hello, User";

    it("Login to Console", () => {
      console.login();
    });
  
    it("Search reuse component project", () => {
      project = console.searchProject(PROJECT_NAME);
    });

  it("Navigate to existing Schedule Trigger component", () => {
    if (!project.isComponentExists(SCHEDULE_NAME)) {
      project
        .createScheduleTriggerComponent(
          Enums.Accessibility.EXTERNAL,
          {
            url: "https://github.com/choreo-test-apps/schedule-trigger",
            branch: "main",
          },
        )
        .then((comp: ScheduleTrigger) => {
          project.visitComponent(SCHEDULE_NAME);
          component = comp;
        });
    } else {
      project.visitComponent(SCHEDULE_NAME);
      component = new ScheduleTrigger(SCHEDULE_NAME);
    }
  });


  it("Build the component", () => {
    component.build();
  });

  it("Deploying to Dev", () => {
    component.deployToDev();
  });

  it("Verify component promotion to Prod", () => {
    component.promoteProd();
  });

  it("Verify dev env logs", () => {
    component.verifyObservabilityMetricsLogs(
      Enums.Environment.DEVELOPMENT,
      MATCHING_STRING,
      SHORT_TIME.timeout
    );
  });

  it("Verify prod env logs", () => {
    component.verifyObservabilityMetricsLogs(
      Enums.Environment.PRODUCTION,
      MATCHING_STRING,
      0
    );
  });

  it("Stop deployments", () => {
    component.stopDeployment();
    component.stopPromotion();
  });
});