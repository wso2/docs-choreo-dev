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
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { Enums } from "../../../support/console/enums";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";

describe("Verify project creation functionality", () => {
  const COMPONENT_NAME = "create-rest-api-from-scratch-1.3";
  const REPO_NAME = Utils.generateComponentName("repo");
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();

  before(() => {
    LoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
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
    ChoreoHomePage.changeToAPIPerspective();
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      Enums.Region.US,
      Enums.Perspective.APIM
    );
    GraphQL.createComponent(PROJECT_NAME, REPO_NAME, componentData, GraphQLQueryBuilder.getRestComponentCreationQuery)
  });


});
