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

import { MEDIUM_TIME } from "../../../support/console/constants";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Enums } from "../../../support/console/enums";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GitHub } from "../../../support/github/github";

describe("Create Schedule Trigger", () => {
  const SCHEDULE_NAME = Utils.generateComponentName()
  const EXPECTED_RESULT =
    '{"userId":1,"id":1,"title":"delectus aut autem","completed":false}';
  const REPO_NAME = Utils.generateComponentName("repo");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Internal API Test";

  before(() => {
    LoginPage.login();
    GitHub.deleteWebhooks("schedule-trigger");
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
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.EU
    );

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Verify component deployment", () => {
    ComponentListingPage.visitToAComponent(SCHEDULE_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployScheduleTask();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteScheduleTask();
  });

  it("Verify task execution in observability ", () => {
    ComponentOverviewPage.navigateToObserve();
    ComponentObservePage.gotoLogs(MEDIUM_TIME);
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
