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

import { console } from "../../support/console/console";
import { devPortal } from "../../support/console/devportal";
import { Project } from "../../support/console/entities/project/project";
import { Proxy } from "../../support/console/entities/component/proxy-component";
import { ApiVisibility } from "../../support/commons/enums";

describe("Public access on devportal", () => {
  const PROJECT_DESCRIPTION = "sample oas flow scenario";

  let project: Project;
  let proxy: Proxy;

  after(() => {
    console.logout();
  });

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Create API Proxy", () => {
    project
      .createProxyComponent({
        version: "1.0",
        oasFilePath: "apis/generation_oas.yaml",
        endpointUrl: "",
      })
      .then((comp) => {
        proxy = comp;
      });
  });

  it("Deploy API Proxy", () => {
    proxy.deploy();
  });

  it("Publish proxy", () => {
    proxy.publish();
  });

  it("Update the API visibility to Private", () => {
    proxy.updateApiVisibility(ApiVisibility.Private);
  });

  it("Navigate to Dev portal public view", () => {
    proxy.navigateToPublicDevPortal();
  });

  it("Check for available public apis", () => {
    devPortal.verifyApiNotFound(proxy.getName());
  });

  it("Update the API visibility to public", () => {
    proxy.navigateToComponentInConsole();
    proxy.updateApiVisibility(ApiVisibility.Public);
  });

  it("Navigate to Dev portal public view", () => {
    proxy.navigateToPublicDevPortal();
  });

  it("Check for available public apis to confirm availability", () => {
    devPortal.searchApi(proxy.getName());
  });
});
