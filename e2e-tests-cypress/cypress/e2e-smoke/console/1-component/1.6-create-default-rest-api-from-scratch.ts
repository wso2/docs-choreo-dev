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
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Environment } from "../../../support/console/pages/enum/environment";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { Utils } from "../../../support/console/utils";
import { REUSABLE_PROJECT_NAME } from "../../../support/devportal/constants";

describe("Verify REST API component creation functionality", () => {
  
  const RESOURCE_NAME = "greeting";
  const PARAM_NAME = "name";
  const PARAM_VALUE = "World";
  const MATCHING_STRING = "Hello, " + PARAM_VALUE;
  const queryParameters1 = [{ key: PARAM_NAME, value: PARAM_VALUE }];
  const COMPONENT_NAME = "create-default-Rest-API-1.6";

  before(() => {
    LoginPage.login();
    ChoreoHomePage.switchOrganization();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Verify REST API component creation", () => {
    ProjectListingPage.selectProject(REUSABLE_PROJECT_NAME);
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Environment.DEVELOPMENT, RESOURCE_NAME, PARAM_NAME, PARAM_VALUE).
      then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING);
        expect(res.statusCode).to.be.eq("200");
      });
  });

  it("Verify test functionality of root resource in dev on curl", () => {
    TestHelper.testOnCurl(Environment.DEVELOPMENT, HTTPMethod.GET, RESOURCE_NAME, queryParameters1).
    then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });

  });

  it("Verify test functionality of root resource in prod on swagger", () => {

    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Environment.PRODUCTION, RESOURCE_NAME, PARAM_NAME, PARAM_VALUE).
      then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING);
        expect(res.statusCode).to.be.eq("200");
      });
  });

  it("Verify test functionality of root resource in prod on curl", () => {

    TestHelper.testOnCurl(Environment.PRODUCTION, HTTPMethod.GET, RESOURCE_NAME, queryParameters1).
    then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Apply configs to dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.selectEnvironment(Environment.DEVELOPMENT)
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(Environment.DEVELOPMENT,"Revision 3");
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Environment.DEVELOPMENT
    );
  });

  it("Apply configs to prod", () => {
    ComponentAPILifecycle.selectEnvironment(Environment.PRODUCTION)
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration(Environment.PRODUCTION);
  });

  it("Verify resource access without the token in dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurl(Environment.DEVELOPMENT, HTTPMethod.GET, RESOURCE_NAME, queryParameters1).
    then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
    
  });

  it("Verify resource access without the token in prod", () => {
    TestHelper.testOnCurl(Environment.PRODUCTION, HTTPMethod.GET, RESOURCE_NAME).
    then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify manage functionality and Publish Connector", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
  });

  it("Verify connector republishing", () => {
    ComponentAPILifecycle.republishConnector();
  });

  it("Verify settings configuration", () => {
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});

