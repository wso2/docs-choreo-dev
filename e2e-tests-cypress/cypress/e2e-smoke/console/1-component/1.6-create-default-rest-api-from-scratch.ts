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
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { Environment } from "../../../support/console/pages/enum/environment";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { Utils } from "../../../support/console/utils";

describe("Verify project creation functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("rest");
  const COMPONENT_DESCRIPTION = "covid daily stats";
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const FILE_ID = "restapidefault";
  const RESOURCE_NAME = "greeting";
  const PARAM_NAME = "name";
  const PARAM_VALUE = "World";
  const MATCHING_STRING = "Hello, " + PARAM_VALUE;
  const queryParameters1 = [{ key: PARAM_NAME, value: PARAM_VALUE }];

  before(()=>{
    LoginPage.login()
  })
  after(()=>{
    ChoreoHomePage.logout()
  })
  

  it("Verify REST API component creation", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(
      COMPONENT_NAME,
      COMPONENT_DESCRIPTION,
      FILE_ID
    );
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deploy();
    ComponentDeployPage.verifyDevInvokeURL().should("not.be.null");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.be.null");
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource(RESOURCE_NAME);
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue(PARAM_NAME, PARAM_VALUE);
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", MATCHING_STRING);
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Verify test functionality of root resource in dev on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter(RESOURCE_NAME);
    Curl.addQueryParameter(queryParameters1);
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.DEVELOPMENT}${RESOURCE_NAME}`
    ).then((curl) =>
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource(RESOURCE_NAME);
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue(PARAM_NAME, PARAM_VALUE);
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", MATCHING_STRING);
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Verify test functionality of root resource in prod on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.PRODUCTION);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter(RESOURCE_NAME);
    Curl.addQueryParameter(queryParameters1);
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.PRODUCTION}${RESOURCE_NAME}`
    ).then((curl) =>
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      })
    );
  });

  it("Apply configs to dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(Environment.DEVELOPMENT);
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Environment.DEVELOPMENT
    );
    ComponentAPILifecycle.getLatestRevision().should("eq", "Revision 3");
  });

  it("Apply configs to prod", () => {
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.applyConfiguration(Environment.PRODUCTION);
  });

  it("Verify resource access without the token in dev", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter(RESOURCE_NAME);
    Curl.addQueryParameter(queryParameters1);
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.DEVELOPMENT}${RESOURCE_NAME}`
    ).then((curl) =>
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify resource access without the token in prod", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.PRODUCTION);
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.PRODUCTION}${RESOURCE_NAME}`
    ).then((curl) =>
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector();
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment()
  });


});
