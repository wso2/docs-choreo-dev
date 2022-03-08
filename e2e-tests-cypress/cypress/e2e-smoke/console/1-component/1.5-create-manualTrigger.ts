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
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTemplate } from "../../../support/console/pages/enum/component-template";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { TriggersTemplate } from "../../../support/console/pages/templates/manualTrigger-creation-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { VSSourceControl } from "../../../support/console/pages/vscod-editor/vs-source-control";
import { Utils } from "../../../support/console/utils";

describe("Verify manual trigger creation functionality", () => {
  const MANUAL_NAME = Utils.generateComponentName("ManualTrigger");
  const FILE_ID = "manualTrigger";
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Manual Trigger";

  after(()=>{
    ChoreoHomePage.navigateToHome()
  })

  it("Verify manual trigger component creation", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    TriggersTemplate.selectManualTriggerTemplate();
    TriggersTemplate.createManualTriggerFromTemplate(MANUAL_NAME, FILE_ID);
    ComponentDevelopPage.getComponentURL(FILE_ID);
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deploy();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
  });


});
