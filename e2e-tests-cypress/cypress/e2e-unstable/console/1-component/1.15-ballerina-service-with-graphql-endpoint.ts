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
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GitHub } from "../../../support/github/github";
import { ComponentData } from "../../../support/interfaces/component-data";

before(() => {
  LoginPage.login();
});
after(() => {
  ChoreoHomePage.logout();
});

describe(`Graphql GQL service functionality`, () => {
  const PROJECT_DESCRIPTION = "ballerina service with graphql endpoint";
  const PROJECT_NAME = Utils.generateProjectName();
  const TEST_QUERY = '{greeting(name:"John")}';
  const TEST_QUERY_RESPONSE = 'greeting": "Hello, John';
  const TEST_MUTATION = 'mutation{createUser(name:"John")}';
  const TEST_MUTATION_RESPONSE = 'createUser": "User created with name: John';
  const COMPONENT_NAME = Utils.generateComponentName();
  const REPO_NAME = "graphql-service-sample";
  const ENDPOINT_NAME = "GraphQL Greet";

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify GraphQL component creation", () => {
    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.ballerinaService,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/gql-service",
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

  it("Verify component deployment", () => {
    ComponentDeployPage.deployService(PROJECT_NAME,COMPONENT_NAME,ENDPOINT_NAME);
  });

  it("Verify test functionality of GQL query in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testGraphQL(
      Enums.Environment.DEVELOPMENT,
      TEST_QUERY,
      ENDPOINT_NAME
    );
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE);
  });

  it("Verify test functionality of GQL mutation in dev on swagger", () => {
    TestHelper.testGraphQL(
      Enums.Environment.DEVELOPMENT,
      TEST_MUTATION,
      ENDPOINT_NAME
    );
    TestHelper.getGqlResult(TEST_MUTATION_RESPONSE);
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteService(ENDPOINT_NAME);
  });

  it("Verify test functionality of GQL query in Prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testGraphQL(
      Enums.Environment.PRODUCTION,
      TEST_QUERY,
      ENDPOINT_NAME
    );
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE);
  });

  it("Verify test functionality of GQL mutation in Prod on swagger", () => {
    TestHelper.testGraphQL(
      Enums.Environment.PRODUCTION,
      TEST_MUTATION,
      ENDPOINT_NAME
    );
    TestHelper.getGqlResult(TEST_MUTATION_RESPONSE);
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
