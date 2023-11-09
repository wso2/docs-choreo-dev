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
import { Utils } from "../../../support/commons/utils";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentExecutePage } from "../../../support/console/pages/component/UI-components/Component-execute-page";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GitHub } from "../../../support/github/github";
import { ComponentData } from "../../../support/interfaces/component-data";

describe("Verify manual trigger creation functionality", () => {
  const MANUAL_NAME = Utils.generateComponentName();
  const REPO_NAME = Utils.generateComponentName("repo");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "Manual Trigger";

  before(() => {
    LoginPage.login();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify Manual Trigger component creation", () => {
    let componentData: ComponentData = {
      componentName: MANUAL_NAME,
      displayType: Enums.DisplayType.manualTrigger,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/manual-trigger",
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      initializeAsBallerinaProject: false,
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
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Navigate to deployment", () => {
    ComponentListingPage.visitToAComponent(MANUAL_NAME);
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment", () => {
    ComponentDeployPage.deployToDevWithoutSplitButton(PROJECT_NAME,MANUAL_NAME,false, false, true);
  });

  it("Verify component promotion to prod", () => {
    ComponentDeployPage.promoteManualTriggerToProd();
  });

  it("Verify execution in dev", () => {
    ComponentOverviewPage.navigateToExecute();
    ComponentExecutePage.selectEnvironment(Enums.Environment.DEVELOPMENT);
    ComponentExecutePage.verifyExecution();
  });

  it("Verify execution in prod", () => {
    ComponentOverviewPage.navigateToExecute();
    ComponentExecutePage.selectEnvironment(Enums.Environment.PRODUCTION);
    ComponentExecutePage.verifyExecution();
  });

});
