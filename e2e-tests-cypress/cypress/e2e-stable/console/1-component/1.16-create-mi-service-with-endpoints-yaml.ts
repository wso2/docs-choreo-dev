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

import { BuildPacks, Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";
import { createDefaultSteps } from "../../../support/commons/types";
import { console } from "../../../support/console/console";
import { Service } from "../../../support/console/entities/component/service-component";
import { Project } from "../../../support/console/entities/project/project";

describe("Verify MI service with endpoint.yaml functionality", () => {
  const PROJECT_DESCRIPTION = "MI Service with endpoint.yaml component";
  const MATCHING_STRING = "World";
  const ENDPOINT_NAME = "HelloWorld";
  const RESOURCE_NAME = "";
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "hello-world-mi";
  const BRANCH = "choreo-test-mi-endpoints-yaml";

  let project: Project;
  let service: Service;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a MI service from choreo samples", () => {
    project
      .createMIServiceEndpointComponentUI({
        displayName: "",
        repoUrl: REPO_URL,
        branch: BRANCH,
        buildPack: BuildPacks.MI,
        directoryInfo: { directoryName: REPO_NAME, directoryTestid: REPO_NAME }
      }, 
      ENDPOINT_NAME)
      .then((comp) => {
        service = comp;
      });
  });

  it("Build the component", () => {
    service.build();
  });

  it("Deploying the component with Public level visibility", () => {
    service.deployPublicLevelAccessibility(false);
  });

  it("Verifying component promotion to Prod", () => {
    service.promotePublicLevelAccessibility(createDefaultSteps(2), false);
  });

  it("Testing the component in Dev", () => {
    service
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: RESOURCE_NAME,
        method: "",
        parentComponentId: "",
      })
      .then((res) => {
        expect(res.response).to.include(MATCHING_STRING);
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });

  it("Testing the component in Prod", () => {
    service
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: RESOURCE_NAME,
        method: "",
        parentComponentId: "",
      })
      .then((res) => {
        expect(res.response).to.include(MATCHING_STRING);
        expect(res.statusCode).to.be.eq(OK.toString());
      });
  });
});
