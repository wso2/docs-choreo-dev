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
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { REUSABLE_PROJECT_NAME } from "../../../support/devportal/constants";

describe("Verify manual trigger creation functionality", () => {
  const MANUAL_NAME = "create-manualTrigger-1.5";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify manual trigger component creation", () => {
    ProjectListingPage.selectProject(REUSABLE_PROJECT_NAME);
    ComponentListingPage.visitToAComponent(MANUAL_NAME);
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployManualTriggerToDev();
  });

  it("Verify component promotion to prd", () => {
    ComponentDeployPage.promoteManualTriggerToProd();
  });
});
