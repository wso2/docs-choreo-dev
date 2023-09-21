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
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentObservePage } from "../../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../../support/interfaces/component-data";

describe("Create Reusable Schedule Trigger", () => {
  const SCHEDULE_NAME = "create-ReuseScheduleTrigger-1.7.1";
  const PROJECT_NAME = "Default Project";
  const EXPECTED_RESULT =
    '{"userId":1,"id":1,"title":"delectus aut autem","completed":false}';

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify Schedule Trigger component creation", () => {
    let componentData: ComponentData = {
      componentName: SCHEDULE_NAME,
      displayType: Enums.DisplayType.scheduledTask,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/schedule-trigger",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };

    ProjectListingPage.selectProject();
    ProjectOverviewPage.searchReuseComponent(componentData);
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(SCHEDULE_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployScheduleTask();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteScheduleTask();
  });

  it("Verify navigate to observability Page", () => {
    ComponentOverviewPage.navigateToObserve(MEDIUM_TIME.timeout);
  });
  
  it("Verify dev env logs", () => {
    ComponentObservePage.selectEnv(Enums.Environment.DEVELOPMENT);
    ComponentObservePage.verifyTextInLogs(EXPECTED_RESULT);
  });

  it("Verify prod env logs", () => {
    ComponentObservePage.selectEnv(Enums.Environment.PRODUCTION);
    ComponentObservePage.verifyTextInLogs(EXPECTED_RESULT);
  });

  it("Verify application suspension", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
