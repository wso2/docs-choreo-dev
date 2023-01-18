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

import { GraphQL } from "../../../support/console/apis/graphql";
import { Enums } from "../../../support/console/enums";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { GitHub } from "../../../support/github/github";
import { ComponentData } from "../../../support/interfaces/component-data";

describe("Verify webhook creation functionality", () => {
  const CONFIG = "pkKgDNr5vGND364IsHzwGM7O";
  const WEBHOOK_NAME = "create-webhook-slackTrigger-1.4";
  const REPO_NAME = Utils.generateComponentName("repo");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Slack Webhook";

  before(() => {
    GitHub.initGitHubRepo(REPO_NAME, true, true, "nanoc")
    GitHub.createNewFile(REPO_NAME, "Ballerina.toml", "cypress/fixtures/Ballerina.toml")
    GitHub.createNewFile(REPO_NAME, "gql.bal", "cypress/fixtures/slacktrigger.bal")
    GitHub.createNewFile(REPO_NAME, "Cloud.toml", "cypress/fixtures/Cloud.toml")
    LoginPage.login();

  });


  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify REST API component creation", () => {
    let componentData: ComponentData = {
      componentName: WEBHOOK_NAME,
      displayType: Enums.DisplayType.webhook,
      projectName: PROJECT_NAME,
      triggerChannels: "IssuesService",
      triggerId: "35",
      srcGitRepoUrl: GitHub.getGitHubRepoUrl(REPO_NAME)
    }
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION, Enums.Region.US);
    GraphQL.createComponentWithRepo(componentData, REPO_NAME)
  });


  it("Deploy the component", () => {
    ComponentListingPage.visitToAComponent(WEBHOOK_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeploy(CONFIG);
  });

  it("Component promotion to prod", () => {
    ComponentDeployPage.promoteWebHookToProd(CONFIG);
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    cy.get('[data-testid="feature-disable-info"]').should('be.visible');
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

