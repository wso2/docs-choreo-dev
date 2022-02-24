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
  const WEBHOOK_NAME = "Slack Trigger";
  const FILE_ID = "slacktrigger";
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Slack Trigger";
  const labels = ["IT Operations/Testing Tools", "IT Operations/Debug Tools"];
  const commitMessage = "adding slacktrigger bal file";

  before(() => LoginPage.loginToChoreo(FILE_ID));
  after(() => ChoreoHomePage.logout());
  it("Verify slack trigger creation", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    TriggersTemplate.SelectWebhookTemplate();
    TriggersTemplate.createSlackTriggerFromTemplate(WEBHOOK_NAME, FILE_ID);

    ComponentDevelopPage.getComponentURL(FILE_ID);
  });

  it("Edit code in VScode", () => {
    LoginPage.navigateToCodespace(FILE_ID);
    VSExplorer.typeCode("slacktrigger.bal", ComponentTemplate.WEBHOOK);
    VSExplorer.selectSourceControl();

    VSExplorer.enterCommandInTerminal(
      "bash /config/workspace/.githooks/pre-commit"
    );
    VSExplorer.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit"
    );
    VSSourceControl.commitChanges(commitMessage);
    VSExplorer.enterCommandInTerminal("git push");
    VSExplorer.waitTillCodeSyncWithChoreo();
  });

  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo(FILE_ID);
    ComponentDevelopPage.addLabels(labels).then((arr) => {
      expect(arr).to.deep.eq(labels);
    });
    ComponentDevelopPage.verifyLatestCommit(commitMessage);
  });

  it("Deploy the component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeploy("pkKgDNr5vGND364IsHzwGM7O");
    ComponentDeployPage.verifyDevInvokeURL().should("not.be.null");
  });

  it("Component promotion to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.be.null");
  });

  it("Verify test functionality in Dev env", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    cy.contains("This feature is disabled for webhook components.").should(
      "be.visible"
    );
  });

  it.skip("Delete created project", () => {
    ChoreoHomePage.selectHomeMenu();
    ChoreoHomePage.navigateToComponents();
    ProjectListingPage.selectProject(FILE_ID);
  });


});
