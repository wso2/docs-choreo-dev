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
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";

describe("Verify project creation functionality", () => {
  const queryParameters1 = [{ key: "number", value: "2" }];
  const queryParameters2 = [{ key: "number", value: "5" }];
  const COMPONENT_NAME = Utils.generateComponentName();
  const REPO_NAME = Utils.generateComponentName("repo");
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();

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
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.restAPI,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/rest-api",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployToDev(PROJECT_NAME,COMPONENT_NAME);
  });

  it("Verify test functionality of root resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
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
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      "root",
      queryParameters1
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(4);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality of isOdd resource in dev on swagger", () => {
    TestHelper.testOnSwagger(
      Enums.Environment.DEVELOPMENT,
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
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      "isOdd",
      queryParameters2
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(true);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality of root resource in prod on swagger", () => {
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      "root",
      "number",
      "2"
    ).then((res) => {
      expect(res.response).to.be.eq("4");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      "root",
      queryParameters1
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(4);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality of isOdd resource in prod on swagger", () => {
    TestHelper.testOnSwagger(
      Enums.Environment.PRODUCTION,
      "isOdd",
      "number",
      "5"
    ).then((res) => {
      expect(res.response).to.be.eq("true");
      expect(res.statusCode).to.be.eq("200");
    });
  });

  it("Verify test functionality using generated curl in prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      "isOdd",
      queryParameters2
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.body).equal(true);
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.changeLifeCycleToPublished(
      Enums.ConnectorAudience.PRIVATE
    );
  });

  it("Verify connector publishing ", () => {
    ComponentAPILifecycle.publishConnector(Enums.ConnectorAudience.PRIVATE);
  });

  it("Verify connector republishing ", () => {
    ComponentAPILifecycle.republishConnector();
  });

  it("Verify suspending all component deployments", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
