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
import { ComponentDeployPage } from "../../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../../support/interfaces/component-data";


const dp = Enums.Region.US;

before(() => {
  LoginPage.login();
});
after(() => {
  ChoreoHomePage.logout();
});


describe(`Graphql GQL service functionality in region ${dp}`, () => {
  const PROJECT_NAME = "Default Project";
  const COMPONENT_NAME = "create-GqlService-1.12.1";

  it("Verify GraphQL component creation", () => {
    let componentData: ComponentData = {
      componentName: COMPONENT_NAME,
      displayType: Enums.DisplayType.graphql,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: PROJECT_NAME,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/gql-service",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: ""
    };
    ProjectListingPage.selectProject();
    ProjectOverviewPage.searchReuseComponent(componentData);
  });


  it("Verify component deployment", () => {
    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev(PROJECT_NAME,COMPONENT_NAME);
  });


  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
  });

  it("Verify suspending Prod deployed component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

});


