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
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../support/interfaces/component-data";

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe("Verify Ballerina service functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("ballerina-service");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "sample ballerina service scenario";
  const REPO_NAME = Utils.generateComponentName("repo");
  const ENDPOINT_NAME = "Readinglist";

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

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

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Initiate service deployment.", () => {
    ComponentDeployPage.initiateServiceDeployment("project");
    ComponentDeployPage.getVisibilityLevel("project").should(
      "equal",
      "Project"
    );
  });

  it("Deploy the service", () => {
      ComponentDeployPage.deployServiceWithVisibilityLevel();
      ComponentDeployPage.verifyDeploymentStatusOfService(PROJECT_NAME,COMPONENT_NAME).should('equal','Active');
  });
});
