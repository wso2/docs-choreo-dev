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

import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GitHub } from "../../../support/github/github";
import { ComponentData } from "../../../support/interfaces/component-data";

describe("Graphql GQL service test", () => {
  const PROJECT_DESCRIPTION = "sample gql service";
  const PROJECT_NAME = Utils.generateProjectName();
  const TEST_QUERY = '{greeting(name:"John")}';
  const TEST_QUERY_RESPONSE = 'greeting": "Hello, John';
  const COMPONENT_NAME = Utils.generateComponentName();
  const REPO_NAME = "graphql-service-sample";
  const ENDPOINT_NAME = "Endpoint 8090";
  const subPath = Cypress.env("branch").replace("-ci", "");

  before(() => {
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify GraphQL sample creation", () => {
    GitHub.deleteRepoContent(REPO_NAME);
    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.ballerinaService,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      sampleTemplate: "choreo/graphql_service:3.1.1",
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: `https://github.com/choreo-test-apps/graphql-service-sample/tree/main/${subPath}`,
      initializeAsBallerinaProject: true,
      repositoryType: Enums.RepoType.UserManagedEmpty,
      repositorySubPath: subPath,
    };
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.US
    );
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

  it("Verify component deployment", () => {
    ComponentDeployPage.deployService(
      PROJECT_NAME,
      COMPONENT_NAME,
      ENDPOINT_NAME,
      true
    );
  });

  it("Verify test functionality of GQL query in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testGraphQL(Enums.Environment.DEVELOPMENT, TEST_QUERY);
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE);
  });

  it("Verify suspending deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopSingleDevContainer();
  });
});
