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
import { WebappComponent } from "../../../support/interfaces/choreo-components/webapp-component";
import { UserstoreManagerService } from "../../../support/console/apis/userstore-mgt-service";

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

  // Removing existing userstores
  it("Removing existing userstores", () => {
    const { uuid } = Cypress.env("userData");
    cy.log("orgId: ", uuid);
    UserstoreManagerService.getUserstores(uuid).then((response) => {
      cy.log(`Found ${response.body.length} userstores`);
      if (response.body.length > 0) {
        response.body.forEach((element) => {
          cy.log(`Deleting userstore ${element.userStoreId}`)
          UserstoreManagerService.deleteUserstore(element.userStoreId);
        });
      }
      response.body.forEach(element => {
        cy.log("element: ", element.userStoreId);
      });
    });
  });

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
        dockerContext: "cloud-native-app-developer/reading-list-front-end",
        srcGitRepoUrl: "https://github.com/rajithacharith/choreo-examples",
        srcGitRepoBranch: "app-gw",
        webAppType: "React",
        webAppBuildCommand: "npm install && npm run build",
        webAppPackageManagerVersion: "18",
        webAppOutputDirectory: "dist",
        isAppGatewayEnabled: true,
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
    ProjectListingPage.selectProject(PROJECT_NAME);
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    if (Utils.isBuildDeployEnabled()) {
      ComponentOverviewPage.navigateToBuild();
      ComponentBuild.buildComponent();
    }
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment to dev", () => {
    ComponentDeployPage.deployToDev(
      PROJECT_NAME,
      COMPONENT_NAME,
      true,
      true,
      false,
      true
    );
    ComponentDeployPage.promoteToProd(false, true, 1, true);
  });

  it("Retrieve webapp url", () => {
    ComponentOverviewPage.navigateToOverview();
    ComponentOverviewPage.getDeployedURLofInitialEnv("webAppUrl");
    cy.get("@webAppUrl").then((url) => {
      Cypress.env("webAppUrl", url.toString());
    });
  });

  it("Access webapp and login", () => {
    cy.origin(Cypress.env("webAppUrl"), () => {
      cy.visit('/');
      cy.contains('button', 'Login').click();
    });

    cy.url().then((url) => {
      let uri = new URL(url.toString());
      let hostname = uri.hostname;
      cy.origin(hostname, () => {
        cy.get('input[id="username"]').should("be.visible");
        cy.get('input[id="username"]').type(Cypress.env("demoUserUsername-dev"));
        cy.get('input[id="password"]').type(Cypress.env("demoUserPassword-dev"));
        cy.contains('button', 'Sign In').click();
      });
    });

    cy.origin(Cypress.env("webAppUrl"), () => {
      cy.contains('p', 'Reading List');
      cy.contains('button', 'Logout').click();
    });
  });

});
