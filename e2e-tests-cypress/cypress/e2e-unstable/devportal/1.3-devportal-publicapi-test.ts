import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { ApiOverview } from "../../support/devportal/pages/apis/api-overview";
import { LoginPage as ConsoleLoginPage } from "../../support/console/pages/login-page";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { DevPortalHelper } from "../../support/devportal/helpers/devportal-helper";
import { ComponentOverviewPage } from "../../support/console/pages/component/component-overview-page";
import { ComponentAPILifecycle } from "../../support/console/pages/component/component-manage-page";
import { ComponentListingPage } from "../../support/console/pages/component/component-listing-page";
import { ProjectListingPage } from "../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../support/commons/utils";

describe("Public access on devportal", () => {
  const API_Name = Utils.generateComponentName("rest");
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
  const projectName = Utils.generateProjectName();

  before(() => {
    ConsoleLoginPage.login();
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Creating a project", () => {
    ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
  });

  it("Create and deploy a component", () => {
    DevPortalHelper.createDeployBalServiceComponent(API_Name, projectName);
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.verifyAPIVisibility("Private");
  });

  it("Check for available public apis", () => {
    DevportalLoginPage.visitToDevportalOrgPublicApis();
    DevPortalHomePage.verifyDevportalHomePagePublicView();
    DevPortalHomePage.navigateToApisPage();
    Apis.confirmAPIUnavailability(API_Name);
  });

  it("Update the API visibility to public", () => {
    ConsoleLoginPage.visitToHomePage();
    ProjectListingPage.selectProject(projectName as string);
    ComponentListingPage.visitToAComponent(API_Name as string);
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.updateAPIVisibility("Public");
  });

  it("Check for available public apis to confirm availability", () => {
    DevportalLoginPage.visitToDevportalOrgPublicApis();
    DevPortalHomePage.navigateToApisPage();
    Apis.navigateToApiOverview(API_Name as string);
    ApiOverview.confirmPublicAPIOverview();
    ApiOverview.confirmPublicAPIResourcePage();
  });
});
