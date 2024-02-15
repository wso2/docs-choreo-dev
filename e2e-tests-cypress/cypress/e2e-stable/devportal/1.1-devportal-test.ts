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

import { Enums, UsagePlan } from "../../support/commons/enums";
import { console } from "../../support/console/console";
import { Project } from "../../support/console/entities/project/project";
import { Proxy } from "../../support/console/entities/component/proxy-component";
import { devPortal } from "../../support/console/devportal";
import { Application } from "../../support/console/entities/application/application";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";

let application: Application;
let proxy: Proxy;

describe("API overview comment and rating scenario", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const OPERATION_USERS = "intensity";

  let project: Project;

  after(() => {
    console.logout();
  });

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Create API Proxy", () => {
    project
      .createProxyComponent({
        version: "1.0",
        oasFilePath: "apis/generation_oas.yaml",
        endpointUrl: "",
      })
      .then((comp) => {
        proxy = comp;
      });
  });

  it("Deploy API Proxy", () => {
    proxy.deploy();
  });

  it("Promote API Proxy", () => {
    proxy.promote();
  });

  it("Update usage plans", () => {
    proxy.updateUsagePlans([UsagePlan.Gold, UsagePlan.Bronze]);
  });

  it("Publish proxy", () => {
    proxy.publish();
  });

  it("Navigate to Dev portal", () => {
    proxy.navigateToDevPortal();
  });

  it("verify api search in devportal", () => {
    devPortal.searchApi(proxy.getName());
  });

  it("Add a comment for the API", () => {
    proxy.addComment_DevPortal("Test comment from Cypress Test Runner");
  });

  it("Delete the comment for the API", () => {
    proxy.deleteComment_DevPortal();
  });

  it("Add and modify ratings of the API", () => {
    proxy.addRating_DevPortal(4);
  });

  it("Generate credentials for SANDBOX env", () => {
    proxy.generateCredentials_DevPortal(Enums.Environment.SANDBOX);
  });

  it("Tryout API in Development env", () => {
    proxy.testSwaggerConsole_DevPortal({
      resource: OPERATION_USERS,
      env: Enums.Environment.DEVELOPMENT,
    });
  });

  it("Tryout API in Production env", () => {
    proxy.testSwaggerConsole_DevPortal({
      resource: OPERATION_USERS,
      env: Enums.Environment.PRODUCTION,
    });
  });

  it("Verify the downloaded SDK file", () => {
    proxy.downloadSdk_DevPortal(proxy.getName() + "_v1.0_android.zip");
  });

  it("Create a consumer application", () => {
    application = proxy.createApplication_DevPortal();
  });

  it("Share application with another user", () => {
    application.shareApplication(application.getName);
  });

  it("Generate subscription credentials", () => {
    application.generateCredentials(Enums.Environment.SANDBOX);
    application.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Add subscription", () => {
    application.addSubscription(proxy.getName());
  });

  it("Verify suspending Dev deployed component", () => {
    proxy.navigateToComponentInConsole();
    proxy.stopDeployment();
    proxy.stopPromotion();
  });
});


describe("Check application with shared user", () => {

  before(() => {
    DevportalLoginPage.loginToDevportalAsInvitedUser();
  });

  after(() => {
    DevPortalHomePage.logout();
  });

  it("Check shared application", () => {
      let appName = application.getName();
      cy.get('[data-testid="applications-appbar-btn"]').click();
      cy.get('[data-testid="search-btn"]').trigger("mouseover");
      cy.get('[data-testid="search-app"] [placeholder="Search"]').type(appName);
      cy.get('[data-testid="application-list-a"] > [value="a"]').should("exist");
  });

});
