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
import { Utils } from "../../../support/console/utils";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";

describe("Verify project creation functionality", () => {
  const API_NAME = Utils.generateComponentName("CYE2E");
  const API_BASE_PATH = Utils.generateBasePath();
  const API_VERSION = "1.0.0";
  const API_NEW_VERSION = "1.1.0";
  const API_ENDPOINT = "https://jsonplaceholder.typicode.com";
  const OPERATION_USERS = "users";
  const OPERATION_POSTS = "posts";
  const ALLOWED_ORIGINS = ["https://127.0.0.1"];
  const ALLOWED_HEADERS = ["tenantId"];
  const ALLOWED_METHODS = [HTTPMethod.TRACE, HTTPMethod.HEAD];
  const PROJECT_DESCRIPTION = "sample stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const idpUser = "choreoe2etest";
  const it_privatedp = Cypress.env("isPrivateOrg") ? it : it.skip;

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify Rest API creation from existing endpoint", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.designNewRestApi(
      API_NAME,
      API_VERSION,
      API_BASE_PATH,
      API_ENDPOINT
    );
    APIDevelop.addResources(OPERATION_USERS, HTTPMethod.GET);
    APIDevelop.addEndpoints();
  });

  it("Verify component deployment to dev", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.verifyDevInvokeURL().should("not.eq", "");
  });

  it_privatedp("Verify component promote and stg invoke url", () => {
    APIDeployment.promoteToStg();
    APIDeployment.verifyStgeInvokeURL().should("not.eq", "");
  });

  it("Verify prod invoke url", () => {
    APIDeployment.PromoteToProd();
    APIDeployment.verifyProdInvokeURL().should("not.eq", "");
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    TestHelper.testOnSwagger(Environment.DEVELOPMENT, OPERATION_USERS).then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it_privatedp("Verify test functionality using Swagger UI in Stg", () => {
    TestHelper.testOnSwagger(Environment.STAGING, OPERATION_USERS).then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    TestHelper.testOnSwagger(Environment.PRODUCTION, OPERATION_USERS).then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.configureSecuritySettings(true, false, ALLOWED_ORIGINS, ALLOWED_HEADERS, ALLOWED_METHODS);
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  });

  it("Create new version from the created API", () => {
    ComponentOverviewPage.navigateToDevelop();
    ComponentOverviewPage.createNewVersion(API_NEW_VERSION, "");
    ComponentDevelopPage.getVersion().should(
      "eq",
      `API Version ${API_NEW_VERSION}`
    );

  });

  it("Add  a new version", () => {
    APIDevelop.addResources(OPERATION_POSTS, HTTPMethod.GET);
    APIDevelop.addEndpoints();
  })

  it("Deploy new version to Dev", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.verifyDevInvokeURL().should("not.eq", "");
  });

  it_privatedp("Verify new component promote to stg", () => {
    APIDeployment.promoteToStg();
    APIDeployment.verifyStgeInvokeURL().should("not.eq", "");
  });

  it("Verify new prod invoke url", () => {
    APIDeployment.PromoteToProd();
    APIDeployment.verifyProdInvokeURL().should("not.eq", "");
  });

  it("Test in dev", () => {
    APITest.testAPI();
    APITest.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.getResponseCode().should("eq", "200");
    SwaggerUI.invokeResource(OPERATION_POSTS);
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it_privatedp("Test in stg", () => {
    APITest.testAPI();
    APITest.selectEnvironment(Environment.STAGING);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.getResponseCode().should("eq", "200");
    SwaggerUI.invokeResource(OPERATION_POSTS);
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Test in prod", () => {
    APITest.testAPI();
    APITest.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(OPERATION_USERS);
    SwaggerUI.getResponseCode().should("eq", "200");
    SwaggerUI.invokeResource(OPERATION_POSTS);
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Publish the API", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector();
  });

  it("Verify api invoke urls", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    Apis.verifyAPIname().should("eq", API_NAME);
    Apis.verifyInvokeUrl()
  });

  it("Test in devportal", () => {
    Apis.searchApiAndSelect(API_NAME, 2);
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials();
    TryOut.navigateToTryOutMenu();
    TryOut.generateTestKeyAndVerify();
    TryOut.SelectResource(null, OPERATION_USERS);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  });

  it("Verify application suspension", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
