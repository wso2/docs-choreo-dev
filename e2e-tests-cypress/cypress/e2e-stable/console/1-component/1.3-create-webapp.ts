/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { WebApp } from "../../../support/console/concepts/component/webapp/webapp-component";
import {
  Project,
  RepoInfo,
  WebAppInfo,
} from "../../../support/console/concepts/project/project";
import { console } from "../../../support/console/console";

describe("Create Web App", () => {
  const PROJECT_DESCRIPTION = "Web App";

  const repoInfo: RepoInfo = {
    url: "https://github.com/choreo-test-apps/byor-service-app1",
    branch: "main",
    dockerContext:
      "cloud-native-app-developer/reading-list-front-end-with-managed-auth",
  };

  const webAppInfo: WebAppInfo = {
    webAppType: "React",
    webAppBuildCommand: "npm install && npm run build",
    webAppPackageManagerVersion: "18",
    webAppOutputDirectory: "dist",
  };

  let project: Project;
  let webApp: WebApp;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a Web App", () => {
    project
      .createWebAppComponent(Enums.Accessibility.EXTERNAL, repoInfo, webAppInfo)
      .then((app: WebApp) => {
        webApp = app;
      });
  });
});
