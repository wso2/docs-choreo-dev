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
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { Utils } from "../../../support/console/utils";

describe("Verify performance forecaster functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("rest");
  const COMPONENT_DESCRIPTION = "Performance Forecaster";
  const PROJECT_DESCRIPTION = "Performance Forecaster";
  const PROJECT_NAME = Utils.generateProjectName();

  before(() => LoginPage.login());
  after(() => ChoreoHomePage.logout());

  it("Verify REST API component creation", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(
      COMPONENT_NAME,
      COMPONENT_DESCRIPTION
    );
    ComponentDevelopPage.getComponentURL();
  });

  it("Edit code in VScode", () => {
    LoginPage.navigateToCodespace();
    VSExplorer.typeCode("perf-analyzer.bal");
    VSExplorer.matchCodeLense(1, /^Forecasted latency between \d+\.?\d*  (ms|s) - \d+\.?\d*  (ms|s) \(for concurrency \d+ - \d+\)/);
    VSExplorer.getCodeLense(1).click();
    VSExplorer.matchCodeLense(2, /^Forecasted latency \d+\.?\d*  (ms|s) \(for concurrency 1\)/);
    VSExplorer.matchCodeLense(3, /^Forecasted latency \d+\.?\d*  (ms|s) \(for concurrency 1\)/);
    cy.wait(4000);
    VSExplorer.clickPerfGraph(1);
    VSExplorer.matchCodeLense(1, /^Forecasted latency \d+\.?\d*  (ms|s) \(for concurrency 25\)/);
    VSExplorer.matchCodeLense(2, /^Forecasted latency \d+\.?\d*  (ms|s) \(for concurrency 25\)/);
    VSExplorer.matchCodeLense(3, /^Forecasted latency \d+\.?\d*  (ms|s) \(for concurrency 25\)/);

  });
});
