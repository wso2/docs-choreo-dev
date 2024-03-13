/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { Enums } from "../../../../support/commons/enums";
import { Service } from "../../../../support/console/entities/component/service-component";
import { Project } from "../../../../support/console/entities/project/project";
import { console } from "../../../../support/console/console";
import { OK } from "../../../../support/commons/http";

after(() => {
  console.logout();
});

describe("Verify Ballerina service functionality", () => {
  let project: Project;
  let component: Service;
  const COMPONENT_NAME = "create-ReuseService-1.13.1";
  const PROJECT_NAME = "Default Project";
  const ENDPOINT_NAME = "Readinglist";

  it("Login to Console", () => {
    console.login();
  });

  it("Search reuse component project", () => {
    project = console.searchProject(PROJECT_NAME);
  });

  it("Verify Reuse Ballerina service component creation", () => {
    project.isComponentExists(COMPONENT_NAME).then((isExists) => {
      if (!isExists) {
        project
          .createServiceComponent(
            Enums.Accessibility.EXTERNAL,
            {
              url: "https://github.com/choreo-test-apps/byor-service-app1",
              branch: "main",
            },
            ENDPOINT_NAME,
            COMPONENT_NAME
          )
          .then((serviceComponent: Service) => {
            project.visitComponent(COMPONENT_NAME);
            component = serviceComponent;
          });
      } else {
        project.visitComponent(COMPONENT_NAME);
        component = new Service(COMPONENT_NAME, ENDPOINT_NAME);
      }
    });
  });

  it("Build the component", () => {
    component.build();
  });

  it("Deploying the component with Public level visibility", () => {
    component.deployPublicLevelAccessibility();
  });

  it("Testing the component in Dev", () => {
    component
      .testConsole({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).equal(OK.toString());
      });
  });

  it("Verifying component promotion to Prod", () => {
    component.promotePublicLevelAccessibility();
  });

  it("Testing the component in Prod", () => {
    component
      .testConsole({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        resourcePath: "books",
        method: "get",
        parentComponentId: "operations-default-getBooks",
      })
      .then((res) => {
        cy.fixture("books").then((books) => {
          expect(res.response.toString()).to.include(books[1].title);
        });
        expect(res.statusCode).equal(OK.toString());
      });
  });

  it("Stop component", () => {
    component.stopDeployment();
    component.stopPromotion();
  });
});
