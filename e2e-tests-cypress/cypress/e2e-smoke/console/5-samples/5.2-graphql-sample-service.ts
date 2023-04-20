import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { Enums } from "../../../support/console/enums";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { GitHub } from "../../../support/github/github";
import { ComponentData } from "../../../support/interfaces/component-data";

describe("Graphql GQL service test", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const TEST_QUERY = '{greeting(name:"John")}';
  const TEST_QUERY_RESPONSE = "Hello, John";
  const TEST_MUTATION = 'mutation{createUser(name:"John")}';
  const TEST_MUTATION_RESPONSE = 'createUser": "User created with name: John';
  const COMPONENT_NAME = "graphql-sample";
  const REPO_NAME = "graphql-service-sample";
  const subPath = Cypress.env("branch").replace("-ci", "");

  before(() => {
    LoginPage.login();
    GitHub.deleteRepoContent(REPO_NAME);
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify GraphQL sample creation", () => {

    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.graphql,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      sampleTemplate: "choreo/graphql_service:3.1.0",
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
    GraphQL.createComponent(PROJECT_NAME, REPO_NAME, componentData, GraphQLQueryBuilder.getRestComponentCreationQuery)
  });



  it("Verify component deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality of GQL query in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testDevOnGraphQL(TEST_QUERY);
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE);
  });

  it("Verify test functionality of GQL query in Prod on swagger", () => {
    TestHelper.testProdOnGraphQL(TEST_QUERY);
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE);
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
