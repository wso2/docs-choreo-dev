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

import { ONE_HOUR } from "../constants";
import { Utils } from "../utils";

export const SUCCESS_STATUS_CODE = 200;
export const CREATED_STATUS_CODE = 201;
export const NO_CONTENT_STATUS_CODE = 204;
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
      if (response.status !== SUCCESS_STATUS_CODE) {
        cy.log(`getProjects failed, status returned: ${response.status}`);
        return;
      }

      const projects = response.body.data.projects as {
        id: string;
        name: string;
      }[];

      const e2eProjects = projects.filter(
        ({ name }) =>
          name.includes(Utils.projectNamePrefix) ||
          name.includes(Utils.oldProjectNamePrefix)
      );
      cy.log(`Total projects found : ${projects.length}`);
      cy.log(`E2E projects found : ${e2eProjects.length}`);

      e2eProjects.forEach((project) => {
        if (this.isProjectOld(project.name)) {
          this.deleteComponentsInProject(project.id, orgHandle, token);
          this.deleteProject(orgId, project.id, token);
        }
      });
    });
  }

  private static isProjectOld(projectName: string) {
    // Previous project name format signifies old projects
    if (projectName.includes(Utils.oldProjectNamePrefix)) {
      return true;
    }

    // Extract date section of project name for comparison
    const createdDate = Number(projectName.split(Utils.projectNamePrefix)[1]);

    // Only delete projects(and their components) that are older than 1 hour
    if (Date.now() - createdDate > ONE_HOUR) {
      return true;
    }

    return false;
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
      if (response.status === SUCCESS_STATUS_CODE) {
        response.body.data.components.forEach((component) => {
          const { handler } = component;
          this.deleteConnectors(token);
          this.changeComponentLifeCycle(projectId, handler, token);
          this.deleteComponent(component.id, projectId, orgHandle, token);
        });
      } else {
        cy.log(`getComponents failed, status returned: ${response.status}`);
      }
    });
  }

  public static deleteComponent(
    componentId: string,
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    const query = {
      query: `mutation{ deleteComponentV2(
        orgHandler: "${orgHandle}",
        projectId: "${projectId}",
        componentId: "${componentId}"){status, canDelete, message}}`,
    };

    this.callGraphQL(token, query).then((response) => {
      if (response.status === SUCCESS_STATUS_CODE) {
        cy.log(`Successfully deleted Component  ${componentId}`);
      } else {
        cy.log(
          `Could not delete Component: ${componentId}, status returned: ${response.status}`
        );
      }
    });
  }

  private static deleteProject(
    orgId: number,
    projectId: string,
    token: string
  ) {
    const query = {
      query: `mutation{ deleteProject(
        orgId: ${orgId}, projectId: "${projectId}"){ status, details }}`,
    };

    this.callGraphQL(token, query).then((response) => {
      if (response.status === SUCCESS_STATUS_CODE) {
        cy.log(`Successfully deleted Project  ${projectId}`);
      } else {
        cy.log(
          `Could not delete Project: ${projectId}, status returned: ${response.status}`
        );
      }
    });
  }

  static getProjects(orgId: number, token: string) {
    const query = {
      query: `query{projects(orgId: ${orgId}){ id, orgId, name, version, createdDate,handler }}`,
    };

    return this.callGraphQL(token, query);
  }

  private static callGraphQL(token: string, query: any) {
    const appSvcURL = Cypress.env("newAppSvcURL");
    const header = {
      Authorization: `Bearer ${token}`,
      "content-type": "application/json",
    };
    return cy.request({
      method: "POST",
      url: `${appSvcURL}/projects/1.0.0/graphql`,
      body: JSON.stringify(query),
      headers: header,
      failOnStatusCode: false,
    });
  }

  private static changeComponentLifeCycle(
    projectId: string,
    componentHandler,
    token: string
  ) {
    cy.log(`changeComponentLifeCycle ==> Project Id ${projectId}`);
    const query = {
      query: `query{    component(      projectId: "${projectId}"      componentHandler: "${componentHandler}"    )
{      id,     
 name,      
 handler,      
 description,      
 displayType,      
 displayName,      
 ownerName,      
 orgId,      
 orgHandler,      
 version,      
 labels,      
 createdAt,      
 updatedAt,      
 projectId,      
 apiId,      
 repository{        
 nameApp,        
 nameConfig,        
 branch,        
 branchApp,        
 organizationApp,        
 organizationConfig,        
 isUserManage      },      
 apiVersions{       
 apiVersion,        
 proxyName,        
 proxyUrl,        
 proxyId,        
 id,        
 state,        
 latest,       
 branch,        
 appEnvVersions{         
 environmentId,          
 releaseId,          
 release{ id, metadata{choreoEnv},environmentId,environment,gitHash,gitOpsHash,}}}}}`,
    };

    const headers = {
      Authorization: `Bearer ${token}`,
    };

    this.callGraphQL(token, query).then((res) => {
      const apiVersion: [] = res.body.data.component.apiVersions;
      apiVersion.forEach((e) => {
        const { proxyId } = e;
        if (proxyId) {
          this.deprecateComponent(proxyId, token);
        } else {
          cy.log(`ProxyID is :: ${proxyId}`);
        }
      });
    });
  }

  private static deprecateComponent(apiId: string, token: string) {
    const { uuid } = Cypress.env("userData");
    const statusRequest = `${Cypress.env(
      "apimSvcURL"
    )}/api/am/publisher/v2/apis/${apiId}/lifecycle-state?organizationId=${uuid}`;
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    return Utils.sendGetRequest(statusRequest, headers).then((res) => {
      const { state } = res.body;
      if (state === "Published") {
        this.sendDeprecateRetireRequest(apiId, uuid, token);
      }
    });
  }

  private static sendDeprecateRetireRequest(
    apiId: string,
    uuid: string,
    token: string
  ) {
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    const deprecateRequest = `${Cypress.env(
      "apimSvcURL"
    )}/api/am/publisher/v2/apis/change-lifecycle?organizationId=${uuid}&apiId=${apiId}&action=Deprecate`;
    const retireRequest = `${Cypress.env(
      "apimSvcURL"
    )}/api/am/publisher/v2/apis/change-lifecycle?organizationId=${uuid}&apiId=${apiId}&action=Retire`;
    Utils.sendPostRequest(deprecateRequest, headers, {});
    Utils.sendPostRequest(retireRequest, headers, {});
  }

  private static deleteConnector(pkg: any, token) {
    const { organization, name, version } = pkg;
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    const url = `${Cypress.env("balRegistryURL")}/packages/${organization}/${name}/${version}?force=true`;
    Utils.sendDeleteRequest(url, headers).then((res) => {
      if (res.status === NO_CONTENT_STATUS_CODE) {
        cy.log(`Successfully deleted Connector  ${name}`);
      } else {
        cy.log(
          `Could not delete connector: ${name}, status returned: ${res.status}`
        );
      }
    });
  }

  private static deleteConnectors(token: string) {
    const { handle } = Cypress.env("userData");
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    const url = `${Cypress.env("balRegistryURL")}/packages/${handle}`;
    Utils.sendGetRequest(url, headers).then((res) => {
      const packages = res.body as [];
      if (packages.length > 0) {
        packages.forEach((p) => this.deleteConnector(p, token));
      }
    });
  }
}
