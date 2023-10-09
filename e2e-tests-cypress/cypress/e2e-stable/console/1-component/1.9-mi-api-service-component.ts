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

import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { IntegrationComponentData } from "../../../support/interfaces/integration-component-data";

describe("Verify MI API SERVICE component in root", () => {
  const PROJECT_DESCRIPTION = "MI API SERVICE Test";
  const PROJECT_NAME = Utils.generateProjectName();
  const COMPONENT_NAME = Utils.generateComponentName("miApiService");
  const MATCHING_STRING = "Hello Integration";
  const ENDPOINT_NAME = "HelloWorld";

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.US
    );
  });

  it("Verify REST API component creation", () => {
    let componentData: IntegrationComponentData = {
      componentName: COMPONENT_NAME,
      componentType: Enums.ComponentType.MI_API_SERVICE,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      srcGitRepoUrl:
        "https://github.com/choreo-test-apps/synaps-api-project-sample",
      repositorySubPath: "",
      oasFilePath: "",
      srcGitRepoBranch: "with-response-message",
    };

    GraphQL.createIntegrationComponent(componentData);
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment with project level endpoint", () => {
    ComponentDeployPage.deployService(PROJECT_NAME,COMPONENT_NAME,ENDPOINT_NAME, false, true);
  });

  it("Verify manage page for project level endpoint", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.verifyOverviewForProjectLevelEndpoints();
  });

  it("Verify component deployment with public level endpoint", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployService(PROJECT_NAME,COMPONENT_NAME,ENDPOINT_NAME, true, true);
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.DEVELOPMENT,
      ENDPOINT_NAME,
      "",
      "",
      ""
    ).then((res) => {
      expect(res.response).to.include(MATCHING_STRING);
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteService(ENDPOINT_NAME, true, 0, true);
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.PRODUCTION,
      ENDPOINT_NAME,
      "",
      "",
      ""
    ).then((res) => {
      expect(res.response).to.include(MATCHING_STRING);
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify suspending Prod and Dev deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
