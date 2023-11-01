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

import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { AppsList } from "../../../support/devportal/pages/applications/apps-list";
import { Subscriptions } from "../../../support/devportal/pages/applications/subscriptions";
import { DevPortalHomePage } from "../../../support/devportal/pages/home/home-page";
import { generateAppName } from "../../../support/devportal/utils";
import { OK } from "../../../support/commons/http";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";

describe("Choreo APIM publisher scenarios", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  let PROJECT_NAME;
  let API_NAME;
  let API_BASE_PATH;
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

  it("Creating a project", () => {
    PROJECT_NAME = Utils.generateProjectName();
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Creating and publishing an API from open API specification", () => {
    cy.log("Starting API Creation using open API specification");
    API_NAME = Utils.generateComponentName("oas");
    API_BASE_PATH = Utils.generateBasePath();

    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.createOpenApi(Filepath);
    RestAPIProxyTemplate.enterAPIdetails(
      API_NAME,
      API_BASE_PATH,
      "",
      "",
      "",
      "get"
    );
  });

  it("Verify component deployment and endpoint configurations", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployToDev();
  });

  it("Verify prod invoke url", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    ComponentOverviewPage.navigateToTest();
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

  it("Verify test functionality using Swagger UI in Prod", () => {
    ComponentOverviewPage.navigateToTest();
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
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    if (Utils.isKubeConFeaturesEnabled(false)) {
      ComponentOverviewPage.navigateToDeploy();
      APIDeployment.configureSecuritySettings(false);
      APIDevelop.managePermissions(permissions, API_NAME);
    } else {
      ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
      ComponentAPILifecycle.navigatePermissionManagementWindow();
      ComponentAPILifecycle.managePermissions(permissions, API_NAME);
    }
  });

  it("Change API lifecycle to Published", () => {
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector();
  });

  it("Verify connector publishing ", () => {
    ComponentAPILifecycle.publish(Enums.ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
  });

  it("Search application in devportal", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(
      PROJECT_NAME,
      API_NAME,
      idpUser
    );
    Apis.searchApiAndSelect(API_NAME, 1);
  });

  it("Generate credentials for prod env", () => {
    ApiCredentials.navigateCredentialsTab(); // Validate the API call without the scope
    ApiCredentials.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Tryout resource in PROD env", () => {
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.selectEndpoint(Enums.Environment.PRODUCTION);
    TryOut.SelectResource(OPERATION);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse(OK);
  });

  it("Generate credentials for application", () => {
    DevPortalHomePage.navigateToAppsPage(); // Create app
    AppsList.createAnApplication(appName);
  });

  it("Generate credentials", () => {
    AppsList.generateCredentials(Enums.Environment.SANDBOX);
    AppsList.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Add subscription", () => {
    Subscriptions.addSubscriptionToApplication(API_NAME);
    Subscriptions.validateResubscribingApi(API_NAME);
  });

  it("Add permissions and tryout", () => {
    AppsList.editAnApplication(appName, permissions[0]);
    DevPortalHomePage.navigateToApisPage();
    Apis.searchApiAndSelect(API_NAME, 1);
  });

  it("Generate access token for application", () => {
    TryOut.navigateToTryOutMenu();
    TryOut.SelectApplication(appName);
    TryOut.GenerateAccessToken();
  });

  it("Tryout application", () => {
    TryOut.SelectResource(OPERATION);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse(OK);
  });

  it("Verify consumers", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectConsumers();
    ComponentAPILifecycle.verifyConsumer(appName).should("be.visible");
  });

  it("Verify deleting consumer app", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(
      PROJECT_NAME,
      API_NAME,
      idpUser
    );
    TryOut.DeleteApplication(appName);
  });

  it("Verify delete permissions", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToManage();

    // No need to check deletion in new flow as it is already checked in the initial step
    if (!Utils.isKubeConFeaturesEnabled(false)) {
      ComponentAPILifecycle.selectPermissions();
      permissions.forEach((permission) => {
        ComponentAPILifecycle.deletePermission(permission);
      });
    }
  });

  it("Verify redeployment after removing permissions", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployToDev();
  });

  it("Navigate to component usage insights", () => {
    ChoreoHomePage.navigateToComponentUsageInsights();
  });

  it("Navigate to project usage insights", () => {
    ChoreoHomePage.navigateToProjectUsageInsights();
  });

  it("Verify insight values for dev", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(2);
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

  it("Navigate to deployment", () => {
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(API_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Reset and undeploy component", () => {
    ComponentDeployPage.stopAllDeployment();
  });
});
