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
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { Environment } from "../../../support/console/pages/enum/environment";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { InsightsPage } from "../../../support/console/pages/insights/insights-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { Utils } from "../../../support/console/utils";

describe("Verify project creation functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("rest");
  const COMPONENT_DESCRIPTION = "covid daily stats";
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const labels = ["IT Operations/Testing Tools", "IT Operations/Debug Tools"];
  const commitMessage = "adding new service";
  const queryParameters1 = [{ key: "number", value: "2" }];
  const queryParameters2 = [{ key: "number", value: "5" }];
  const NEW_BRANCH = "feature";
  const API_NEW_VERSION = "1.1";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify REST API component creation", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(COMPONENT_NAME, COMPONENT_DESCRIPTION);
    ComponentDevelopPage.getComponentURL();
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.eq", "");
  });

  it("Edit code in VScode", () => {
    ComponentOverviewPage.navigateToOverview();
    LoginPage.navigateToCodespace();
    VSExplorer.creteNewBranch(NEW_BRANCH);
    VSExplorer.pasteCode("Numbers.bal");
    VSExplorer.commitPush(commitMessage, true);
  });

  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo();
    ComponentDevelopPage.selectBranch(NEW_BRANCH).then((arr) => {
      expect(arr).to.include(NEW_BRANCH);
    });
    ComponentDevelopPage.verifyLatestCommit(commitMessage);
  });

  it("Verify new version creation", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentOverviewPage.createNewVersion(API_NEW_VERSION, NEW_BRANCH);
    ComponentDevelopPage.getVersion().should("eq", "API Version " + "1.1.0");
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployToDev();
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.eq", "");
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.DEVELOPMENT,
      "root",
      "number",
      "2"
    ).then((res) => {
      expect(res.response).to.be.eq("4");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.PRODUCTION,
      "root",
      "number",
      "2"
    ).then((res) => {
      expect(res.response).to.be.eq("4");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    TestHelper.testOnCurl(
      Environment.DEVELOPMENT,
      HTTPMethod.GET,
      "root",
      queryParameters1
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(4);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(Environment.PRODUCTION, HTTPMethod.GET, "root").then(
      (curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(4);
          expect(res.status).equal(200);
        });
      }
    );
  });

  it("Verify test functionality of isOdd resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.DEVELOPMENT,
      "isOdd",
      "number",
      "5"
    ).then((res) => {
      expect(res.response).to.be.eq("true");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality of isOdd resource in prod on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Environment.PRODUCTION,
      "isOdd",
      "number",
      "5"
    ).then((res) => {
      expect(res.response).to.be.eq("true");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality using generated curl in dev", () => {
    TestHelper.testOnCurl(
      Environment.DEVELOPMENT,
      HTTPMethod.GET,
      "isOdd",
      queryParameters2
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(true);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality using generated curl in prod", () => {
    TestHelper.testOnCurl(Environment.PRODUCTION, HTTPMethod.GET, "isOdd").then(
      (curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(true);
          expect(res.status).equal(200);
        });
      }
    );
  });

  it("Apply configs to dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity("root");
    ComponentAPILifecycle.applyConfiguration(
      Environment.DEVELOPMENT,
      "Revision 3"
    );
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Environment.DEVELOPMENT
    );
  });

  it("Apply configs to prod", () => {
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.applyConfiguration(Environment.PRODUCTION);
  });

  it("Verify resource access without the token in dev", () => {
    Curl.getRequestComponents(`${Environment.DEVELOPMENT}root`).then((curl) =>
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.body).equal(4);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify resource not access without the token in dev", () => {
    Curl.getRequestComponents(`${Environment.DEVELOPMENT}isOdd`).then((curl) =>
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(true);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify resource access without the token in prod", () => {
    Curl.getRequestComponents(`${Environment.PRODUCTION}root`).then((curl) =>
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.body).equal(4);
        expect(res.status).equal(200);
      })
    );
  });

  it("Verify resource not access without the token in prod", () => {
    Curl.getRequestComponents(`${Environment.PRODUCTION}isOdd`).then((curl) =>
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(true);
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

  it("Verify suspending all component deployments", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  it("Verify insight values for dev", () => {
    ChoreoHomePage.navigateToHome();
    ChoreoHomePage.navigateToInsights();
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.DEVELOPMENT);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(6);
    });
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });

  it("Verify insight values for prod", () => {
    InsightsPage.selectTimePeriod();
    InsightsPage.selectEnvironment(Environment.PRODUCTION);
    InsightsPage.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(6);
    });
    InsightsPage.getTotalErrorRequestCount().should("eq", "0");
    InsightsPage.getAverageErrorRate().should("eq", "0");
  });
});
