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

import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentBuild } from "../../../support/console/pages/component/Functionalities/Component-build";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { TestRunnerComponent } from "../../../support/interfaces/choreo-components/testrunner-component";

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe("Verify Test Runner Component functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("TestRunner-Component");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "TestRunner Go Component";
  const REPO_NAME = Utils.generateComponentName("repo");

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify test runner component creation", () => {
    let componentData: TestRunnerComponent = {
      name: COMPONENT_NAME,
      displayName: COMPONENT_NAME,
      accessibility: Enums.Accessibility.NONE,
      componentType: Enums.DisplayType.buildpackTestRunner,
      description: "Test runner Component",
      labels: "",
      projectId: "",
      oasFilePath: "",
      port: null,
      buildpackConfig: {
        buildContext: "test-runner-go",
        srcGitRepoUrl: "https://github.com/choreo-test-apps/buildPack-testrunner-Goapp",
        srcGitRepoBranch: "main",
        languageVersion: "1.x",
        buildpackId: "F9E4820E-6284-11EE-8C99-0242AC120005",
      },
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getTestRunnerComponentCreationQuery);
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);

    if (Utils.isKubeConFeaturesEnabled(false)) {
      ComponentOverviewPage.navigateToBuild();
      ComponentBuild.buildComponent();
    }

    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment to dev", () => {
    ComponentDeployPage.deployToDev(PROJECT_NAME,COMPONENT_NAME,
      true,
      true,
      false,
      false
    );
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd(false, true, 1, true);
  });

  it("Verify test page is disabled", () => {
    cy.get('[data-cyid="link-test"]').should("have.attr", "disabled");
  });

  it("Verify manage page is disabled", () => {
    cy.get('[data-cyid="link-manage"]').should("have.attr", "disabled");
  });

  it("Verify suspending all component deployments", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
