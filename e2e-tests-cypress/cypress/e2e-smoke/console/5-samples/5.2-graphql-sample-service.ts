import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GreetingSample } from "../../../support/console/pages/samples/greeting";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { Utils } from "../../../support/console/utils";

describe("Graphql GQL service test", () => {


  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const commitMessage = "adding new service";


  const TEST_QUERY = '{greeting(name:"John")}'
  const TEST_QUERY_RESPONSE="Hello, John"
  const TEST_MUTATION='mutation{createUser(name:"John")}'
  const TEST_MUTATION_RESPONSE='createUser": "User created with name: John'

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });
  it("Creating a project and add GQL sample", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION
    );
    ProjectOverviewPage.addNewComponent();
    GreetingSample.selectSample("GraphQL Service")
    ComponentDevelopPage.getComponentURL();
  });



  it("Edit code in VScode", () => {
    ComponentOverviewPage.navigateToDevelop();
    LoginPage.navigateToCodespace();

    VSExplorer.pasteCode("gqlservice.bal");
    VSExplorer.commitPush(commitMessage);
  });


  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo();

    ComponentDevelopPage.refreshBranchCommit()
    ComponentDevelopPage.verifyLatestCommit(commitMessage);
  });


  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.eq", "");
  });


  it("Verify test functionality of GQL query in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnGraphiQL(Environment.DEVELOPMENT, TEST_QUERY)
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE)

  });


  it("Verify test functionality of GQL query in Prod on swagger", () => {
    TestHelper.testOnGraphiQL(Environment.PRODUCTION, TEST_QUERY)
    TestHelper.getGqlResult(TEST_QUERY_RESPONSE)
  });



  it("Verify test functionality of GQL mutation in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnGraphiQL(Environment.DEVELOPMENT, TEST_MUTATION)
    TestHelper.getGqlResult(TEST_MUTATION_RESPONSE)

  });


  it("Verify test functionality of GQL mutation in Prod on swagger", () => {
    TestHelper.testOnGraphiQL(Environment.PRODUCTION, TEST_MUTATION)
    TestHelper.getGqlResult(TEST_MUTATION_RESPONSE)
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

})