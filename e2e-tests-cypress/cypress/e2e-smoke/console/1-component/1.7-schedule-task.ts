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

import { LONG_TIME } from "../../../support/console/constants";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ScheduleTask } from "../../../support/console/pages/templates/schedule-task-template";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { VSSourceControl } from "../../../support/console/pages/vscod-editor/vs-source-control";
import { Utils } from "../../../support/console/utils";

describe("Schedule task", () => {

  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_Name = Utils.generateComponentName("sch");
  const commitMessage = "adding task method";
  const EXPECTED_RESULT =
    '{"userId":1,"id":1,"title":"delectus aut autem","completed":false}';

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a schedule task", () => {
    cy.log("Starting schedule task creation");
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    ScheduleTask.selectTask();
    ScheduleTask.createTask(API_Name, PROJECT_DESCRIPTION);
    ComponentDevelopPage.getComponentURL();
  });

  it("Verify code edit in vscode", () => {
    LoginPage.navigateToCodespace();
    VSExplorer.pasteCode("scheduletask.bal");
    VSExplorer.selectSourceControl();
    VSExplorer.enterCommandInTerminal(
      "bash /config/workspace/.githooks/pre-commit"
    );
    VSExplorer.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit"
    );
    VSSourceControl.commitChanges(commitMessage);
    VSExplorer.enterCommandInTerminal("git push");
  });

  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo();

  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployScheduleTask();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteScheduleTask();
  });

  it("Verify task execution in observability ", () => {
    ComponentOverviewPage.navigateToObserve();
    ComponentObservePage.gotoLogs(LONG_TIME);
  });
  it("Verify dev env logs", () => {
    ComponentObservePage.selectEnv(Environment.DEVELOPMENT);
    ComponentObservePage.verifyTextInLogs(EXPECTED_RESULT);
  });

  it("Verify dev env logs", () => {
    ComponentObservePage.selectEnv(Environment.PRODUCTION);
    ComponentObservePage.verifyTextInLogs(EXPECTED_RESULT);
  });

  it("Verify application suspension", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
