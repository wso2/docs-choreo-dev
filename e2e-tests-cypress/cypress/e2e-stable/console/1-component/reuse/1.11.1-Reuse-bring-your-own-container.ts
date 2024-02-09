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

import { Enums } from "../../../../support/commons/enums";
import { Byoc } from "../../../../support/console/entities/component/byoc-component";
import { console } from "../../../../support/console/console";
import { Project } from "../../../../support/console/entities/project/project";
import { OK } from "../../../../support/commons/http";


after(() => {
  console.logout();
});


describe(`Create Reusable BYOC`, () => {

  let project: Project;
  let byoc: Byoc;

  const BYOC_NAME = "create-ReuseBYOC";
  const RESOURCE_NAME = "movies";
  const PROJECT_NAME="Default Project"

  it("Login to Console", () => {
    console.login();
  });

  it("Search reuse component project", () => {
    project = console.searchProject(PROJECT_NAME);
  });

it("Navigate to existing BYOC component", () => {
  if (!project.isComponentExists(BYOC_NAME)) {
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
      project.visitComponent(BYOC_NAME);
      byoc = comp;
    });
    } else {
    project.visitComponent(BYOC_NAME);
    byoc = new Byoc (BYOC_NAME);
    }
    });
    
it("Build the Component", () => {
  byoc.build();
});

it("Redeploying to Dev", () => {
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

it("Verify test functionality using generated curl in Prod", () => {
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
