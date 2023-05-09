/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Utils } from "../../../support/commons/utils";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";

describe("Create proxy api using existing url", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_Name = Utils.generateComponentName("oas");
  const API_BASE_PATH = Utils.generateBasePath();
  const URL = "https://petstore.swagger.io/v2/swagger.json";

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Creating and publishing an API from open API specification", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.createOpenApi("", URL);
    RestAPIProxyTemplate.enterAPIdetails(
      API_Name,
      API_BASE_PATH,
      `${URL}/v2`,
      "1.0.0",
      "pet/{petId}",
      ""
    );
  });
});
