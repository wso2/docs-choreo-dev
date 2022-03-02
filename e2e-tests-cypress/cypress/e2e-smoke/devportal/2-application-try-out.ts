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

/// <reference types="cypress" />

import { LoginPage } from "../../support/devportal/pages/login/login-page";
import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { generateAppName } from "../../support/devportal/utils";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { ProductionKeys } from "../../support/devportal/pages/applications/production-keys";
import { Subscriptions } from "../../support/devportal/pages/applications/subscriptions";
import { TryOut } from "../../support/devportal/pages/apis/try-out";

describe("Application tryout scenario", () => {
  const appName = generateAppName("-e2etest");
  const apiName = "e2etestdevportalapi";

  before(() => LoginPage.loginToDevportal());
  after(() => DevPortalHomePage.logout());

  it("Create a consumer application and tryout an API", () => {
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);
    ProductionKeys.generateTestToken();
    Subscriptions.addAnSubsriptionToApplication(apiName);
    Subscriptions.validateResubscribingApi(apiName);
    TryOut.navigateToTryOut(apiName);
    TryOut.tryOutApi(appName);
    TryOut.SelectResource("GET", "/sayHello");
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  });

  it("Delete a consumer application", () => {
    TryOut.DeleteApplication(appName);
  });
});
