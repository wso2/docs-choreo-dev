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

import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GitHub } from "../../../support/github/github";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";

describe("Verify BYOR functionality", () => {
  const PROJECT_DESCRIPTION = "Internal API Test";
  const PROJECT_NAME = Utils.generateProjectName();
  const REST_API_NAME = Utils.generateComponentName();
  const REPO_NAME = Utils.generateComponentName("repo");
  const RESOURCE_NAME = "greeting";
  const PARAM_NAME = "name";
  const PARAM_VALUE = "World";
  const MATCHING_STRING = "Hello ra, " + PARAM_VALUE;
  const queryParameters1 = [{ key: PARAM_NAME, value: PARAM_VALUE }];

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify REST API component creation", () => {
    let componentData: ComponentData = {
      componentName: REST_API_NAME,
      displayType: Enums.DisplayType.restAPI,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl:
        "https://github.com/choreo-test-apps/byor-greetings-app2/tree/main/test",
      repositorySubPath: "test",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      sampleTemplate: "",
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Deploy component", () => {
    ComponentListingPage.visitToAComponent(REST_API_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev(PROJECT_NAME,REST_API_NAME);
  });

  it("Verify test functionality of root resource on swagger in dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
      RESOURCE_NAME,
      PARAM_NAME,
      PARAM_VALUE
    ).then((res) => {
      expect(res.response).to.be.eq(MATCHING_STRING);
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality of root resource on curl in dev", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters1
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality of root resource on swagger in prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      RESOURCE_NAME,
      PARAM_NAME,
      PARAM_VALUE
    ).then((res) => {
      expect(res.response).to.be.eq(MATCHING_STRING);
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality of root resource on curl in prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME,
      queryParameters1
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify resource access without the token in prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(MATCHING_STRING);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
