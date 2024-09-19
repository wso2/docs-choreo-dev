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

import { Enums, UsagePlan } from "../../support/commons/enums";
import { console } from "../../support/console/console";
import { Project } from "../../support/console/entities/project/project";
import {
  Proxy,
  ProxyMetaData,
} from "../../support/console/entities/component/proxy-component";
import { devPortal } from "../../support/console/devportal";
import { Application } from "../../support/console/entities/application/application";

describe("API overview comment and rating scenario", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const OPERATION_USERS = "intensity";

  let project: Project;
  let proxy: Proxy;
  let application: Application;

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
        // Since we are switching domains when navigating to devportal url we will no longer have access to the proxy object
        // So we need to save the proxy metadata in nodejs global state using below cy.task() to access it later
        cy.task("setData", {
          key: Cypress.spec.name, // Unique key to store the data, in this case spec name is sufficient
          value: proxy.getMetaData(),
        });
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
    devPortal.loginToDevPortal();

    cy.task("getData", Cypress.spec.name).then((metaData) => {
      proxy = Proxy.fromMetaData(metaData as ProxyMetaData);
    });
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

  it("Generate subscription credentials", () => {
    application.generateCredentials(Enums.Environment.SANDBOX);
    application.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Add subscription", () => {
    application.addSubscription(proxy.getName(), UsagePlan.Bronze);
  });

  it("Delete a consumer application", () => {
    proxy.deleteApplication_DevPortal(application);
  });

  it("Verify suspending Dev deployed component", () => {
    proxy.navigateToComponentInConsole();
    proxy.stopDeployment();
    proxy.stopPromotion();
  });
});
