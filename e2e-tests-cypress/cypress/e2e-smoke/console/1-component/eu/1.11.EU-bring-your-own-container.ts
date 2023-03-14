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

import { GraphQLQueryBuilder } from "../../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../../support/console/apis/graphql";
import { Enums } from "../../../../support/console/enums";
import { TestHelper } from "../../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../../support/console/utils";
import { ByocComponent } from "../../../../support/interfaces/byoc-component";

const dp = Enums.Region.EU;

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe(`Verify BYOC functionality in region ${dp}`, () => {
  const PROJECT_DESCRIPTION = "BYOC component";
  const PROJECT_NAME = Utils.generateProjectName();
  const REST_API_NAME = Utils.generateComponentName("byor");
  const REPO_NAME = Utils.generateComponentName("repo");
  const RESOURCE_NAME = "movies";

  it("Verify REST API component creation", () => {
    let componentData: ByocComponent = {
      name: REST_API_NAME,
      displayName: REST_API_NAME,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocRestApi,
      description: "BYOC Component",
      labels: "",
      oasFilePath: "",
      projectId: "",
    };
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION, dp);
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

  
  it("Verify component promote to prod", () => {
       ComponentDeployPage.promoteToProd();
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentDeployPage.stopAllDeployment();
  });
});
