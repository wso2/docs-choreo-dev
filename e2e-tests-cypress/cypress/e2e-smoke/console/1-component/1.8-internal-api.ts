/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
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
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Enums } from "../../../support/console/enums";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { Utils } from "../../../support/console/utils";
import { REUSABLE_PROJECT_NAME } from "../../../support/devportal/constants";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { DevPortalHomePage } from "../../../support/devportal/pages/home/home-page";

describe("Verify internal API creation functionality", () => {
  const REST_API_NAME = "internal-api-1.8";
  const REST_API_NAME_DEVPORTAL = "internalapi18";
  const PROXY_API_NAME_DEV = Utils.generateComponentName("dev").substring(
    10,
    50
  );
  const PROXY_API_VERSION_DEV = "1.0.0";
  const PROXY_API_BASEPATH_DEV = `/${PROXY_API_NAME_DEV}`;

  const PROXY_API_NAME_PROD = Utils.generateComponentName("prod").substring(
    10,
    50
  );
  const PROXY_API_VERSION_PROD = "1.0.0";
  const PROXY_API_BASEPATH_PROD = `/${PROXY_API_NAME_PROD}`;

  const RESOURCE_NAME = "greeting";
  const PARAM_NAME = "name";
  const PARAM_TYPE = "query";
  const PARAM_VALUE = "World";
  const PARAM_DATA_TYPE = "string";
  const queryParameters = [{ key: PARAM_NAME, value: PARAM_VALUE }];
  const ACCESS_MODE_INTERNAL = "internal";
  const ACCESS_MODE_EXTERNAL = "external";
  let DEV_INVOKE_URL = "";
  let PROD_INVOKE_URL = "";

  const PROJECT_DESCRIPTION = "API proxy project";
  const PROJECT_1_NAME = Utils.generateProjectName();

  const idpUser = "choreoe2etest";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify Internal REST API component creation", () => {
    ProjectListingPage.selectProject(REUSABLE_PROJECT_NAME);
    ComponentListingPage.visitToAComponent(REST_API_NAME);
  });

  it("Verify REST API component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
    ComponentDeployPage.verifyDeploymentStatus();
  });

  it("Verify REST API component promote to PROD", () => {
    ComponentDeployPage.promoteToProdApiPerspectiveView();
    ComponentDeployPage.verifyDeploymentStatus();
  });

  it("Publish the API", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector();
  });

  it("Verify resource access without the token in DEV", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      expect(Utils.isHostResolvable(curl.url) == false);
    });
  });

  it("Verify resource access without the token in PROD", () => {
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      expect(Utils.isHostResolvable(curl.url) == false);
    });
  });

  it("Apply disable security config in DEV", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.selectRevision(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(
      Enums.Environment.DEVELOPMENT,
      "Revision 5"
    );
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Enums.Environment.DEVELOPMENT
    );
  });

  it("Apply disable security config in PROD", () => {
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.PRODUCTION);
    ComponentAPILifecycle.selectRevision(Enums.Environment.PRODUCTION);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(Enums.Environment.PRODUCTION);
  });

  it("Verify resource access without the security in DEV", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      cy.get("#filled-disabled")
        .eq(0)
        .invoke("attr", "value")
        .then((invokeUrl) => {
          DEV_INVOKE_URL = invokeUrl;
        });
      expect(Utils.isHostResolvable(curl.url) == false);
    });
  });

  it("Verify resource access without the security in PROD", () => {
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      cy.get("#filled-disabled")
        .eq(0)
        .invoke("attr", "value")
        .then((invokeUrl) => {
          PROD_INVOKE_URL = invokeUrl;
        });
      expect(Utils.isHostResolvable(curl.url) == false);
    });
  });

  // Proxy API with dev endpoint
  it("Verify Proxy API creation using existing DEV endpoint", () => {
    LoginPage.reLoginToChoreo();
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.createNewProject(PROJECT_1_NAME, PROJECT_DESCRIPTION);
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.skipSource();
    RestAPIProxyTemplate.enterAPIdetails(
      PROXY_API_NAME_DEV,
      PROXY_API_BASEPATH_DEV,
      DEV_INVOKE_URL,
      PROXY_API_VERSION_DEV
    );
  });

  it("Verify Add resource to Proxy API", () => {
    ComponentOverviewPage.navigateToDevelop();
    ComponentDevelopPage.addResourcesToProxy(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET
    );
    ComponentDevelopPage.addParameterToProxyResource(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET,
      PARAM_TYPE,
      PARAM_NAME,
      PARAM_DATA_TYPE
    );
    ComponentDevelopPage.saveResource();
  });

  it("Verify PROXY API component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeployProxyApiToDev();
  });

  it("Verify PROXY API component promote to PROD", () => {
    ComponentDeployPage.promoteProxyApiToProd();
  });

  // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
  // by receiving a 200 response
  it("Verify resource access without the token in DEV", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify resource access without the token in PROD", () => {
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  // Proxy API with prod endpoint
  it("Verify Proxy API creation using existing PROD endpoint", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_1_NAME);
    ProjectOverviewPage.addComponent();
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.skipSource();
    RestAPIProxyTemplate.enterAPIdetails(
      PROXY_API_NAME_PROD,
      PROXY_API_BASEPATH_PROD,
      PROD_INVOKE_URL,
      PROXY_API_VERSION_PROD
    );
  });

  it("Verify Add resource to proxy API", () => {
    ComponentOverviewPage.navigateToDevelop();
    ComponentDevelopPage.addResourcesToProxy(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET
    );
    ComponentDevelopPage.addParameterToProxyResource(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET,
      PARAM_TYPE,
      PARAM_NAME,
      PARAM_DATA_TYPE
    );
    ComponentDevelopPage.saveResource();
  });

  it("Verify PROXY API component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeployProxyApiToDev();
  });

  it("Verify PROXY API component promote to PROD", () => {
    ComponentDeployPage.promoteProxyApiToProd();
  });

  // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
  // by receiving a 200 response
  it("Verify resource access without the token in dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify resource access without the token in prod", () => {
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  // Navigate back to the Internal API and change the Access Mode to external and test the invocation via curl,
  // Internal API should now be publicly accessible by receiving a 200 response
  it("Verify change access to Internal API to External ", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(REUSABLE_PROJECT_NAME);
    ComponentListingPage.visitToAComponent(REST_API_NAME);
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.updateAPIAccessMode(ACCESS_MODE_EXTERNAL);
  });

  it("Verify resource access to external API in DEV", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify resource access to external API in PROD", () => {
    TestHelper.testOnCurlDiscardPrevious(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify API invocation in Devportal for external REST API component", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(idpUser);
    DevPortalHomePage.navigateToApisPage();
    DevPortalHomePage.navigateSelectAPI(REST_API_NAME_DEVPORTAL);
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials();
    TryOut.navigateToTryOutMenu();
    TryOut.generateTestKeyAndVerify();
    TryOut.SelectResource(Enums.HTTPMethod.GET, "greeting");
    TryOut.TryoutAPI();
    TryOut.InputQueryParamater(PARAM_NAME, PARAM_VALUE);
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse("200");
  });

  it("Verify reset credentials in Devportal", () => {
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.removeCredentials();
  });

  it("Verify reset security in DEV deployed REST API component", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.selectRevision(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(
      Enums.Environment.DEVELOPMENT,
      "Revision 5"
    );
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Enums.Environment.DEVELOPMENT
    );
  });

  it("Verify reset security in PROD deployed REST API component", () => {
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.PRODUCTION);
    ComponentAPILifecycle.selectRevision(Enums.Environment.PRODUCTION);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(Enums.Environment.PRODUCTION);
  });

  it("Verify reset access mode to Internal in REST API component", () => {
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.updateAPIAccessMode(ACCESS_MODE_INTERNAL);
  });

  // Suspend prod/dev deployed Internal REST API
  it("Verify suspending Internal REST API component", () => {
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.demoteToCreated();
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  // Suspend prod/dev deployed PROXY API for dev URL
  it("Verify suspending PROXY API for DEV URL component", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_1_NAME);
    ComponentListingPage.visitToAComponent(PROXY_API_NAME_DEV);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  // Suspend prod/dev deployed PROXY API for prod URL
  it("Verify suspending PROXY API for PROD URL component", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_1_NAME);
    ComponentListingPage.visitToAComponent(PROXY_API_NAME_PROD);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
