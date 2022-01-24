import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { Environment } from "../../../support/console/pages/enum/environment";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { HomePage } from "../../../support/console/pages/home/home-page";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { Utils } from "../../../support/console/utils";

describe("Verify resources security configuration", () => {
  const COMPONENT_NAME = "covid stat api";
  const COMPONENT_DESCRIPTION = "covid daily stats";
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const FILE_ID = "create-rest-api-from-scratch";
  const labels = ["IT Operations/Testing Tools", "IT Operations/Debug Tools"];
  const commitMessage = "adding new service";
  const queryParameters1 = [{ key: "number", value: "2" }];
  const queryParameters2 = [{ key: "number", value: "5" }];

  before(() => LoginPage.loginToChoreo(FILE_ID));

  it("Verify test functionality of root resource in dev on swagger", () => {
    ProjectListingPage.selectProject("Default Project");
    HomePage.navigateToComponents();

    // ProjectOverviewPage.selectComponent("jojo");
    // ComponentOverviewPage.navigateToTest();
    // ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    // ComponentTestPage.getTestKey();
    // SwaggerUI.SelectResource("/root");
    // SwaggerUI.TryoutAPI();
    // SwaggerUI.enterValue("number", "2");
    // SwaggerUI.ExecuteResourceFunction();
    // SwaggerUI.GetResponse().should("eq", "4");
    // SwaggerUI.getResponseCode().should("eq", "200");
  });

  it.skip("Verify test functionality of root resource in dev on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter("root");
    Curl.addQueryParameter(queryParameters1);
    Curl.getRequestComponents(FILE_ID, `${Environment.DEVELOPMENT}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
  });

  it.skip("Verify test functionality of isOdd resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource("/isOdd");
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue("number", "5");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", "true");
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it.skip("Verify test functionality of isOdd resource in dev on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter("isOdd");
    Curl.addQueryParameter(queryParameters2);
    Curl.getRequestComponents(FILE_ID, `${Environment.DEVELOPMENT}isOdd`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(true);
          expect(res.status).equal(200);
        })
    );
  });

  it.skip("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource("/root");
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue("number", "2");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", "4");
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it.skip("Verify test functionality of root resource in prod on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.PRODUCTION);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter("root");
    Curl.addQueryParameter(queryParameters1);
    Curl.getRequestComponents(FILE_ID, `${Environment.PRODUCTION}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
  });

  it.skip("Verify test functionality of isOdd resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource("/isOdd");
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue("number", "5");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", "true");
    SwaggerUI.getResponseCode().should("eq", "200");
  });

  it.skip("Verify test functionality of isOdd resource in prod on curl", () => {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.PRODUCTION);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.enterPathParameter("isOdd");
    Curl.addQueryParameter(queryParameters2);
    Curl.getRequestComponents(FILE_ID, `${Environment.PRODUCTION}isOdd`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(true);
          expect(res.status).equal(200);
        })
    );
  });

  it("Apply configs to dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.getLatestRevision().then((r) => {
      cy.log(JSON.stringify(r));
    });
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity("/root");
    ComponentAPILifecycle.applyConfiguration(Environment.DEVELOPMENT);
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Environment.DEVELOPMENT
    );
    ComponentAPILifecycle.getLatestRevision().should("eq", "Revision 184");
  });

  it("Apply configs to prod", () => {
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.applyConfiguration(Environment.PRODUCTION);
  });

  it("Verify resource access without the token in dev", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.DEVELOPMENT}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
  });

  it("Verify resource not access without the token in dev", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.DEVELOPMENT}isOdd`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(true);
          expect(res.status).equal(200);
        })
    );
  });

  it("Verify resource access without the token in prod", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.PRODUCTION}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
  });

  it("Verify resource not access without the token in prod", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.PRODUCTION}isOdd`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
          expect(res.body).equal(true);
          expect(res.status).equal(200);
        })
    );
  });

  it.skip("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
  });

  it.skip("Verify insight values for dev", () => {
    HomePage.navigateToHome();
    HomePage.navigateToInsights();
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should("eq", "6");
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it.skip("Verify insight values for prod", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.PRODUCTION);
    InsightsPage.getTotalTraffic().should("eq", "6");
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it.skip("Verify increase in total traffic count for dev", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.DEVELOPMENT}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should("eq", "7");
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it.skip("Verify increase in total traffic count for prod", () => {
    Curl.getRequestComponents(FILE_ID, `${Environment.PRODUCTION}root`).then(
      (curl) =>
        Utils.sendRequest(curl.method, curl.url).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        })
    );
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.PRODUCTION);
    InsightsPage.getTotalTraffic().should("eq", "7");
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  after(() => {
    HomePage.logout(FILE_ID);
  });
});
