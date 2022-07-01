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

import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { ComponentTemplate } from "../../../support/console/pages/enum/component-template";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { TriggersTemplate } from "../../../support/console/pages/templates/slackTrigger-creation-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { VSSourceControl } from "../../../support/console/pages/vscod-editor/vs-source-control";
import { Utils } from "../../../support/console/utils";

describe("Verify webhook creation functionality", () => {
  const WEBHOOK_NAME = Utils.generateComponentName("SlackHook");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Slack Trigger";
  const LABELS = ["IT Operations/Testing Tools", "IT Operations/Debug Tools"];
  const COMMIT_MESSAGE = "adding slacktrigger bal file";
  const CONFIG = "pkKgDNr5vGND364IsHzwGM7O";
  const TRIGGER_TYPE = "Slack";
  const TRIGGER_CHANNEL = "SlackEventsAppService";
  const it_privatedp = Cypress.env("isPrivateOrg") ? it : it.skip;

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify new project creation", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
  });

  it("Verify slack trigger creation", () => {
    TriggersTemplate.SelectWebhookTemplate();
    TriggersTemplate.createTrigger(TRIGGER_TYPE, WEBHOOK_NAME, TRIGGER_CHANNEL);
    ComponentDevelopPage.getComponentURL();
  });
  it("Edit code in VScode", () => {
    LoginPage.navigateToCodespace();
    VSExplorer.typeCode("slacktrigger.bal");
    VSExplorer.selectSourceControl();

    VSExplorer.enterCommandInTerminal(
      "bash /config/workspace/.githooks/pre-commit"
    );
    VSExplorer.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit"
    );
    VSSourceControl.commitChanges(COMMIT_MESSAGE);
    VSExplorer.enterCommandInTerminal("git push");
  });

  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo();
    // ComponentDevelopPage.addLabels(LABELS).then((arr) => {
    //   expect(arr).to.deep.eq(LABELS);
    // });
    ComponentDevelopPage.verifyLatestCommit(COMMIT_MESSAGE);
  });

  it("Deploy the component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeploy(CONFIG);
    ComponentDeployPage.verifyDevInvokeURL().should("not.be.null");
  });

  it_privatedp("Component promotion to stg", () => {
    ComponentDeployPage.promoteWebHookToSTG(CONFIG);
    ComponentDeployPage.verifyProdInvokeURL().should("not.be.null");
  });

  it("Component promotion to prod", () => {
    ComponentDeployPage.promoteWebHookToProd(CONFIG);
    ComponentDeployPage.verifyProdInvokeURL().should("not.be.null");
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    cy.contains("This feature is disabled for webhook components.").should(
      "be.visible"
    );
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

