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
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { WebappComponent } from "../../../support/interfaces/choreo-components/webapp-component";

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe("Verify containerized service functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("WebApp-service");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Webapp SPA service";
  const REPO_NAME = Utils.generateComponentName("repo");


  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify Webapp service component creation", () => {
    let componentData: WebappComponent = {
      name: COMPONENT_NAME,
      displayName: COMPONENT_NAME,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocWebAppsDockerfileLess,
      description: "Web app Component",
      labels: "",
      projectId: "",
      byocWebAppsConfig: {
        dockerContext: "react-spa",
        srcGitRepoUrl: "https://github.com/choreo-test-apps/web-apps",
        srcGitRepoBranch: "main",
        webAppType: "React",
        webAppBuildCommand: "npm run build",
        webAppPackageManagerVersion: "18",
        webAppOutputDirectory: "build",
      },
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getWebAppComponentCreationQuery
    );
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment to dev", () => {
    ComponentDeployPage.deployToDev(PROJECT_NAME,COMPONENT_NAME);
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test page is disabled", () => {
    cy.get('[data-cyid="link-test"]').should('be.disabled')
  });

  it("Verify manage page is disabled", () => {
    cy.get('[data-cyid="link-manage"]').should('be.disabled')
  });

  it("Verify suspending all component deployments", () => {
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
