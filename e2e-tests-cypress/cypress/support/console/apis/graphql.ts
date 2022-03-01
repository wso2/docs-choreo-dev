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

import { Utils } from "../utils";

export const SUCCESS_STATUS_CODE = 200;
export const CREATED_STATUS_CODE = 201;

export class GraphQL {

  static createDefaultProjectIfNotExists(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjects(orgId, token).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      // const projects = response.body.data.projects as [];
      // if (projects === undefined || !projects.length) {
      //   this.createDefaultProject(orgId, orgHandle, token);
      // }
    });
  }

  static deleteProjectsCreatedByTests(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjects(orgId, token).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      const projects = response.body.data.projects as {
        id: string;
        name: string;
      }[];
      const e2eProjects = projects.filter(({ name }) =>
        name.includes(Utils.projectNamePrefix)
      );

      cy.log(`Total projects found : ${projects.length}`);
      cy.log(`E2E projects found : ${e2eProjects.length}`);

      e2eProjects.forEach((project) => {
        this.deleteComponentsInProject(project.id, orgHandle, token);
        this.deleteProject(orgId, project.id, token);
      });
    });
  }

  private static createDefaultProject(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    const query = {
      query: `mutation {
            createProjectComponent(
              project: {
                name: "Dummy e2e Project",
                orgId: ${orgId},
                orgHandler: "${orgHandle}",
                description: "",
                version: "1.0.0"
              },
              component: {
                name: "dummye2e",
                orgId: ${orgId},
                orgHandler: "${orgHandle}",
                displayName: "DummyE2E",
                displayType: "restAPI",
                projectId: "",
                labels: "",
                version: "1.0.0",
                description: "",
                apiId: "",
                ballerinaVersion: "swan-lake-alpha5"
              }
            ) {id, projectId, apiId, handler }}`,
    };

    this.callGraphQL(token, query).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      cy.log("Successfully created Default e2e Project");
    });
  }

  private static getComponents(
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    const query = {
      query: `query{ components(orgHandler: "${orgHandle}", projectId: "${projectId}"){
      projectId, id, name, handler, displayName, displayType, version } }`,
    };

    return this.callGraphQL(token, query);
  }

  private static deleteComponentsInProject(
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    cy.log("deleteComponentsInProject()");
    this.getComponents(projectId, orgHandle, token).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      response.body.data.components.forEach((component) => {
        this.deleteComponent(component.id, projectId, orgHandle, token);
      });
    });
  }

  public static deleteComponent(
    componentId: string,
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    const query = {
      query: `mutation{ deleteComponent(
        orgHandler: "${orgHandle}",
        projectId: "${projectId}",
        componentId: "${componentId}"){ id }}`,
    };

    this.callGraphQL(token, query).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      cy.log(`Successfully deleted Component  ${componentId}`);
    });
  }

  private static deleteProject(
    orgId: number,
    projectId: string,
    token: string
  ) {
    const query = {
      query: `mutation{ deleteProject(
        orgId: ${orgId}, projectId: "${projectId}"){ id }}`,
    };

    this.callGraphQL(token, query).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
      cy.log(`Successfully deleted Project  ${projectId}`);
    });
  }

  static getProjects(orgId: number, token: string) {
    const query = {
      query: `query{projects(orgId: ${orgId}){ id, orgId, name, version, createdDate,handler }}`,
    };

    return this.callGraphQL(token, query);
  }

  private static callGraphQL(token: string, query: any) {
    const appSvcURL = Cypress.env("appSvcURL");
    const header = {
      Authorization: `Bearer ${token}`,
      "content-type": "application/json",
    };
    return cy.request({
      method: "POST",
      url: `${appSvcURL}/graphql`,
      body: JSON.stringify(query),
      headers: header,
    });
  }
}
