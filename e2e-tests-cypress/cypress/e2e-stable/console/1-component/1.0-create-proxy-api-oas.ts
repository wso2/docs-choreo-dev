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

import { Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";
import { Utils } from "../../../support/commons/utils";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";

describe("Create proxy api using existing url", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  let PROJECT_NAME;
  let API_NAME;
  let API_BASE_PATH;
  const URL = "https://petstore3.swagger.io/api/v3/openapi.json";
  const ENDPOINT_URL =
    "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/petstore/petstore-9f2/v1.0";
  const API_NEW_VERSION = "1.1";
  const RESOURCE = "store/inventory";
  const NEW_RESOURCE = "pet/{petId}";
  const idpUser = "choreoe2etest";
  const EXPECTED_VALUE = "sold";
  const NEW_RESOURCE_EXPECTED_VALUE = "id";

  before(() => {
    LoginPage.login();
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
    RestAPIProxyTemplate.createOpenApi("", URL);
    RestAPIProxyTemplate.enterAPIdetails(
      API_NAME,
      API_BASE_PATH,
      ENDPOINT_URL,
      "1.0",
      "pet/{petId}",
      ""
    );
  });

  it("Remove additional resources and save", () => {
    APIDevelop.removeResources([
      "panel-/pet/{petId}/uploadImage/post-header",
      "panel-/pet/post-header",
      "panel-/pet/put-header",
      "panel-/pet/findByStatus/get-header",
      "panel-/pet/findByTags/get-header",
      "panel-/pet/{petId}/get-header",
      "panel-/pet/{petId}/post-header",
      "panel-/pet/{petId}/delete-header",
    ]);

    APIDevelop.removeResources([
      "panel-/user/createWithArray/post-header",
      "panel-/user/createWithList/post-header",
      "panel-/user/{username}/get-header",
      "panel-/user/{username}/put-header",
      "panel-/user/{username}/delete-header",
    ]);

    APIDevelop.removeResources([
      "panel-/user/post-header",
      "panel-/user/login/get-header",
      "panel-/user/logout/get-header",
    ]);
  });

  it("Verify component deployment and endpoint configurations", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployToDev();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.DEVELOPMENT, RESOURCE).then(
      (res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      }
    );
  });

  it("Verify test functionality using generated curl in Dev", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(OK);
        expect(res.body).to.have.property(EXPECTED_VALUE);
      });
    });
  });

  it("Verify prod promotion", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd();
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.PRODUCTION, RESOURCE).then(
      (res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      }
    );
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(OK);
        expect(res.body).to.have.property(EXPECTED_VALUE);
      });
    });
  });

  it("Create new version from the created API", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentOverviewPage.createNewVersion(API_NEW_VERSION, "");
    ComponentDevelopPage.getVersion().should("eq", `v${API_NEW_VERSION}`);
  });

  it("Add a resource to new version", () => {
    APIDevelop.addResources(NEW_RESOURCE, Enums.HTTPMethod.GET);
  });

  it("Verify component deployment and endpoint configurations", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.deployToDev();
  });

  it("Verify test functionality of new version using Swagger UI in dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.DEVELOPMENT, RESOURCE).then(
      (res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      }
    );

    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      NEW_RESOURCE,
      "petId",
      "1"
    ).then((res) => {
      expect(res.statusCode).to.be.equal(OK.toString());
      expect(res.response).to.contain(NEW_RESOURCE_EXPECTED_VALUE);
    });
  });

  it("Verify new version promotion to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.promoteToProd();
  });

  it("Verify test functionality of new version using Swagger UI in prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.PRODUCTION, RESOURCE).then(
      (res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      }
    );

    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      NEW_RESOURCE,
      "petId",
      "1"
    ).then((res) => {
      expect(res.statusCode).to.be.equal(OK.toString());
      expect(res.response).to.contain(NEW_RESOURCE_EXPECTED_VALUE);
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
    Apis.searchApiAndSelect(API_NAME, 2);
    ApiCredentials.navigateToEnvironment(Enums.Environment.PRODUCTION);
    ApiCredentials.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Tryout application", () => {
    TryOut.navigateToTryOutMenu();
    TryOut.GenerateAccessToken();
    TryOut.SelectResource(RESOURCE);
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
