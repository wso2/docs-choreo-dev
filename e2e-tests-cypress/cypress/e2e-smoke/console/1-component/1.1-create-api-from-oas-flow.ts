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
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { APITest } from "../../../support/console/pages/apis/api-test";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { Enums } from "../../../support/console/enums";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { Utils } from "../../../support/console/utils";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { AppsList } from "../../../support/devportal/pages/applications/apps-list";
import { ProductionKeys } from "../../../support/devportal/pages/applications/production-keys";
import { Subscriptions } from "../../../support/devportal/pages/applications/subscriptions";
import { DevPortalHomePage } from "../../../support/devportal/pages/home/home-page";
import { generateAppName } from "../../../support/devportal/utils";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";

describe("Choreo APIM publisher scenarios", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_NAME = Utils.generateComponentName("oas");
  const API_BASE_PATH = Utils.generateBasePath();
  const Filepath = "apis/generation_oas.yaml";
  const idpUser = "choreoe2etest";
  const appName = generateAppName("-e2etest");
  const permissions = [`emp-read-${Date.now()}`, `emp-write-${Date.now()}`];
  const OPERATION = "intensity";

  before(() => {
    LoginPage.login(true);
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating and publishing an API from open API specification", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.createOpenApi(Filepath);
    RestAPIProxyTemplate.enterAPIdetails(API_NAME, API_BASE_PATH, "", "", "");
  });

  it("Verify component deployment and endpoint configurations", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev(PROJECT_NAME, API_NAME);
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    TestHelper.testOnSwagger(Enums.Environment.DEVELOPMENT, "intensity").then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify test functionality using generated curl in Dev", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      "intensity"
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Disable security of a resource belonging to the API deployed in Dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity("intensity");
    ComponentAPILifecycle.applyConfiguration();
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Enums.Environment.DEVELOPMENT
    );

    // Verify that deployment has been updated by invoking the API without a token
    APITest.testAPI();
    ComponentTestPage.selectCurl();
    Curl.selectCurlEnvironment(Enums.Environment.DEVELOPMENT);
    Curl.selectMethod(Enums.HTTPMethod.GET);
    Curl.enterPathParameter("intensity");
    Curl.getRequestComponents(`${Enums.Environment.DEVELOPMENT}intensity`).then(
      (curl) =>
        Utils.sendGetRequest(curl.url).then((res) => {
          expect(res.status).equal(200);
        })
    );
  });

  it("Verify prod invoke url", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.PromoteToProd();
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    TestHelper.testOnSwagger(Enums.Environment.PRODUCTION, "intensity").then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      "intensity"
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
    ComponentAPILifecycle.selectPermissions();
    ComponentAPILifecycle.navigatePermissionManagementWindow();
    ComponentAPILifecycle.managePermissions(permissions, API_NAME);
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(Enums.ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
  });

  it("Tryout published api", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    Apis.searchApiAndSelect(API_NAME, 1);
    // Validate the API call without the scope
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials();
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(Enums.HTTPMethod.GET, OPERATION);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse("200");
  });

  it("Create application", () => {
    // Create app
    DevPortalHomePage.navigateToAppsPage();
    AppsList.createAnApplication(appName);
    ProductionKeys.generateTestToken();
    Subscriptions.addSubscriptionToApplication(API_NAME);
    Subscriptions.validateResubscribingApi(API_NAME);
    // Edit App and assign the scope
    cy.get('[data-testid="applications-appbar-btn"]')
      .should("be.visible")
      .click();
  });

  it("Add permissions and tryout", () => {
    AppsList.editAnApplication(appName, permissions[0]);
    // Validate API call with scope
    DevPortalHomePage.navigateToApisPage();
    Apis.searchApiAndSelect(API_NAME, 1);
    // DevPortalHomePage.navigateSelectAPI(API_NAME);
    TryOut.navigateToTryOutMenu();
    TryOut.SelectApplication(appName);
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(Enums.HTTPMethod.GET, OPERATION);
    TryOut.TryoutApplication();
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse("200");
  });

  it("Verify consumers", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectConsumers();
    ComponentAPILifecycle.verifyConsumer(appName).should("be.visible");
  });

  it("Verify deleting consumer app", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    TryOut.DeleteApplication(appName);
  });

  it("Verify delete permissions", () => {
    LoginPage.reLoginToChoreo();
    ComponentListingPage.visitToAComponent(API_NAME);
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectPermissions();
    permissions.forEach((permission) => {
      ComponentAPILifecycle.deletePermission(permission);
    });
  });

  it("Verify redeployment after removing permissions", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev(PROJECT_NAME, API_NAME);
  });

  it("Verify insight values for dev", () => {
    ChoreoHomePage.navigateToInsights();
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(3);
    });
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it("Verify insight values for prod", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Enums.Environment.PRODUCTION);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(2);
    });
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it("Reset and undeploy component", () => {
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(API_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
