/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { Project } from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";
import { Byoc } from "../../../support/console/entities/component/byoc-component";
import { OK } from "../../../support/commons/http";

after(() => {
  console.logout();
});

describe(`Verify BYOC functionality`, () => {
  const PROJECT_DESCRIPTION = "BYOC component";
  const RESOURCE_NAME = "movies";

  let project: Project;
  let byoc: Byoc;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify BYOC REST API component creation", () => {
    project
      .createByocComponent(
        {
          url: "https://github.com/choreo-test-apps/byor-greetings-app2",
          branch: "main",
        },
        {
          dockerfilePath: "byoc-test/Dockerfile",
          dockerContext: "byoc-test",
        },
        "byoc-test/oas.yaml"
      )
      .then((comp: Byoc) => {
        project.visitComponent(comp.getName());
        byoc = comp;
      });
  });

  it("Build the Component", () => {
    byoc.build();
  });

  it("Deploying to Dev", () => {
    byoc.deployToDev();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    byoc
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, RESOURCE_NAME)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    byoc
      .testCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME
      )
      .then((res) => {
        expect(res.status).equal(OK);
      });
  });

  it("Verify component promotion to Prod", () => {
    byoc.promoteProd();
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    byoc
      .testSwaggerConsole(Enums.Environment.PRODUCTION, RESOURCE_NAME)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    byoc
      .testCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME
      )
      .then((res) => {
        expect(res.status).equal(OK);
      });
  });

  it("Stop component deployments", () => {
    byoc.stopDeployment();
    byoc.stopPromotion();
  });
});
