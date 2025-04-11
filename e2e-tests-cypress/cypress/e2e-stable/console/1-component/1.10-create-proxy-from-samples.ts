/*
 * Copyright (c) 2025, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums, SecurityScheme } from "../../../support/commons/enums";
import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import { Proxy } from "../../../support/console/entities/component/proxy-component";
import { OK } from "../../../support/commons/http";

after(() => {
  console.logout();
});

describe("Verify Proxy Creation from Sample Repo", () => {
  const PROJECT_DESCRIPTION = "sample proxy creation scenario";
  let project: Project;
  let proxy: Proxy;
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "department-api-proxy-from-github";
  const RESOURCE = "department/{departmentId}";
  const ENDPOINT_URL = "https://samples.choreoapps.dev/company/hr";
  const EXPECTED_VALUE = "Finance";

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a proxy from choreo samples", () => {
    project
      .createProxyComponentFromGH({
        displayName: "",
        repoUrl: REPO_URL,
        directoryInfo: {
          directoryName: REPO_NAME,
          directoryTestid: REPO_NAME,
        },
        version: "1.0",
        endpointUrl: ENDPOINT_URL,
      })
      .then((comp) => {
        proxy = comp;
        // Since we are switching domains when navigating to devportal url we will no longer have access to the proxy object
        // So we need to save the proxy metadata in nodejs global state using below cy.task() to access it later
        cy.task("setData", {
          key: Cypress.spec.name,
          value: proxy.getMetaData(),
        });
      });
  });

  it("Build the component", () => {
    proxy.build();
  });

  it("Enable OAuth2 security and deploy", () => {
    proxy.enableSecuritySchemesAndDeploy([SecurityScheme.OAuth2]);
  });

  it("Promote proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    proxy
      .testSwaggerConsole(
        Enums.Environment.DEVELOPMENT,
        RESOURCE,
        "departmentId",
        "1"
      )
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    proxy
      .testSwaggerConsole(
        Enums.Environment.PRODUCTION,
        RESOURCE,
        "departmentId",
        "1"
      )
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });
  });
});
