import { APIDeployment } from "../../console/pages/apis/api-deployment";
import { ComponentAPILifecycle } from "../../console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../console/pages/component/component-overview-page";
import { ProjectOverviewPage } from "../../console/pages/projects/project-overview";
import { ProjectListingPage } from "../../console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../console/pages/templates/rest-api-proxy-temp";
import { Utils } from "../../console/utils";
import { generateAppName } from "../utils";

export class DevPortalHelper {
  static FILE_ID = "oasflow";
  static PROJECT_DESCRIPTION = "sample oas flow scenario";
  static PROJECT_NAME = Utils.generateProjectName();

  static API_BASE_PATH = Utils.generateBasePath();
  static Filepath = "apis/generation_oas.yaml";
  static idpUser = "choreoe2etest";
  static OPERATION_USERS = "intensity";
  static appName = generateAppName("-e2etest");

  static createDeployComponent(API_Name) {
    ProjectListingPage.createNewProject(
      DevPortalHelper.PROJECT_NAME,
      DevPortalHelper.PROJECT_DESCRIPTION
    );
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.createOpenApi(DevPortalHelper.Filepath);
    RestAPIProxyTemplate.enterAPIdetails(
      API_Name,
      DevPortalHelper.API_BASE_PATH,
      "",
      "",
      ""
    );
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();

    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  }
}
