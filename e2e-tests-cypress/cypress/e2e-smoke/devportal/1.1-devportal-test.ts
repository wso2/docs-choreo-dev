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
import { Utils } from "../../support/console/utils";
import { ComponentAPILifecycle } from "../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../support/console/pages/component/component-overview-page";
import { ApiCredentials } from "../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../support/devportal/pages/apis/try-out";
import { LoginPage } from "../../support/console/pages/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { ProductionKeys } from "../../support/devportal/pages/applications/production-keys";
import { Subscriptions } from "../../support/devportal/pages/applications/subscriptions";
import { generateAppName } from "../../support/devportal/utils";
import { ComponentDeployPage } from "../../support/console/pages/component/component-deploy";
import { APISdk } from "../../support/devportal/pages/apis/api-sdk";
import { DevPortalHelper } from "../../support/devportal/helpers/devportal-helper";

describe("API overview comment and rating scenario", () => {
  const FILE_ID = "oasflow";
  const API_Name = Utils.generateComponentName("oas");
  const idpUser = "choreoe2etest";
  const OPERATION_USERS = "intensity";
  const appName = generateAppName("-e2etest");
  const sdkFile = API_Name + "_1.0.0_android.zip";

  before(() => {
    LoginPage.login();
    DevPortalHelper.createDeployComponent(API_Name);
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Test in devportal", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    Apis.verifyAPIname().should("eq", API_Name);
    Apis.searchApiAndSelect(API_Name);
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
    APISdk.downloadSDK(sdkFile);
  });

  it("Create a consumer application and tryout an API", () => {
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);
    ProductionKeys.generateTestToken();
    Subscriptions.addSubscriptionToApplication(API_Name);
    Subscriptions.validateResubscribingApi(API_Name);
  });

  it("Delete a consumer application", () => {
    TryOut.DeleteApplication(appName);
  });

  it("Verify suspending Dev deployed component", () => {
    LoginPage.reLoginToChoreo(FILE_ID);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
