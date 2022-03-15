import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GreetingSample } from "../../../support/console/pages/samples/greeting";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { Utils } from "../../../support/console/utils";

describe.skip("Create Greeting sample in Choreo", () => {
  const FILE_ID = "oasflow";
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_NAME = Utils.generateComponentName("oas");
  const API_BASE_PATH = Utils.generateBasePath();
  const Filepath = "apis/generation_oas.yaml";
  const idpUser = "choreoe2etest";
  before(() => {
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });
  it("Creating a project and add Greeting sample", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    GreetingSample.selectSample()
  });


});
