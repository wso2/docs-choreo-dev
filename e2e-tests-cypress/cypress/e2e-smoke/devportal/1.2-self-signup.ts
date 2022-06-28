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

import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { LoginPage } from "../../support/console/pages/login-page";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { generateAppName } from "../../support/devportal/utils";
import { ProductionKeys } from "../../support/devportal/pages/applications/production-keys";


describe("Dev portal self-sign up scenario", () => {
    const appName = generateAppName("-e2etest");

  before(() => {
    cy.request(Cypress.env("selfSignupLoginUrl"), {
    });
  });

  after(() => {
    DevPortalHomePage.logout();
  });

  
  it("Self sign-up to dev portal", () => {
    LoginPage.selfSignup();
  });

  it("Create a consumer application and generate token", () => {  
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);
    ProductionKeys.generateTestToken();
  });
  
});
