/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { ApiOverview } from "../../support/devportal/pages/apis/api-overview";
import { ComponentAPILifecycle } from "../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../support/console/pages/component/component-overview-page";
import { ApiCredentials } from "../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../support/devportal/pages/apis/try-out";
import { LoginPage } from "../../support/console/pages/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { Subscriptions } from "../../support/devportal/pages/applications/subscriptions";
import { generateAppName } from "../../support/devportal/utils";
import { ComponentDeployPage } from "../../support/console/pages/component/component-deploy";
import { APISdk } from "../../support/devportal/pages/apis/api-sdk";
import { DevPortalHelper } from "../../support/devportal/helpers/devportal-helper";
import { Utils } from "../../support/commons/utils";
import { ComponentListingPage } from "../../support/console/pages/component/component-listing-page";
import { ProjectListingPage } from "../../support/console/pages/projects/projects-listing-page";
import { Enums } from "../../support/commons/enums";

describe("API overview comment and rating scenario", () => {
  const API_Name = Utils.generateComponentName("oas");
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const idpUser = "choreoe2etest";
  const OPERATION_USERS = "intensity";
  const appName = generateAppName("-e2etest");
  const sdkFile = API_Name + "_v1.0_android.zip";

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Test in devportal", () => {
    DevPortalHelper.createDeployHttpProxyComponent(API_Name, PROJECT_NAME);
  });

  it("verify api in devportal", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(PROJECT_NAME,API_Name,idpUser);
    Apis.verifyAPIname().should("eq", API_Name);
    Apis.searchApiAndSelect(API_Name);
  });

  it("Add a comment for the API", () => {
    ApiOverview.addCommentToApi("Test comment from Cypress Test Runner");
  });

  it("Delete the comment for the API", () => {
    ApiOverview.deleteComment();
  });

  it("Add and modify ratings of the API", () => {
    ApiOverview.openRatings();
    ApiOverview.addRatings();
    ApiOverview.validateRating();
  });

  it("Generate credentials for SANDBOX env", () => {
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials(Enums.Environment.SANDBOX);
  })

  it("Tryout API in Sandbox env", () => {
    TryOut.navigateToTryOutMenu();
    TryOut.selectEndpoint(Enums.Environment.DEVELOPMENT)
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  })

  it("Generate access token for application", () => {
    ApiCredentials.navigateCredentialsTab()
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
  });

  it("Tryout API in prod env", () => {
    TryOut.selectEndpoint(Enums.Environment.PRODUCTION)
    TryOut.SelectResource(OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  })

  it("Verify the downloaded SDK file", () => {
    APISdk.downloadSDK(sdkFile);
  });

  it("Create a consumer application", () => {
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);

  });

  it("Generate subscription credentials",()=>{
    AppsList.generateCredentials(Enums.Environment.SANDBOX)
    AppsList.generateCredentials(Enums.Environment.PRODUCTION)
  })

  it("Add subscription",()=>{
    Subscriptions.addSubscriptionToApplication(API_Name);
    Subscriptions.validateResubscribingApi(API_Name);
  })

  it("Delete a consumer application", () => {
    TryOut.DeleteApplication(appName);
  });

  it("Verify suspending Dev deployed component", () => {
    LoginPage.login();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(API_Name);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopDevContainer();
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentDeployPage.stopProdContainer();
  });
});
