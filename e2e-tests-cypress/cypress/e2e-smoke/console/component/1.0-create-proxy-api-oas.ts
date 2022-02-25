import { RandomTextGenerator } from "../../../support/console/pages/component/common/random-text-generator";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { Utils } from "../../../support/console/utils";

describe.skip("Create proxy api using existing url", () => {
  const FILE_ID = "1.0-create-proxy-api-oas";
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_Name = RandomTextGenerator.generateApiName("oas");
  const URL = "https://petstore.swagger.io/v2/swagger.json";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating and publishing an API from open API specification", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.createOpenApi("", URL);
    RestAPIProxyTemplate.enterAPIdetails(API_Name,API_Name,`${URL}/v2`)
  });
});
