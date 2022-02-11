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

import { LoginPage } from "../../../support/console/pages/login-page";

import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { APITest } from "../../../support/console/pages/apis/api-test";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { RandomTextGenerator } from "../../../support/console/pages/component/common/random-text-generator";
import { Utils } from "../../../support/console/utils";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { Environment } from "../../../support/console/pages/enum/environment";
import { DevportalHomePage } from "../../../support/devportal/pages/home/home-page";

describe("Verify project creation functionality", () => {
  const API_NAME = RandomTextGenerator.generateApiName("CYE2E");
  const API_VERSION = "1.0.0";
  const API_ENDPOINT = "https://jsonplaceholder.typicode.com";
  const OPERATION_USERS = "/users";
  const   OPERATION_POSTS ="/posts"
  const ALLOWED_ORIGINS = ["https://127.0.0.1"];
  const ALLOWED_HEADERS = ["tenantId"];
  const ALLOWED_METHODS = [HTTPMethod.TRACE, HTTPMethod.HEAD];
  const PROJECT_DESCRIPTION = "sample stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const FILE_ID = "1.2-create-api-from-rest-endpoint";
  const idpUser = "choreoe2etest";
  

  before(() => LoginPage.loginToChoreo(FILE_ID));

  after(() => DevportalHomePage.logout(FILE_ID));

  it("Verify Rest API creation from existing endpoint", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.designNewRestApi(
      API_NAME,
      API_VERSION,
      "",
      API_ENDPOINT,
      FILE_ID
    );
    APIDevelop.addResources(OPERATION_USERS, HTTPMethod.GET);
    APIDevelop.addEndpoints();
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.verifyDevInvokeURL().should('not.be.null')
    APIDeployment.PromoteToProd();
    APIDeployment.verifyProdInvokeURL().should('not.be.null')
  });

  it("Verify test functionality", () => {
    APITest.testAPI();
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.GetResponse();
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.configureSecuritySettings(
      true,
      false,
      ALLOWED_ORIGINS,
      ALLOWED_HEADERS,
      ALLOWED_METHODS
    );
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  });

  it("Create new version from the created API", () => {
    ComponentOverviewPage.navigateToDevelop();
    ComponentOverviewPage.createNewVersion();
    ComponentDevelopPage.getVersion().should("eq", "Version 1.0.1");
    APIDevelop.addResources(OPERATION_POSTS, HTTPMethod.GET);
    APIDevelop.addEndpoints();
   
  });

  it("Deploy new version", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.verifyDevInvokeURL().should('not.be.null')
    APIDeployment.PromoteToProd();
    APIDeployment.verifyProdInvokeURL().should('not.be.null')
  });

  it("Test in prod", () => {
    APITest.testAPI();
    APITest.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.getResponseCode().should('eq','200')
    SwaggerUI.invokeResource(OPERATION_POSTS);
    SwaggerUI.getResponseCode().should('eq','200')
 //   SwaggerUI.GetResponse();
  });

  it("Test in dev", () => {
    APITest.testAPI();
    APITest.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.getResponseCode().should('eq','200')
    SwaggerUI.invokeResource(OPERATION_POSTS);
    SwaggerUI.getResponseCode().should('eq','200')
 //   SwaggerUI.GetResponse();
  });

  it("Publish connector", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
  });

  it("Test in devportal", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    Apis.verifyAPIname().should("eq", API_NAME);
    Apis.searchApiAndSelect(API_NAME);
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials();
    TryOut.navigateToTryOutMenu();
    TryOut.generateTestKeyAndVerify();
    TryOut.SelectResource(null, OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    cy.wait(5000);
    TryOut.GetResponse();
  });
});
