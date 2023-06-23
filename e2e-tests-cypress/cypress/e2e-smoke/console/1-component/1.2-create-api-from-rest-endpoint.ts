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
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { OK } from "../../../support/commons/http";
import { cyLog } from "../../../support/commons/cy";
import { GraphQL } from "../../../support/console/apis/graphql";

before(() => {
  LoginPage.login();
});
after(() => {
  ChoreoHomePage.logout();
});

describe(`Verify proxy api functionality`, () => {
  const API_NAME = Utils.generateComponentName("CYE2E");
  const API_BASE_PATH = Utils.generateBasePath();
  const API_VERSION = "1.0.0";
  const API_NEW_VERSION = "1.1.0";
  const API_ENDPOINT = "https://jsonplaceholder.typicode.com";
  const OPERATION_USERS = "users";
  const OPERATION_POSTS = "posts";
  const PROJECT_DESCRIPTION = "sample stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const idpUser = "choreoe2etest";
  const HEADER_KEY = "x-header-test";
  const HEADER_VALUE = "test";
  const HEADER_KEY_2 = "x-header-test2";
  const HEADER_VALUE_2 = "test2";
  const HEADER_VALUE_3 = "test3";

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify Rest API creation from existing endpoint", () => {
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.skipSource();
    RestAPIProxyTemplate.enterAPIdetails(
      API_NAME,
      API_BASE_PATH,
      API_ENDPOINT,
      API_VERSION,
      "*",
      "get"
    );
    APIDevelop.addResources(OPERATION_USERS, Enums.HTTPMethod.GET);
  });

  it("Add first mediation policy to the GET resource", () => {
    APIDevelop.addPolicy(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      Enums.PolicyType.setHeader,
      HEADER_KEY,
      HEADER_VALUE
    );
  });

  it("Verify component deployment to dev", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployProxyAPIToDev();
    APIDeployment.verifyProxyDeployment(PROJECT_NAME, API_NAME);
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify header values using curl in dev", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY]: HEADER_VALUE },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Verify prod promotion", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify header values using curl in prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY]: HEADER_VALUE },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Verify adding second mediation policy", () => {
    ComponentOverviewPage.navigateToDevelop();
    APIDevelop.addPolicy(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      Enums.PolicyType.setHeader,
      HEADER_KEY_2,
      HEADER_VALUE_2,
      2
    );
  });

  it("Verify component deployment to dev with new policy", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployProxyAPIToDev();
    APIDeployment.verifyProxyDeployment(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in Dev with new policy", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify test functionality using generated curl in dev with new policy", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY_2]: HEADER_VALUE_2 },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Verify prod promotion with new policy", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in Prod with new policy", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify test functionality using generated curl in prod with new policy", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY_2]: HEADER_VALUE_2 },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Verify mediation policy update functionality", () => {
    ComponentOverviewPage.navigateToDevelop();
    APIDevelop.editHeader(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      HEADER_VALUE_3
    );
  });

  it("Verify component deployment to dev with updated header value", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployProxyAPIToDev();
    APIDeployment.verifyProxyDeployment(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in Dev with updated header value", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify test functionality using generated curl in dev with updated header value", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY]: HEADER_VALUE_3 },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Verify prod promotion with updated header value", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in Prod with updated header value", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify test functionality using generated curl in prod with updated header value", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      OPERATION_USERS
    ).then((curl) => {
      const expected = {
        expectedCode: OK,
        expectedHeaders: { [HEADER_KEY]: HEADER_VALUE_3 },
      };
      Utils.sendGetRequestAndMatch(curl.url, curl.headers, expected);
    });
  });

  it("Create new version from the created API", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentOverviewPage.createNewVersion(API_NEW_VERSION, "");
    ComponentDevelopPage.getVersion().should(
      "eq",
      `API Version ${API_NEW_VERSION}`
    );
  });

  it("Add a resource to new version", () => {
    ComponentOverviewPage.navigateProxyResources();
    APIDevelop.addResources(OPERATION_POSTS, Enums.HTTPMethod.GET);
  });

  it("Deploy new version to Dev", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployProxyAPIToDev();
    APIDeployment.verifyProxyDeployment(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in dev", () => {
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });

    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      OPERATION_POSTS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Verify new version promotion to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd(PROJECT_NAME, API_NAME, true);
  });

  it("Verify test functionality using Swagger UI in prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      OPERATION_USERS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });

    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      OPERATION_POSTS
    ).then((res) => {
      expect(res.statusCode).to.be.equal("200");
    });
  });

  it("Publish the API to dev portal", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector();
  });

  it("Generate credentials for prod env", () => {
    ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(
      PROJECT_NAME,
      API_NAME,
      idpUser
    );
    Apis.searchApiAndSelect(API_NAME, 2, API_NEW_VERSION);
    ApiCredentials.navigateToEnvironment(Enums.Environment.PRODUCTION);
    ApiCredentials.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Tryout application", () => {
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(OPERATION_USERS);
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
