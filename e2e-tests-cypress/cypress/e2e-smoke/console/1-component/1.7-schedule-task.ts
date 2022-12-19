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
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ScheduleTask } from "../../../support/console/pages/templates/schedule-task-template";
import { Utils } from "../../../support/console/utils";
import { REUSABLE_PROJECT_NAME } from "../../../support/devportal/constants";

describe("Create Schedule Trigger", () => {

  const SCHEDULE_NAME = "create-ScheduleTrigger-1.7";
  const EXPECTED_RESULT =
    '{"userId":1,"id":1,"title":"delectus aut autem","completed":false}';

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Selecting a schedule task", () => {
    cy.log("Starting schedule task selection");
    ProjectListingPage.selectProject(REUSABLE_PROJECT_NAME);
    ComponentListingPage.visitToAComponent(SCHEDULE_NAME);
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
