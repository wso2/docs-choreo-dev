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
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { APITest } from "../../../support/console/pages/apis/api-test";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { RandomTextGenerator } from "../../../support/console/pages/component/common/random-text-generator";
import { Utils } from "../../../support/console/utils";
import { Environment } from "../../../support/console/pages/enum/environment";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";

describe("Choreo APIM publisher scenarios", () => {
  const FILE_ID = "oasflow";
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_Name = RandomTextGenerator.generateApiName("oas");
  const Filepath = "apis/generation_oas.yaml";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating and publishing an API from open API specification", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.createOpenApi(Filepath);
    RestAPIProxyTemplate.enterAPIdetails(API_Name, API_Name, "");
  });

  it("Verify component deployment and endpoint configurations", () => {
    //APIDevelop.updateEndpointConfiguration('https://api.carbonintensity.org.uk');
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    ComponentDeployPage.verifyDevInvokeURL().should("not.be.null");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.be.null");
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    APITest.testAPI();
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource("/intensity");
    SwaggerUI.GetResponse();

    APITest.testAPI();
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource("/intensity/factors");
    SwaggerUI.GetResponse();

    APITest.testAPI();
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource("/generation");
    SwaggerUI.GetResponse();
  });

  it("Verify test functionality using generated curl in Dev", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter("intensity");
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.DEVELOPMENT}intensity`
    ).then((curl) =>
      Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      })
    );
  });

  it("Disable security of a resource belonging to the API deployed in Dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity("/intensity");
    ComponentAPILifecycle.applyConfiguration(Environment.DEVELOPMENT);
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Environment.DEVELOPMENT
    );
    ComponentAPILifecycle.getLatestRevision().should("eq", "Revision 3");

    // Verify that deployment has been updated by invoking the API without a token
    Curl.getRequestComponents(
      `${FILE_ID}${Environment.DEVELOPMENT}intensity`
    ).then((curl) =>
      Utils.sendRequest(curl.method, curl.url).then((res) => {
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
  });
});
