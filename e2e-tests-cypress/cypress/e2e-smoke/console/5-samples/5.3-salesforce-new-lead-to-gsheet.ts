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

import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { SalesforceNewLeadToGsheet } from "../../../support/console/pages/samples/salesforceNewLeadToGsheet";
import { Utils } from "../../../support/console/utils";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { Environment } from "../../../support/console/pages/enum/environment";

describe("Create salesforce new lead to gsheet sample in Choreo", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const COMPONENT_NAME = Utils.generateComponentName("rest-SF");
  const COMPONENT_DESCRIPTION = "Salesforce-trigger";
  const NEW_BRANCH = "feature";
  const commitMessage = "adding new service";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });
  it("Creating a project and add Salesforce new lead to gsheet sample", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    SalesforceNewLeadToGsheet.selectSample();
    SalesforceNewLeadToGsheet.searchSample();
    ComponentDevelopPage.getComponentURL();
  });

  it("Verify component deployment with configurables", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeploySalesforceToGsheet(
      Cypress.env("sfUsername"),
      Cypress.env("sfPassword"),
      Cypress.env("salesforceOAuthConfig"),
      Cypress.env("sfclientId"),
      Cypress.env("sfclientSecret"),
      Cypress.env("sfrefreshToken"),
      Cypress.env("gsworksheetName"),
      Cypress.env("sfrefreshUrl"),
      Cypress.env("sfsalesforceBaseUrl"),
      Cypress.env("gsClientId"),
      Cypress.env("gsclientSecret"),
      Cypress.env("gsrefreshToken"),
      Cypress.env("gsrefreshUrl"),
      Cypress.env("gsspreadsheetId")
      );
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
  });

  it("Verify component with configurables promote to prod", () => {
    ComponentDeployPage.promoteConfigDeployment();
  });

  it("Verify the template by creating a new salesforce lead", () => {
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(COMPONENT_NAME, COMPONENT_DESCRIPTION);
    ComponentDevelopPage.getComponentURL();
// Edit in code server
    ComponentOverviewPage.navigateToDevelop();
    LoginPage.navigateToCodespace();
    VSExplorer.creteNewBranch(NEW_BRANCH);
    VSExplorer.pasteCode("salesforce.bal");
    VSExplorer.commitPush(commitMessage,true);
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

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.DEVELOPMENT,
      "createLead",
      "Lead",
      "sample-Lead"
    ).then((res) => {
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.PRODUCTION,
      "createLead",
      "Lead",
      "sample-Lead"
    ).then((res) => {
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify suspending deployed component with configurables", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.verifyDeploymentStatus();
    ComponentDeployPage.stopAllDeployment();
  });

  it("Verify component with configurables deletion", () => {
    ComponentOverviewPage.goBack();
    ComponentListingPage.deleteComponent("Salesforce New Lead to Google Sheets Row");
});

});
