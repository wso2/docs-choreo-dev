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
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
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
  const COMPONENT_NAME = Utils.generateComponentName("containerized-service");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Webapp SPA service";
  const REPO_NAME = Utils.generateComponentName("repo");
  const ENDPOINT_NAME = "Go Greeter";

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify containerized service component creation", () => {
    let componentData: WebappComponent = {
      name: COMPONENT_NAME,
      displayName: COMPONENT_NAME,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocService,
      description: "Web app Component",
      labels: "",
      projectId: "",
      byocWebAppsConfig: {
        dockerContext: "",
        srcGitRepoUrl: "https://github.com/wso2/choreo-sample-apps/tree/main/web-apps/react-spa",
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
      GraphQLQueryBuilder.getBYOCComponentCreationQuery
    );
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment with public level endpoint", () => {
    ComponentDeployPage.deployService(PROJECT_NAME,COMPONENT_NAME,ENDPOINT_NAME);
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.DEVELOPMENT,
      ENDPOINT_NAME,
      "greeter/greet",
      "",
      "operations-greeting-get_greeter_greet"
    ).then((res) => {
      expect(res.response).to.be.eq("Hello, Stranger!\n\n");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteService(ENDPOINT_NAME);
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testManagedEndpoint(
      Enums.Environment.PRODUCTION,
      ENDPOINT_NAME,
      "greeter/greet",
      "",
      "operations-greeting-get_greeter_greet"
    ).then((res) => {
      expect(res.response).to.be.eq("Hello, Stranger!\n\n");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Navigate to component usage insights", () => {
    ChoreoHomePage.navigateToComponentUsageInsights();
  });

  it("Navigate to project usage insights", () => {
    ChoreoHomePage.navigateToProjectUsageInsights();
  });

  it("Verify API insights for dev env", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(1);
    });
  });

  it("Verify API insights for prod env", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Enums.Environment.PRODUCTION);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(1);
    });
  });

  it("Verify suspending all component deployments", () => {
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
