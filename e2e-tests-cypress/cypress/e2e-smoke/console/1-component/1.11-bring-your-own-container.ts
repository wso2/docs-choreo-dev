/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { Utils } from "../../../support/commons/utils";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GitHub } from "../../../support/github/github";
import { ByocComponent } from "../../../support/interfaces/byoc-component";

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe(`Verify BYOC functionality`, () => {
  const PROJECT_DESCRIPTION = "BYOC component";
  const PROJECT_NAME = Utils.generateProjectName();
  const REST_API_NAME = Utils.generateComponentName();
  const REPO_NAME = Utils.generateComponentName("repo");
  const RESOURCE_NAME = "movies";

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify REST API component creation", () => {
    let componentData: ByocComponent = {
      name: REST_API_NAME,
      displayName: REST_API_NAME,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocRestApi,
      description: "BYOC Component",
      labels: "",
      oasFilePath: "byoc-test/oas.yaml",
      port: 8080,
      projectId: "",
      byocConfig: {
        dockerfilePath: "byoc-test/Dockerfile",
        dockerContext: "byoc-test",
        srcGitRepoUrl:
          "https://github.com/choreo-test-apps/byor-greetings-app2",
        srcGitRepoBranch: "main",
      },
    };

    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getBYOCComponentCreationQuery
    );
  });

  it("Deploy component", () => {
    ComponentListingPage.visitToAComponent(REST_API_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.DEVELOPMENT, RESOURCE_NAME).then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify test functionality using generated curl in dev", () => {
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnSwagger(Enums.Environment.PRODUCTION, RESOURCE_NAME).then(
      (res) => {
        expect(res.statusCode).to.be.equal("200");
      }
    );
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Apply configs to dev", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration();
    ComponentAPILifecycle.verifyDevRevision().should(
      "eq",
      Enums.Environment.DEVELOPMENT
    );
  });

  it("Apply configs to prod", () => {
    ComponentAPILifecycle.selectEnvironment(Enums.Environment.PRODUCTION);
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
    ComponentAPILifecycle.applyConfiguration();
  });

  it("Verify test functionality using generated curl in Dev", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.testOnCurl(
      Enums.Environment.DEVELOPMENT,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    TestHelper.testOnCurl(
      Enums.Environment.PRODUCTION,
      Enums.HTTPMethod.GET,
      RESOURCE_NAME
    ).then((curl) => {
      Utils.sendGetRequest(curl.url).then((res) => {
        expect(res.status).equal(200);
      });
    });
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
