/*

Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
This software is the property of WSO2 Inc. and its suppliers, if any.
Dissemination of any information or reproduction of any material contained
herein is strictly forbidden, unless permitted by WSO2 in accordance with
the WSO2 Commercial License available at http://wso2.com/licenses.
For specific language governing the permissions and limitations under
this license, please see the license as well as any agreement you’ve
entered into with WSO2 governing the purchase of this software and any
associated services.
*/

import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { Utils } from "../../../support/console/utils";

describe("Enterprise Login using auth0Idp", () => {
  const COMPONENT_NAME = Utils.generateComponentName("rest");
  const COMPONENT_DESCRIPTION = "covid daily stats";
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();


  before(() => {
    cy.request(Cypress.env("auth0LogoutUrl"), {
      client_id: Cypress.env("auth0ClientID"),
      returnTo: Cypress.env("enterpriseLoginUrl"),
    });
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Enterprise login to console", () => {
    LoginPage.enterpriseLogin();
  });

  it("Verify REST API component creation", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(COMPONENT_NAME, COMPONENT_DESCRIPTION, true);
    ComponentDevelopPage.getComponentURL();
  });

  it("Verify vscode sso login", () => {
    ComponentOverviewPage.navigateToDevelop();
    LoginPage.navigateToCodespaceEP();
    VSExplorer.verifyVsCodeWorkspace();
  });

  it("Verify devportal sso login", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToDevPortal().should(
      "eq",
      "API Developer Portal"
    );
  });
});
