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

import { console } from "../../../support/console/console";
import { Enums } from "../../../support/commons/enums";
import { GitHub } from "../../../support/github/github";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";

describe("Graphql GQL service test", () => {
  const PROJECT_DESCRIPTION = "sample gql service";
  const TEST_QUERY = `query MyQuery {
                        greeting(name: "John")
                      }`;
  const TEST_QUERY_RESPONSE = 'greeting": "Hello, John';
  const REPO_NAME = "choreo-samples";
  const ENDPOINT_NAME = "Greeting GraphQL";
  const subPath = "graphql-service";

  let project: Project;
  let component: Service;

  after(() => {
    console.logout();
  });

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify GraphQL sample creation", () => {
    GitHub.syncForkWithUpstream(REPO_NAME, "main");

    project
      .createServiceComponent(
        Enums.Accessibility.EXTERNAL,
        {
          url: "https://github.com/choreo-test-apps/choreo-samples",
          branch: "main",
          subPath: subPath,
        },
        ENDPOINT_NAME
      )
      .then((serviceComponent: Service) => {
        project.visitComponent(serviceComponent.getName());
        component = serviceComponent;
      });
  });

  it("Build the sample", () => {
    component.build();
  });

  it("Deploy sample", () => {
    component.deployPublicLevelAccessibility(false);
  });

  it("Verify test functionality of GQL query in dev on swagger", () => {
    component
      .testGQL({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        query: TEST_QUERY,
      })
      .then((res) => {
        expect(res.text()).to.be.contains(TEST_QUERY_RESPONSE);
      });
  });

  it("Verify suspending deployed component", () => {
    component.stopDeployment();
  });
});
