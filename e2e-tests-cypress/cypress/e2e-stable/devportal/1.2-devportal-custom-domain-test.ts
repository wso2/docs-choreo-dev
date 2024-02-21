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
import { ApiCredentials } from "../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../support/devportal/pages/apis/try-out";
import { LoginPage as ConsoleLoginPage } from "../../support/console/pages/login-page";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { Subscriptions } from "../../support/devportal/pages/applications/subscriptions";
import { generateAppName } from "../../support/devportal/utils";
import { APISdk } from "../../support/devportal/pages/apis/api-sdk";
import { CustomDomainType, Enums } from "../../support/commons/enums";
import { console } from "../../support/console/console";
import { Project } from "../../support/console/entities/project/project";
import { Application } from "../../support/console/entities/application/application";
import { Proxy } from "../../support/console/entities/component/proxy-component";

const CUSTOM_DOMAIN = Cypress.env("devportalCustomDomain");
const Filepath = "apis/generation_oas.yaml";
const PROJECT_DESCRIPTION = "Devportal custom domain scenario";

describe("Create and deploy a component to test developer portal with custom domain", () => {
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

  // Partially migrated spec upto this point. Need to wait till changes to custom domain definition are finalized across all envs,
  // before the rest of the spec can be migrated.

  it("Add a developer portal custom domain", () => {
    // TODO: Uncomment the below code after the changes to custom domain definition are finalized across all envs
    // console.addOrReplaceCustomDomain(CUSTOM_DOMAIN, CustomDomainType.DevPortal);

    ChoreoHomePage.navigateToSettings();
    DomainsComponents.navigateToDomainsSettings();
    DomainsComponents.navigateToDevPortalCustomDomain();
    DomainsComponents.deleteDevportalDomainIfExists(CUSTOM_DOMAIN);
    DomainsComponents.createDevportalDomain(CUSTOM_DOMAIN);
  });

  after(() => {
    console.logout();
  });
});

describe("Login and test developer portal with custom domain", () => {
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

  it("Generate credentials for  Sandbox env", () => {
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials(Enums.Environment.SANDBOX);
  });

  it("Tryout API in Sandbox env", () => {
    TryOut.navigateToTryOutMenu(true);
    TryOut.selectEndpoint(Enums.Environment.DEVELOPMENT);
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  });

  it("Generate credentials and tryout the API in Prod env", () => {
    ApiCredentials.navigateCredentialsTab();
    TryOut.navigateToTryOutMenu(true);
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  });

  it("Verify the downloaded SDK file", () => {
    cy.task("getAPIName").then((API_Name) => {
      const sdkFile = API_Name + "_v1.0_android.zip";
      APISdk.downloadSDK(sdkFile);
    });
  });

  it("Create a consumer application", () => {
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);
  });

  it("Generate keys and Subscribe", () => {
    AppsList.generateCredentials(Enums.Environment.SANDBOX);
    AppsList.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Generate credentials and tryout the API in Sandbox env", () => {
    cy.task("getAPIName").then((an) => {
      let API_Name = an as string;
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
    cy.log("Deleted the custom domain");
  });
});
