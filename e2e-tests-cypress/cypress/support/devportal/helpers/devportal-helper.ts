/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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


import { APIDeployment } from "../../console/pages/apis/api-deployment";
import { ComponentDeployPage } from "../../console/pages/component/component-deploy";
import { ComponentAPILifecycle } from "../../console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../console/pages/component/component-overview-page";
import { ProjectOverviewPage } from "../../console/pages/projects/project-overview";
import { ProjectListingPage } from "../../console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../console/pages/templates/rest-api-proxy-temp";
import { RestAPITemplate } from "../../console/pages/templates/rest-api-temp";
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

  static createDeployHttpProxyComponent(API_Name) {
    ProjectListingPage.createNewProject(DevPortalHelper.PROJECT_NAME, DevPortalHelper.PROJECT_DESCRIPTION);
    ProjectOverviewPage.addNewComponent();
    RestAPIProxyTemplate.SelectHttpProxyAPITemplate();
    RestAPIProxyTemplate.createOpenApi(DevPortalHelper.Filepath);
    RestAPIProxyTemplate.enterAPIdetails(API_Name, DevPortalHelper.API_BASE_PATH, "", "", "");
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.PromoteToProd()
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  }

  static createDeployRestApiComponent(API_Name, description, projectName = DevPortalHelper.PROJECT_NAME) {
    ProjectListingPage.createNewProject(projectName, DevPortalHelper.PROJECT_DESCRIPTION);
    ProjectListingPage.selectProject(projectName);
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(API_Name, description);
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deployToDev();
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  }
}
