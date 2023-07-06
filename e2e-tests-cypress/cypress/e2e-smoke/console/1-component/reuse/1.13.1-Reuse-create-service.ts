/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { Enums } from "../../../../support/commons/enums";
import { TestHelper } from "../../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../../support/interfaces/component-data";


before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe("Verify Ballerina service functionality", () => {
  const COMPONENT_NAME = "create-ReuseService-1.13.1";
  const PROJECT_NAME = "Default Project";
  const ENDPOINT_NAME = "Readinglist";


  it("Verify Ballerina service component creation", () => {
    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.ballerinaService,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/byor-service-app1",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };
    ProjectListingPage.selectProject();
    ProjectOverviewPage.searchReuseComponent(componentData);
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment with public level endpoint", () => {
    ComponentDeployPage.reDeployService(PROJECT_NAME,COMPONENT_NAME,ENDPOINT_NAME, true);
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.DEVELOPMENT,
      "Readinglist",
      "books",
      "get",
      "operations-default-getBooks"
    ).then((res) => {
      expect(res.response).to.be.eq("[]");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteService(ENDPOINT_NAME, true);
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.PRODUCTION,
      "Readinglist",
      "books",
      "get",
      "operations-default-getBooks"
    ).then((res) => {
      expect(res.response).to.be.eq("[]");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishServiceToMarketplace();
  });

  it("Verify suspending all component deployments", () => {
    ComponentAPILifecycle.demoteToCreated();
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});

