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
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../../support/interfaces/component-data";


describe("Create Reusable Webhook functionality", () => {
  const CONFIG = "pkKgDNr5vGND364IsHzwGM7O";
  const WEBHOOK_NAME = "create-Reuse-slackTrigger-1.4.1";
  const PROJECT_NAME = "Default Project"

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify webhook component creation", () => {
    let componentData: ComponentData = {
      componentName: WEBHOOK_NAME,
      displayType: Enums.DisplayType.webhook,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "AppService",
      triggerId: "126",
      srcGitRepoUrl: "https://github.com/choreo-test-apps/slack-web-hook",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };
    ProjectListingPage.selectProject();
    ProjectOverviewPage.searchReuseComponent(componentData);
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(WEBHOOK_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Deploy the component", () => {
    ComponentDeployPage.configureAndDeploy(CONFIG);
  });

  it("Component promotion to prod", () => {
    ComponentDeployPage.promoteWebHookToProd(CONFIG, false);
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
  });

  it("Verify suspending Dev deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopDevContainer();
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopProdContainer();
  });
});
