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

import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";
import { Proxy } from "../../../support/console/entities/component/proxy-component";
import { BuildPacks, EndpointAccessibility, Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";

describe("Verify Component visibility functionality", () => {
  const PROJECT_DESCRIPTION = "Component Visibility Test";
  const ENDPOINT_NAME = "Hello";
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "hello-world";

  let project: Project;
  let service: Service;
  let proxy: Proxy;

  const OPERATION = "greeting";

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a ballerina service from choreo samples", () => {
    project
      .createServiceComponentUI({
        displayName: "",
        repoUrl: REPO_URL,
        buildPack: BuildPacks.Ballerina,
        repoName: REPO_NAME,
        repoTestid: REPO_NAME,
        ENDPOINT_NAME,
      })
      .then((comp) => {
        service = comp;
      });
  });

  it("Build the service", () => {
    service.build();
  });

  it("Deploying the service with Project level visibility", () => {
    service.deployProjectLevelAccessibility();
  });

  it("Promoting the service with Project level visibility", () => {
    service.promoteProjectLevelAccessibility();
  });

  it("Return to Project", () => {
    service.goBackToProject();
  });

  it("Creating a proxy from scratch", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: service.getDevEndpointUrl(EndpointAccessibility.Project),
      })
      .then((comp) => {
        proxy = comp;
      });
  });

  it("Remove default resources", () => {
    proxy.removeDefaultResources();
  });

  it("Add resource to proxy", () => {
    proxy.addResources([
      { path: OPERATION, verbs: [Enums.HTTPMethod.GET] },
    ]);
  });

  it("Deploy proxy", () => {
    proxy.deploy();
  });

  it("Promote proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain("Hello, World!");
      });
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain("Hello, World!");
      });
  });
});
