import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Enums } from "../../../support/console/enums";

import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { IntegrationComponentData } from "../../../support/interfaces/integration-component-data";

describe("Verify MI REST API component in root", () => {
  const PROJECT_DESCRIPTION = "MI REST API Test";
  const PROJECT_NAME = Utils.generateProjectName();
  const COMPONENT_NAME = Utils.generateComponentName("miRest");
  const RESOURCE_NAME = "HelloWorld";
  const MATCHING_STRING = "Hello Integration";

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify REST API component creation", () => {
    let componentData: IntegrationComponentData = {
      componentName: COMPONENT_NAME,
      componentType: Enums.ComponentType.MI_REST_API,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/synaps-api-project-sample",
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      oasFilePath: "",
      branch: "with-response-message"
    };
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.EU
    );
    GraphQL.createIntegrationComponent(componentData);
  });

  it("Deploy component", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev(false); //This is set to false till the env config support is provided for miRestApi
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd(false); //This is set to false till the env config support is provided for miRestApi
  });

  it("Verify test functionality of root resource in dev on curl", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body.message).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality of root resource in prod on curl", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body.message).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify suspending Prod and Dev deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
