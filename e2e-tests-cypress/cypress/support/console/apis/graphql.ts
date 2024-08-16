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

import { ONE_HOUR } from "../../commons/constants";
import { AUTH_HEADER, AUTH_HEADER2, OK } from "../../commons/http";
import { Utils } from "../../commons/utils";
import { GitHub } from "../../github/github";
import { AbsComponent } from "../../interfaces/abs-component";
import { Component } from "../../interfaces/choreo-components/component";
import { Project } from "../../interfaces/choreo-components/projects";
import { PR } from "../../interfaces/pr";
import { APILifeCycleService } from "./api-life-cycle-service";
import { BallerinaService } from "./bal-service";
import { GraphQLQueryBuilder } from "./gql-query-builder";
import { GRAPHQL_URL } from "../../commons/urls";
import { login } from "../entities/login/login";

export const SUCCESS_STATUS_CODE = 200;
export const NO_CONTENT_STATUS_CODE = 204;

export interface ComponentDetails {
  id: string;
  projectId: string;
  handler: string;
}

export class GraphQL {
  static count = 0;

  static createComponentV2(
    projectName: string,
    repoName: string,
    componentData: AbsComponent,
    callback,
    createComponentCallback?: any
  ) {
    return this.getProjectsV2().then((p) => {     
      const project = p.projects.find((p) => p.name === projectName);

      if (project === undefined) {
        throw new Error(`Project ${projectName} not found`);
      }

      const projectId = project.id;

      componentData.handle = login.getOrgHandle();
      componentData.orgId = login.getOrgId();

      const query = callback(componentData, projectId);
      cy.wait(3000); // Wait before doing next call to prevent browser queueing or to wait for server to be free
      return this.callGraphQLV2(query).then((res) => {
        let id, projectId, handler;
        if (res.body.createComponent) {
          id = res.body.createComponent["id"];
          projectId = res.body.createComponent["projectId"];
          handler = res.body.createComponent["handler"];
        }
        if (res.body.createByocComponent) {
          id = res.body.createByocComponent["id"];
          projectId = res.body.createByocComponent["projectId"];
          handler = res.body.createByocComponent["handle"];
        }
        cy.log(`Component Id :: ${id}`);
        Cypress.env("component", { id, projectId, handler });
        if (componentData.initializeAsBallerinaProject) {
          this.getPullRequests(id, repoName);
        }
        if (createComponentCallback !== undefined) {
          createComponentCallback(query, res);
        }
        return Promise.resolve({ id, projectId, handler });
      });
    });
  }

  static deleteProjectsCreatedByTests(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjects().then((response) => {
      if (response.status > 205) {
        cy.log(`getProjects failed, status returned: ${response.status}`);
        return;
      }

      const projects = response.projects;

      const e2eProjects = projects.filter(
        ({ name }) =>
          name.includes(Utils.projectNamePrefix) ||
          name.includes(Utils.oldProjectNamePrefix) ||
          name.includes("test")
      );
      cy.log(`Total projects found : ${projects.length}`);
      cy.log(`E2E projects found : ${e2eProjects.length}`);

      e2eProjects.forEach((project) => {
        if (this.isProjectOld(project.name)) {
          this.deleteComponentsInProject(project.id, orgHandle, token);
          this.deleteProject(orgId, project.id);
        }
      });
    });
  }

  static deleteProjectsCreatedByTestsV2(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjectsV2().then((response) => {
      if (response.status > 205) {
        cy.log(`getProjects failed, status returned: ${response.status}`);
        return;
      }

      const projects = response.projects;

      const e2eProjects = projects.filter(
        ({ name }) =>
          name.includes(Utils.projectNamePrefix) ||
          name.includes(Utils.oldProjectNamePrefix)
      );
      cy.log(`Total projects found : ${projects.length}`);
      cy.log(`E2E projects found : ${e2eProjects.length}`);

      e2eProjects.forEach((project) => {
        if (this.isProjectOld(project.name)) {
          this.deleteComponentsInProjectV2(project.id, orgHandle, token);
          this.deleteProjectV2(orgId, project.id);
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
    return Date.now() - createdDate > ONE_HOUR;
  }
  static getComponents(projectId: string) {
    const { handle } = Cypress.env("userData");
    const query = {
      query: `query{ components(orgHandler: "${handle}", projectId: "${projectId}"){
        projectId, id, description, name, handler, displayName, displayType, version, createdAt, orgHandler,apiVersions {
            apiVersion,
            proxyName,
            proxyUrl,
            proxyId,
            id,
            state,
            latest,
            branch,
            accessibility
          } } }`,
    };

    return this.callGraphQL(query).then((res) => {
      if (res.status === SUCCESS_STATUS_CODE) {
        const components = res.body.components as Component[];
        const status = res.status;
        return Promise.resolve({ components, status });
      }
      return Promise.resolve({ components: [], status: -1 });
    });
  }

  static getComponentsV2(projectId: string, handle: string) {
    const query = {
      query: `query{ components(orgHandler: "${handle}", projectId: "${projectId}"){
        projectId, id, description, name, handler, displayName, displayType, version, createdAt, orgHandler,apiVersions {
            apiVersion,
            proxyName,
            proxyUrl,
            proxyId,
            id,
            state,
            latest,
            branch,
            accessibility
          } } }`,
    };

    return this.callGraphQLV2(query).then((res) => {
      if (res.status === SUCCESS_STATUS_CODE) {
        const components = res.body.components as Component[];
        const status = res.status;
        return Promise.resolve({ components, status });
      }
      return Promise.resolve({ components: [], status: -1 });
    });
  }

  static isProjectExists(projectName: string): Cypress.Chainable<boolean> {
    return this.getProjectsV2().then((response) => {
      let isFound = false;
      if (response.status === OK) {
        isFound = response.projects.find((p) => p.name === projectName)
          ? true
          : false;
      }
      return isFound;
    });
  }

  private static deleteComponentsInProject(
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    cy.log("deleteComponentsInProject()");
    this.getComponents(projectId).then((response) => {
      if (response.status === OK && response.components.length > 0) {
        response.components.forEach((component) => {
          const { handler } = component;
          BallerinaService.deleteConnectors(token);
          this.changeComponentLifeCycle(projectId, handler, token);
          this.deleteComponent(component.id, projectId, orgHandle);
        });
      } else {
        cy.log(`getComponents failed, status returned: ${response.status}`);
      }
    });
  }

  private static deleteComponentsInProjectV2(
    projectId: string,
    orgHandle: string,
    token: string
  ) {
    cy.log("deleteComponentsInProject()");
    this.getComponentsV2(projectId, orgHandle).then((response) => {
      if (response.status === OK && response.components.length > 0) {
        response.components.forEach((component) => {
          const { handler } = component;
          this.changeComponentLifeCycleV2(projectId, handler, token);
          this.deleteComponentV2(component.id, projectId, orgHandle);
        });
      } else {
        cy.log(`getComponents failed, status returned: ${response.status}`);
      }
    });
  }

  public static deleteComponent(
    componentId: string,
    projectId: string,
    orgHandle: string
  ) {
    const query = {
      query: `mutation{ deleteComponentV2(
        orgHandler: "${orgHandle}",
        projectId: "${projectId}",
        componentId: "${componentId}"){status, canDelete, message}}`,
    };

    this.callGraphQL(query).then((response) => {
      if (response.status === OK) {
        cy.log(`Successfully deleted Component  ${componentId}`);
      } else {
        cy.log(
          `Could not delete Component: ${componentId}, status returned: ${response.status}`
        );
      }
    });
  }

  public static deleteComponentV2(
    componentId: string,
    projectId: string,
    orgHandle: string
  ) {
    const query = {
      query: `mutation{ deleteComponentV2(
        orgHandler: "${orgHandle}",
        projectId: "${projectId}",
        componentId: "${componentId}"){status, canDelete, message}}`,
    };

    this.callGraphQLV2(query).then((response) => {
      if (response.status === OK) {
        cy.log(`Successfully deleted Component  ${componentId}`);
      } else {
        cy.log(
          `Could not delete Component: ${componentId}, status returned: ${response.status}`
        );
      }
    });
  }

  private static deleteProject(orgId: number, projectId: string) {
    const query = {
      query: `mutation{ deleteProject(
        orgId: ${orgId}, projectId: "${projectId}"){ status, details }}`,
    };

    this.callGraphQL(query).then((response) => {
      if (response.status === SUCCESS_STATUS_CODE) {
        cy.log(`Successfully deleted Project  ${projectId}`);
      } else {
        cy.log(
          `Could not delete Project: ${projectId}, status returned: ${response.status}`
        );
      }
    });
  }

  private static deleteProjectV2(orgId: number, projectId: string) {
    const query = {
      query: `mutation{ deleteProject(
        orgId: ${orgId}, projectId: "${projectId}"){ status, details }}`,
    };

    this.callGraphQLV2(query).then((response) => {
      if (response.status === SUCCESS_STATUS_CODE) {
        cy.log(`Successfully deleted Project  ${projectId}`);
      } else {
        cy.log(
          `Could not delete Project: ${projectId}, status returned: ${response.status}`
        );
      }
    });
  }

  static getProjects() {
    const { orgId } = Cypress.env("userData");
    const query = {
      query: `query{projects(orgId: ${orgId}){ id, orgId, name, version, createdDate,handler }}`,
    };
    return this.callGraphQL(query).then((res) => {
      const projects = res.body.projects as Project[];
      const status = res.status;
      return Promise.resolve({ projects, status });
    });
  }

  static getProjectsV2() {
    const query = {
      query: `query{projects(orgId: ${login.getOrgId()}){ id, orgId, name, version, createdDate,handler }}`,
    };
    return this.callGraphQLV2(query).then((res) => {
      const projects = res.body.projects as Project[];
      const status = res.status;
      return Promise.resolve({ projects, status });
    });
  }

  static callGraphQL(query: any) {
    const appSvcURL = Cypress.env("newAppSvcURL");
    return cy
      .request({
        method: "POST",
        url: `${appSvcURL}/projects/1.0.0/graphql`,
        body: JSON.stringify(query),
        headers: AUTH_HEADER(),
        failOnStatusCode: false,
      })
      .then((resp) => {
        if (resp.status > 205) {
          cy.log(query);
          cy.log(resp.body);
        }
        return Promise.resolve({
          body: resp.body.data,
          status: resp.status,
        });
      });
  }

  static callGraphQLV2(query: any) {
    return cy
      .request({
        method: "POST",
        url: GRAPHQL_URL,
        body: JSON.stringify(query),
        headers: AUTH_HEADER2(),
        failOnStatusCode: false,
      })
      .then((resp) => {
        if (resp.status > 205) {
          cy.log(query);
          cy.log(resp.body);
        }
        return Promise.resolve({
          body: resp.body.data,
          status: resp.status,
          headers: resp.headers,
        });
      });
  }

  static _getBuildsByVersion(componentId: string, latestAPIVersionId: string) {
    const { handle } = Cypress.env("userData");
    const query = GraphQLQueryBuilder.getBuildsByVersionQuery(
      handle,
      componentId,
      latestAPIVersionId
    );
    return this.callGraphQL(query).then((res) => {
      if (res.status === OK) {
        const builds = res.body.buildsByVersion as [];
        const { id, buildId, status } = builds[builds.length - 1];
        return Promise.resolve({ id, buildId, status });
      }
    });
  }

  private static _getPromotionStatus(url: string, count = 0) {
    if (count > 60) {
      return;
    }
    Utils.sendGetRequest(url, AUTH_HEADER()).then((res) => {
      const { deploymentStatus } = res.body;
      Utils.isError(
        deploymentStatus,
        "Proxy With Mediation Policy Deployment Failed"
      );
      if (deploymentStatus !== "ACTIVE") {
        cy.wait(15000);
        count++;
        this._getPromotionStatus(url, count);
      }
      return;
    });
  }

  public static getPullRequests(
    componentId: string,
    repoName: string,
    count = 0
  ) {
    const query = {
      query: `query{
                 componentPullRequests(componentId: "${componentId}")
                 { url, number }
                 }`,
    };
    cy.wait(10000);
    this.callGraphQL(query).then((res) => {
      const prs: PR[] = res.body.componentPullRequests as [];
      if (prs.length > 0) {
        const { number } = prs[0];
        GitHub.mergePR(repoName, number);
        return;
      } else {
        if (count < 10) {
          count++;
          this.getPullRequests(componentId, repoName, count);
        }
      }
    });
  }

  private static changeComponentLifeCycle(
    projectId: string,
    componentHandler,
    token: string
  ) {
    cy.log(`changeComponentLifeCycle ==> Project Id ${projectId}`);
    const query = GraphQLQueryBuilder.getLifeCycleChangeQuery(
      projectId,
      componentHandler
    );

    this.callGraphQL(query).then((res) => {
      if (res.status === SUCCESS_STATUS_CODE) {
        const apiVersion: [] = res.body.component.apiVersions;
        apiVersion.forEach((e) => {
          const { proxyId } = e;
          if (proxyId) {
            this.deprecateComponent(proxyId, token);
          } else {
            cy.log(`ProxyID is :: ${proxyId}`);
          }
        });
      } else {
        cy.log(`Status Code For changeComponentLifeCycle ==> ${res.status}`);
      }
    });
  }

  private static changeComponentLifeCycleV2(
    projectId: string,
    componentHandler,
    token: string
  ) {
    cy.log(`changeComponentLifeCycle ==> Project Id ${projectId}`);
    const query = GraphQLQueryBuilder.getLifeCycleChangeQuery(
      projectId,
      componentHandler
    );

    this.callGraphQLV2(query).then((res) => {
      if (res.status === SUCCESS_STATUS_CODE) {
        const apiVersion: [] = res.body.component.apiVersions;
        apiVersion.forEach((e) => {
          const { proxyId } = e;
          if (proxyId) {
            this.deprecateComponentV2(proxyId, token);
          } else {
            cy.log(`ProxyID is :: ${proxyId}`);
          }
        });
      } else {
        cy.log(`Status Code For changeComponentLifeCycle ==> ${res.status}`);
      }
    });
  }

  private static deprecateComponent(apiId: string, token: string) {
    const { uuid } = Cypress.env("userData");
    cy.log(`Current UUID ==> ${uuid}`);
    const statusRequest = `${Cypress.env(
      "apimSvcURL"
    )}/api/am/publisher/v2/apis/${apiId}/lifecycle-state?organizationId=${uuid}`;
    const headers = { Authorization: `Bearer ${token}` };
    return Utils.sendGetRequest(statusRequest, headers).then((res) => {
      const { state } = res.body;
      if (state === "Published") {
        this.sendDeprecateRetireRequest(apiId);
      }
    });
  }

  private static deprecateComponentV2(apiId: string, token: string) {
    const uuid = login.getOrgUuid();
    cy.log(`Current UUID ==> ${uuid}`);
    const statusRequest = `${Cypress.env(
      "apimSvcURL"
    )}/api/am/publisher/v2/apis/${apiId}/lifecycle-state?organizationId=${uuid}`;
    const headers = { Authorization: `Bearer ${token}` };
    return Utils.sendGetRequest(statusRequest, headers).then((res) => {
      const { state } = res.body;
      if (state === "Published") {
        this.sendDeprecateRetireRequestV2(apiId);
      }
    });
  }

  private static sendDeprecateRetireRequest(apiId: string) {
    APILifeCycleService.deprecateAPI(apiId);
    APILifeCycleService.retireAPI(apiId);
  }

  private static sendDeprecateRetireRequestV2(apiId: string) {
    APILifeCycleService.deprecateAPIV2(apiId);
    APILifeCycleService.retireAPIV2(apiId);
  }
}
