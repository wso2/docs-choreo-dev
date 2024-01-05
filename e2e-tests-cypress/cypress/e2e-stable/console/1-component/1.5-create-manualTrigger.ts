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

import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { ComponentExecutePage } from "../../../support/console/pages/component/UI-components/Component-execute-page";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/concepts/project/project";
import { ManualTrigger } from "../../../support/console/concepts/component/service/manualTrigger-component";


after(() => {
  console.logout();
});


describe("Verify manual trigger creation functionality", () => {
  const MANUAL_NAME = Utils.generateComponentName();
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Manual Trigger";
  let project: Project;
  let component: ManualTrigger;

  it("Login to Console", () => {
    console.login();
  });

 
  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });


  it("Verify Manual Trigger component creation", () => {
    project
      .createManualTriggerComponent(
        Enums.Accessibility.EXTERNAL,
        {
          url: "https://github.com/choreo-test-apps/manual-trigger",
          branch: "main",
        },
        
      )
      .then((ManualTriggerComponent: ManualTrigger) => {
        component = ManualTriggerComponent;
      });
  });

  
  it("Build the component", () => {
    component.buildComponent();
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployToDevWithoutSplitButton(
      PROJECT_NAME,
      MANUAL_NAME,
      false,
      false,
      true
    );
  });

  it("Verify component promotion to prod", () => {
    ComponentDeployPage.promoteManualTriggerToProd();
  });

  it("Verify execution in dev", () => {
    ComponentOverviewPage.navigateToExecute();
    ComponentExecutePage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentExecutePage.verifyExecution();
  });

  it("Verify execution in prod", () => {
    ComponentOverviewPage.navigateToExecute();
    ComponentExecutePage.selectEnvironment(Enums.Environment.PRODUCTION);
    ComponentExecutePage.verifyExecution();
  });
});
