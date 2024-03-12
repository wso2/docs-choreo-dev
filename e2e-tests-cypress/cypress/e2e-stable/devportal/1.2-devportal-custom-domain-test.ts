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

import { CustomDomainType, Enums } from "../../support/commons/enums";
import { console } from "../../support/console/console";
import { Project } from "../../support/console/entities/project/project";
import { Application } from "../../support/console/entities/application/application";
import {
  Proxy,
  ProxyMetaData,
} from "../../support/console/entities/component/proxy-component";
import { devPortal } from "../../support/console/devportal";

const CUSTOM_DOMAIN = Cypress.env("devportalCustomDomain");
const Filepath = "apis/generation_oas.yaml";
const PROJECT_DESCRIPTION = "Devportal custom domain scenario";

describe("Create and deploy a component to test developer portal with custom domain", () => {
  const OPERATION_USERS = "intensity";

  let project: Project;
  let proxy: Proxy;
  let application: Application;

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
        oasFilePath: Filepath,
        endpointUrl: "",
      })
      .then((comp) => {
        proxy = comp;
        // Since we are switching domains when navigating to custom devportal domain url we will no longer have access to the proxy object
        // So we need to save the proxy metadata in the global cypress state using below cy.task() to access it later
        cy.task("setAPIName", proxy.getMetaData());
      });
  });

  it("Deploy API Proxy", () => {
    proxy.deploy();
  });

  it("Promote API Proxy", () => {
    proxy.promote();
  });

  it("Publish proxy", () => {
    proxy.publish();
  });

  it("Add a developer portal custom domain", () => {
    console.addOrReplaceCustomDomain(CUSTOM_DOMAIN, CustomDomainType.DevPortal);
  });

  it("Login to devportal custom domain", () => {
    devPortal.loginToDevPortal(CUSTOM_DOMAIN);
    // Recreate Proxy object using previously saved metadata
    cy.task("getAPIName").then((metaData) => {
      proxy = Proxy.fromMetaData(metaData as ProxyMetaData);
    });
  });

  it("Find API in devportal custom domain", () => {
    devPortal.searchApi(proxy.getName());
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
    application.addSubscription(proxy.getName());
  });

  it("Delete a consumer application", () => {
    proxy.deleteApplication_DevPortal(application);
  });
});
