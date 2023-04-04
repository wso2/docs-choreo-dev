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
/// <reference types="cypress-xpath" />

import { DomainsComponents } from "../../support/console/pages/component/common/domains-components";
import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { ApiOverview } from "../../support/devportal/pages/apis/api-overview";
import { Utils } from "../../support/console/utils";
import { ApiCredentials } from "../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../support/devportal/pages/apis/try-out";
import { LoginPage as ConsoleLoginPage } from "../../support/console/pages/login-page";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { ProductionKeys } from "../../support/devportal/pages/applications/production-keys";
import { Subscriptions } from "../../support/devportal/pages/applications/subscriptions";
import { generateAppName } from "../../support/devportal/utils";
import { APISdk } from "../../support/devportal/pages/apis/api-sdk";
import { ProjectOverviewPage } from "../../support/console/pages/projects/project-overview";
import { RestAPIProxyTemplate } from "../../support/console/pages/templates/rest-api-proxy-temp";
import { ComponentOverviewPage } from "../../support/console/pages/component/component-overview-page";
import { APIDeployment } from "../../support/console/pages/apis/api-deployment";
import { ProjectListingPage } from "../../support/console/pages/projects/projects-listing-page";
import { ComponentAPILifecycle } from "../../support/console/pages/component/component-manage-page";

const CUSTOM_DOMAIN = Cypress.env("devportalCustomDomain");
const API_BASE_PATH = Utils.generateBasePath();
const Filepath = "apis/generation_oas.yaml";
const API_NAME = Utils.generateComponentName("oas")
const PROJECT_DESCRIPTION = "sample oas flow scenario";
const PROJECT_NAME = Utils.generateProjectName();


describe("Create and deploy a component to test developer portal with custom domain", () => {
  before(() => {
    ConsoleLoginPage.login();
  });

  it("Create and deploy a component", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.createOpenApi(Filepath);
    RestAPIProxyTemplate.enterAPIdetails(API_NAME, API_BASE_PATH, "", "", "");
    cy.task('setAPIName', API_NAME);
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.PromoteToProd()
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  });

  it("Add a developer portal custom domain", () => {
    ChoreoHomePage.navigateToSettings();
    DomainsComponents.navigateToDomainsSettings();
    DomainsComponents.navigateToDevPortalCustomDomain();
    DomainsComponents.deleteDevportalDomainIfExists(CUSTOM_DOMAIN);
    DomainsComponents.createDevportalDomain(CUSTOM_DOMAIN);
  });

  after(() => {
    ChoreoHomePage.logout();
  });
});

describe.skip("Login and test developer portal with custom domain", () => {
  const OPERATION_USERS = "intensity";
  const appName = generateAppName("-e2etest");

  before(() => {
    DevportalLoginPage.loginToDevportal(CUSTOM_DOMAIN);
  });

  after(() => {
    DevPortalHomePage.logout();
  });

  it("Test in devportal", () => {
    cy.task("getAPIName").then((apiName) => {
      let API_Name = apiName as string;
      DevPortalHomePage.navigateToApisPage();
      Apis.searchApiAndSelect(API_Name);
      DevPortalHomePage.navigateToPerApiView(API_Name);
      Apis.verifyAPIname().should("eq", API_Name);
    });
  });

  it("Add and delete comment for the API", () => {
    ApiOverview.addCommentToApi("Test comment from Cypress Test Runner");
    ApiOverview.deleteComment();
  });

  it("Add and modify ratings of the API", () => {
    ApiOverview.openRatings();
    ApiOverview.addRatings();
    ApiOverview.validateRating();
  });

  it("Generate credentials and tryout the API", () => {
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials();
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.SelectResource("GET", OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  });

  it("Verify the downloaded SDK file", () => {
    cy.task("getAPIName").then((API_Name) => {
      const sdkFile = API_Name + "_1.0.0_android.zip";
      APISdk.downloadSDK(sdkFile);
    });
  });

  it("Create a consumer application and tryout an API", () => {
    cy.task("getAPIName").then((an) => {
      let API_Name = an as string;
      DevPortalHomePage.navigateToAppsPage();
      AppsList.createAnApplication(appName);
      ProductionKeys.generateTestToken();
      Subscriptions.addSubscriptionToApplication(API_Name);
      Subscriptions.validateResubscribingApi(API_Name);
    });
  });

  it("Delete a consumer application", () => {
    TryOut.DeleteApplication(appName);
  });
});

describe("Delete added custom domain", () => {
  before(() => {
    ConsoleLoginPage.login();
    ChoreoHomePage.navigateToSettings();
    DomainsComponents.navigateToDomainsSettings();
    DomainsComponents.navigateToDevPortalCustomDomain();
  });

  it("Delete added custom domain", () => {
    DomainsComponents.deleteCreatedCustomDomain(CUSTOM_DOMAIN);
  });
});
