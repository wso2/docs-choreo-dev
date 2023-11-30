/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { Enums } from "../../../support/commons/enums";
import { MEDIUM_TIME, SHORT_TIME } from "../../../support/commons/timeouts";
import { Utils } from "../../../support/commons/utils";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GraphQL } from "../../../support/console/apis/graphql";
import { ComponentBuild } from "../../../support/console/pages/component/Functionalities/Component-build";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentData } from "../../../support/interfaces/component-data";

before(() => {
  LoginPage.login();
});

after(() => {
  ChoreoHomePage.logout();
});

describe("Verify Ballerina service functionality", () => {
  const COMPONENT_NAME = Utils.generateComponentName("ballerina-service");
  const PROJECT_NAME = Utils.generateProjectName();
  const PROJECT_DESCRIPTION = "sample ballerina service scenario";
  const REPO_NAME = Utils.generateComponentName("repo");
  const LOG_MESSAGE = "MATCHING RESPONSE";
  const MANUAL_NAME = Utils.generateComponentName("manual-trigger");

  const MANUAL_TRIGGER: ComponentData = {
    componentName: MANUAL_NAME,
    displayType: Enums.DisplayType.manualTrigger,
    accessibility: Enums.Accessibility.EXTERNAL,
    projectName: PROJECT_NAME,
    triggerChannels: "",
    triggerId: null,
    srcGitRepoUrl:
      "https://github.com/choreo-test-apps/book-service-manual-trigger",
    repositoryType: Enums.RepoType.UserManagedNonEmpty,
    initializeAsBallerinaProject: false,
    repositorySubPath: "",
    sampleTemplate: "",
  };

  const BALLERINA_SERVICE: ComponentData = {
    componentName: COMPONENT_NAME,
    displayType: Enums.DisplayType.ballerinaService,
    accessibility: Enums.Accessibility.EXTERNAL,
    projectName: PROJECT_NAME,
    triggerChannels: "",
    triggerId: null,
    srcGitRepoUrl: "https://github.com/choreo-test-apps/byor-service-app1",
    initializeAsBallerinaProject: false,
    repositoryType: Enums.RepoType.UserManagedNonEmpty,
    repositorySubPath: "",
    sampleTemplate: "",
  };

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Verify Ballerina service component creation", () => {
    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      BALLERINA_SERVICE,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );

    ComponentListingPage.visitToAComponent(COMPONENT_NAME);
  });

  it("Navigate to deployment", () => {
    if (Utils.isBuildDeployEnabled()) {
      ComponentOverviewPage.navigateToBuild();
      ComponentBuild.buildComponent();
    }
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Initiate service deployment.", () => {
    ComponentDeployPage.initiateServiceDeployment("project");
    ComponentDeployPage.getVisibilityLevel().should("equal", "Project");
  });

  it("Deploy the service", () => {
    ComponentDeployPage.deployServiceWithVisibilityLevel();
    ComponentDeployPage.verifyDeploymentStatusOfService(
      PROJECT_NAME,
      COMPONENT_NAME
    ).should("equal", "Active");
  });

  it("Promote the service", () => {
    ComponentDeployPage.promoteServiceWithVisibilityLevel().should(
      "equal",
      "Active"
    );
  });

  it("Verify test page for project level endpoint", () => {
    ComponentOverviewPage.navigateToTest();
    TestHelper.verifyProjectLevelEndpoint();
  });

  it("Get project endpoints", () => {
    ComponentOverviewPage.navigateToOverview();
    ComponentOverviewPage.getServiceInvokeUrl("Project");
  });

  it("Get projects namespace", () => {
    ComponentOverviewPage.navigateToDevops();
    ComponentOverviewPage.navigateToRuntime();
    ComponentOverviewPage.generateProxyUrl("Project");
  });

  it("Verify Manual Trigger component creation", () => {
    GraphQL.createComponent(
      PROJECT_NAME,
      REPO_NAME,
      MANUAL_TRIGGER,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    );
  });

  it("Navigate to deployment", () => {
    ChoreoHomePage.closeComponentViews(COMPONENT_NAME);
    ChoreoHomePage.navigateToComponents();
    ComponentListingPage.visitToAComponent(MANUAL_NAME);
    if (Utils.isBuildDeployEnabled()) {
      ComponentOverviewPage.navigateToBuild();
      ComponentBuild.buildComponent();
    }
    ComponentOverviewPage.navigateToDeploy();
  });

  it("Verify component deployment", () => {
    const proxyUrl = Cypress.env("PROXY_URL");
    ComponentDeployPage.deployManualTriggerWithConfig(proxyUrl);
  });

  it("Run manual trigger in DEV", () => {
    ComponentDeployPage.runManualTrigger(Enums.Environment.DEVELOPMENT, 3);
  });

  it("Verify component promotion to prod", () => {
    const proxyUrl = Cypress.env("PROXY_URL");
    ComponentDeployPage.promoteManualTriggerWithConfig(proxyUrl);
  });

  it("Run manual trigger in PROD", () => {
    ComponentDeployPage.runManualTrigger(Enums.Environment.PRODUCTION, 3);
  });

  it("Verify navigate to observability Page", () => {
    ComponentOverviewPage.navigateToObserve(MEDIUM_TIME.timeout);
  });

  it("Verify dev env logs", () => {
    ComponentObservePage.selectEnv(Enums.Environment.DEVELOPMENT);
    ComponentObservePage.verifyManualTriggerTextInLogs(LOG_MESSAGE);
  });

  it("Verify prod env logs", () => {
    ComponentObservePage.selectEnv(Enums.Environment.PRODUCTION);
    ComponentObservePage.verifyManualTriggerTextInLogs(LOG_MESSAGE);
  });
});
