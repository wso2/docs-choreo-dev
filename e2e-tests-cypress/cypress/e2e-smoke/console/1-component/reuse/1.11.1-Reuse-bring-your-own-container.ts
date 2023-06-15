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

import { Enums } from "../../../../support/commons/enums";
import { Utils } from "../../../../support/commons/utils";
import { TestHelper } from "../../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { ByocComponent } from "../../../../support/interfaces/byoc-component";

const dp = Enums.Region.US;

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe(`Verify BYOC functionality in region ${dp}`, () => {
  const BYOC_NAME = "create-ReuseBYOC";
  const RESOURCE_NAME = "movies";
  const PROJECT_NAME="Default Project"

  it("Verify BYOC REST API component creation", () => {
    let componentData: ByocComponent = {
      name: BYOC_NAME,
      displayName: BYOC_NAME,
      componentName: BYOC_NAME,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocRestApi,
      description: "BYOC Component",
      labels: "",
      oasFilePath: "byoc-test/oas.yaml",
      projectId: "",
      port: 8080,
      byocConfig: {
        dockerfilePath: "byoc-test/Dockerfile",
        dockerContext: "byoc-test",
        srcGitRepoUrl:
          "https://github.com/choreo-test-apps/byor-greetings-app2",
        srcGitRepoBranch: "main",
      },
    };
    ProjectListingPage.selectProject();
    ProjectOverviewPage.searchReuseComponent(
      componentData,
      PROJECT_NAME,
      true
    );
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(BYOC_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Deploy component", () => {
    ComponentDeployPage.deployToDev(PROJECT_NAME,BYOC_NAME);
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

  it("Verify suspending deployments", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });
});
