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

import { Enums } from "../../commons/enums";
import { Utils } from "../../commons/utils";
import { GraphQLQueryBuilder } from "../../console/apis/gql-query-builder";
import { GraphQL } from "../../console/apis/graphql";
import { APIDeployment } from "../../console/pages/apis/api-deployment";
import { ComponentDeployPage } from "../../console/pages/component/component-deploy";
import { ComponentListingPage } from "../../console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../console/pages/component/component-overview-page";
import { ProjectOverviewPage } from "../../console/pages/projects/project-overview";
import { ProjectListingPage } from "../../console/pages/projects/projects-listing-page";
import { RestAPIProxyTemplate } from "../../console/pages/templates/rest-api-proxy-temp";

import { ComponentData } from "../../interfaces/component-data";

export class DevPortalHelper {
  static API_BASE_PATH = Utils.generateBasePath();
  static Filepath = "apis/generation_oas.yaml";
  static REPO_NAME = Utils.generateComponentName("repo");

  static createDeployHttpProxyComponent(API_Name, projectName) {
    ProjectOverviewPage.createHttpProxyAPI();
    RestAPIProxyTemplate.createOpenApi(DevPortalHelper.Filepath);
    RestAPIProxyTemplate.enterAPIdetails(
      API_Name,
      DevPortalHelper.API_BASE_PATH,
      "",
      "",
      "",
      ""
    );
    ComponentOverviewPage.navigateToDeploy();
    APIDeployment.DeployToDev();
    APIDeployment.promoteToProd();
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
  }

  static createDeployRestApiComponent(API_Name, projectName) {
    let componentData: ComponentData = {
      componentName: API_Name,
      displayType: Enums.DisplayType.restAPI,
      accessibility: Enums.Accessibility.EXTERNAL,
      projectName: projectName,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: "https://github.com/choreo-test-apps/rest-api",
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: "",
      sampleTemplate: "",
    };

    GraphQL.createComponent(
      projectName,
      DevPortalHelper.REPO_NAME,
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    ).then(() => {
      ComponentListingPage.visitToAComponent(API_Name);
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.deployToDev(projectName,componentData.displayName);
      ComponentOverviewPage.navigateToManage();
      ComponentAPILifecycle.manageLifecycle();
      ComponentAPILifecycle.publishWithoutConnector().should("be.visible");
    });
  }
}
