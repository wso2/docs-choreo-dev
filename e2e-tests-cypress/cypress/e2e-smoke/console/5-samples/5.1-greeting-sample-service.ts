/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GitHub } from "../../../support/github/github";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";

describe("Create Greeting sample in Choreo", () => {
  const PROJECT_DESCRIPTION = "sample greeting service";
  const PROJECT_NAME = Utils.generateProjectName();
  const COMPONENT_NAME = Utils.generateComponentName()
  const REPO_NAME = "hello-world-sample";
  before(() => {
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify Hello World sample creation", () => {
    const subPath = Cypress.env("branch").replace("-ci", "");
    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.restAPI,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      sampleTemplate: "choreo/greeting_service:3.1.0",
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: `https://github.com/choreo-test-apps/hello-world-sample/tree/main/${subPath}`,
      initializeAsBallerinaProject: true,
      repositoryType: Enums.RepoType.UserManagedEmpty,
      repositorySubPath: subPath,
    };
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.US
    );
    GraphQL.createComponent(PROJECT_NAME, REPO_NAME, componentData, GraphQLQueryBuilder.getRestComponentCreationQuery)
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployToDev(PROJECT_NAME,COMPONENT_NAME);
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality of sample resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource("");
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue("name", "dasun");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Verify suspending Dev deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopDevContainer();
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentDeployPage.stopProdContainer();
  });
  it("Verify component deletion", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.deleteComponent(COMPONENT_NAME);
  });
});
