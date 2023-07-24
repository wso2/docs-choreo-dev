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
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { OK } from "../../../support/commons/http";

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
  const API_ENDPOINT = "https://jsonplaceholder.typicode.com";
  const OPERATION_USERS = "users";
  const PROJECT_DESCRIPTION = "sample stats project";
  const PROJECT_NAME = Utils.generateProjectName();
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
      HEADER_VALUE_2
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

  it("Verify application suspension", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
