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
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { DevPortalHomePage } from "../../../support/devportal/pages/home/home-page";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";

describe("Verify internal API creation functionality", () => {
  const REST_API_NAME = Utils.generateComponentName("internal");
  const PROXY_API_NAME_DEV = Utils.generateComponentName("dev");
  const PROXY_API_VERSION_DEV = "1.0.0";
  const PROXY_API_BASEPATH_DEV = `/${PROXY_API_NAME_DEV}`;

  const PROXY_API_NAME_PROD = Utils.generateComponentName("prod");
  const PROXY_API_VERSION_PROD = "1.0.0";
  const PROXY_API_BASEPATH_PROD = `/${PROXY_API_NAME_PROD}`;

  const RESOURCE_NAME = "greeting";
  const PARAM_NAME = "name";
  const PARAM_TYPE = "query";
  const PARAM_VALUE = "World";
  const PARAM_DATA_TYPE = "string";
  const queryParameters = [{ key: PARAM_NAME, value: PARAM_VALUE }];
  const ACCESS_MODE_EXTERNAL = "External";
  let DEV_INVOKE_URL = "";
  let PROD_INVOKE_URL = "";

  const PROJECT_DESCRIPTION = "API proxy project";
  const PROJECT_NAME = Utils.generateProjectName();

  const idpUser = "choreoe2etest";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify Internal REST API component creation", () => {
    let componentData: ComponentData = {
      componentName: REST_API_NAME,
      displayType: Enums.DisplayType.restAPI,
      accessibility: Enums.Accessibility.INTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/greeting-rest-api",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      "",
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Verify REST API component deployment", () => {
    ComponentListingPage.visitToAComponent(REST_API_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev(false);
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

  it("Verify REST API component promote to PROD", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd(false, false);
  });

  it("Verify resource access without the token in PROD", () => {
    ComponentOverviewPage.navigateToTest();
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
    ComponentAPILifecycle.applyConfiguration();
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
    ComponentAPILifecycle.applyConfiguration();
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
  it("Verify 1st Proxy API creation using Internal API DEV endpoint", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ProjectOverviewPage.addComponent();
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.skipSource();
    RestAPIProxyTemplate.enterAPIdetails(
      PROXY_API_NAME_DEV,
      PROXY_API_BASEPATH_DEV,
      DEV_INVOKE_URL,
      PROXY_API_VERSION_DEV,
      "*",
      ""
    );
  });

  it("Verify Add resource to 1st Proxy API", () => {
    ComponentOverviewPage.navigateToDevelop();
    APIDevelop.addResources(RESOURCE_NAME, Enums.HTTPMethod.GET);
    ComponentDevelopPage.addParameterToProxyResource(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET,
      PARAM_TYPE,
      PARAM_NAME,
      PARAM_DATA_TYPE
    );
    ComponentDevelopPage.saveResource();
  });

  it("Verify 1st PROXY API component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeployProxyApiToDev();
  });

  // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
  // by receiving a 200 response
  it("Verify 1st PROXY API resource access in DEV", () => {
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

  it("Verify 1st PROXY API component promote to PROD", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteProxyApiToProd();
  });

  it("Verify 1st PROXY API resource access in PROD", () => {
    ComponentOverviewPage.navigateToTest();
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
  it("Verify 2nd Proxy API creation using Internal API PROD endpoint", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ProjectOverviewPage.addComponent();
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.skipSource();
    RestAPIProxyTemplate.enterAPIdetails(
      PROXY_API_NAME_PROD,
      PROXY_API_BASEPATH_PROD,
      PROD_INVOKE_URL,
      PROXY_API_VERSION_PROD,
      "*",
      ""
    );
  });

  it("Verify Add resource to 2nd proxy API", () => {
    ComponentOverviewPage.navigateToDevelop();
    APIDevelop.addResources(RESOURCE_NAME, Enums.HTTPMethod.GET);
    ComponentDevelopPage.addParameterToProxyResource(
      RESOURCE_NAME,
      Enums.HTTPMethod.GET,
      PARAM_TYPE,
      PARAM_NAME,
      PARAM_DATA_TYPE
    );
    ComponentDevelopPage.saveResource();
  });

  it("Verify 2nd PROXY API component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.configureAndDeployProxyApiToDev();
  });

  // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
  // by receiving a 200 response
  it("Verify 2nd PROXY API resource access in dev", () => {
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

  it("Verify 2nd PROXY API component promote to PROD", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteProxyApiToProd();
  });

  it("Verify 2nd PROXY API resource access in prod", () => {
    ComponentOverviewPage.navigateToTest();
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
    ProjectListingPage.selectProject(PROJECT_NAME);
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
    Apis.searchApiAndSelect(REST_API_NAME, 1);
    ApiCredentials.navigateCredentialsTab();
    ApiCredentials.generateCredentials(Enums.Environment.PRODUCTION);
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.SelectResource("greeting");
    TryOut.TryoutAPI();
    TryOut.InputQueryParamater(PARAM_NAME, PARAM_VALUE);
    TryOut.ExecuteResourceFunction();
    TryOut.ValidateResponse("200");
  });

  // Suspend prod/dev deployed Internal REST API
  it("Verify suspending Internal REST API component", () => {
    LoginPage.reLoginToChoreo();
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  // Suspend prod/dev deployed PROXY API for dev URL
  it("Verify suspending PROXY API for DEV URL component", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ComponentListingPage.visitToAComponent(PROXY_API_NAME_DEV);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  // Suspend prod/dev deployed PROXY API for prod URL
  it("Verify suspending PROXY API for PROD URL component", () => {
    ChoreoHomePage.navigateToHome();
    ProjectListingPage.selectProject(PROJECT_NAME);
    ComponentListingPage.visitToAComponent(PROXY_API_NAME_PROD);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
