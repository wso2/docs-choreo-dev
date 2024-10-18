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

import { console } from "../../../support/console/console";
import { BuildPacks, Enums } from "../../../support/commons/enums";
import { Project } from "../../../support/console/entities/project/project";
import { Service } from "../../../support/console/entities/component/service-component";

describe(`Graphql GQL service functionality`, () => {
  const PROJECT_DESCRIPTION = "ballerina service with graphql endpoint";
  const TEST_QUERY = '{greeting(name:"John")}';
  const TEST_QUERY_RESPONSE = 'greeting": "Hello, John';
  const ENDPOINT_NAME = "Greeting GraphQL";

  let project: Project;
  let component: Service;
  const REPO_URL = "https://github.com/wso2/choreo-samples";
  const REPO_NAME = "graphql-service";

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a GraphQL service from choreo samples", () => {
    project
      .createGQLServiceComponentUI({
        displayName: "",
        repoUrl: REPO_URL,
        buildPack: BuildPacks.Ballerina,
        repoName: REPO_NAME,
        repoTestid: REPO_NAME,
        ENDPOINT_NAME,
      })
      .then((comp) => {
        component = comp;
      });
  });

  it("Build the component", () => {
    component.build();
  });

  it("Deploying the component with Public level visibility", () => {
    component.deployPublicLevelAccessibility(false);
  });

  it("Verify component promote to prod", () => {
    component.promotePublicLevelAccessibility(undefined, false);
  });

  it("Verify test functionality of GQL query in dev on swagger", () => {
    component
      .testGQL({
        env: Enums.Environment.DEVELOPMENT,
        endpoint: ENDPOINT_NAME,
        query: TEST_QUERY,
      })
      .then((res) => {
        expect(res.toString()).to.be.contains(TEST_QUERY_RESPONSE);
      });
  });

  it("Verify test functionality of GQL query in Prod on swagger", () => {
    component
      .testGQL({
        env: Enums.Environment.PRODUCTION,
        endpoint: ENDPOINT_NAME,
        query: TEST_QUERY,
      })
      .then((res) => {
        expect(res.toString()).to.be.contains(TEST_QUERY_RESPONSE);
      });
  });

  it("Verify suspending Prod deployed component", () => {
    component.stopDeployment();
    component.stopPromotion();
  });
});
