import { LONG_TIME } from "../../../support/console/constants";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ScheduleTask } from "../../../support/console/pages/templates/schedule-task-template";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { VSSourceControl } from "../../../support/console/pages/vscod-editor/vs-source-control";
import { Utils } from "../../../support/console/utils";

describe("Schedule task", () => {
  const FILE_ID = "oasflow";
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const API_Name = Utils.generateComponentName("sch");
  const API_BASE_PATH = Utils.generateBasePath();
  const commitMessage = "adding task method";
  const EXPECTED_RESULT ='{"userId":1,"id":1,"title":"delectus aut autem","completed":false}'

  before(() => {
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a schedule task", () => {
    cy.log("Starting API Creation using open API specification");
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    ScheduleTask.selectTask();
    ScheduleTask.createTask(API_Name, PROJECT_DESCRIPTION);
    ComponentDevelopPage.getComponentURL();
  });

  it("Verify code edit in vscode", () => {
    LoginPage.navigateToCodespace();
    VSExplorer.typeCode("scheduletask.bal");
    VSExplorer.selectSourceControl();
    VSExplorer.enterCommandInTerminal(
      "bash /config/workspace/.githooks/pre-commit"
    );
    VSExplorer.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit"
    );
    VSSourceControl.commitChanges(commitMessage);
    VSExplorer.enterCommandInTerminal("git push");
    VSExplorer.waitTillCodeSyncWithChoreo();
  });

  it("Verify component commits", () => {
    LoginPage.reLoginToChoreo();
    ComponentDevelopPage.verifyLatestCommit(commitMessage);
  });

  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployScheduleTask();
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteScheduleTask();
  });

  it("Verify task execution in observability ", () => {
    ComponentOverviewPage.navigateToObserve();
    ComponentObservePage.gotoLogs(LONG_TIME);
    ComponentObservePage.verifyTextInLogs(EXPECTED_RESULT).should('be.true')
  });
});
