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

import { ACTIVE, ERROR, ONE_HOUR } from "../../commons/constants";
import { cyLog } from "../../commons/cy";
import { AUTH_HEADER, AUTH_HEADER2, OK } from "../../commons/http";
import { Utils } from "../../commons/utils";
import { GitHub } from "../../github/github";
import { AbsComponent } from "../../interfaces/abs-component";
import { APIVersion } from "../../interfaces/choreo-components/api-version";
import { AppEnvVersion } from "../../interfaces/choreo-components/app-env-version";
import { Component } from "../../interfaces/choreo-components/component";
import { Project } from "../../interfaces/choreo-components/projects";
import { IntegrationComponentData } from "../../interfaces/integration-component-data";
import { PR } from "../../interfaces/pr";
import { ChoreoHomePage } from "../pages/home/home-page";
import { APILifeCycleService } from "./api-life-cycle-service";
import { BallerinaService } from "./bal-service";
import { GraphQLQueryBuilder } from "./gql-query-builder";
import { Enums } from "../../commons/enums";
import { PROXY_DEPLOYER_EP, PUBLISHER_URL } from "../../commons/urls";
import { ProjectEnvironment } from "../../interfaces/choreo-components/project-environments";
import { VERY_SHORT_TIME } from "../../commons/timeouts";
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

  static createComponent(
    projectName: string,
    repoName: string,
    componentData: AbsComponent,
    callback: any
  ) {
    const { orgId, handle } = Cypress.env("userData");
    this.getProjects().then((p) => {
      const projectId = p.projects.find((p) => p.name === projectName).id;

      componentData.handle = handle;
      componentData.orgId = orgId;

      const query = callback(componentData, projectId);
      this.callGraphQL(query).then((res) => {
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
      });
    });
    return cy.wrap({});
  }

  static createComponentV2(
    projectName: string,
    repoName: string,
    componentData: AbsComponent,
    callback,
    createComponentCallback?: any
  ) {
    return this.getProjectsV2(login.getOrgId()).then((p) => {
      const projectId = p.projects.find((p) => p.name === projectName).id;

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
        createComponentCallback(res.body.request, res.body.response);
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
    this.getProjectsV2(orgId).then((response) => {
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

  static getComponentByName(projectId: string, componentName: string) {
    return this.getComponents(projectId).then((response) => {
      if (response.status == OK) {
        const component: Component = response.components.find(
          (c) => c.displayName === componentName
        );
        return Promise.resolve(component);
      }
    });
  }

  static _getComponentByName(projectName: string, componentName: string) {
    return this.getProjects().then((res) => {
      const projects = res.projects;
      const project = projects.find((p) => p.name === projectName);
      return this.getComponents(project.id).then((resp) => {
        if (resp.status === SUCCESS_STATUS_CODE) {
          const comp: Component = resp.components.find(
            (c) => c.displayName === componentName
          );
          return Promise.resolve(comp);
        }
        return Promise.reject(new Error("Error Component Fetching Failed !!"));
      });
    });
  }

  static getProjectByName(projectName: string) {
    return this.getProjects().then((response) => {
      if (response.status === OK) {
        const project: Project = response.projects.find(
          (p) => p.name === projectName
        );

        return Promise.resolve(project);
      }

      return Promise.resolve(null);
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
          BallerinaService.deleteConnectorsV2(token, orgHandle);
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

  static getProjectsV2(orgId: number) {
    const query = {
      query: `query{projects(orgId: ${orgId}){ id, orgId, name, version, createdDate,handler }}`,
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
    const appSvcURL = Cypress.env("newAppSvcURL");
    return cy
      .request({
        method: "POST",
        url: `${appSvcURL}/projects/1.0.0/graphql`,
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
        });
      });
  }

  static createIntegrationComponent(componentData: IntegrationComponentData) {
    const { orgId } = Cypress.env("userData");
    this.getProjects().then((res) => {
      const projects = res.projects;
      const project = projects.find(
        (p) => p.name === componentData.projectName
      );
      cy.log(`Project Id :: ${project["id"]}`);
      const query = {
        query: `mutation{
                        createIntegrationComponent(
                                 component: {
                                      name: "${componentData.componentName}",
                                      displayName: "${
                                        componentData.componentName
                                      }",
                                      description: "",
                                      orgId: ${orgId},
                                      orgHandler: "${Cypress.env(
                                        "choreoOrgHandle"
                                      )}",
                                      projectId: "${project["id"]}",
                                      labels: "",
                                      componentType: "${
                                        componentData.componentType
                                      }",
                                      accessibility: "${
                                        componentData.accessibility
                                      }",
                                      srcGitRepoUrl: "${
                                        componentData.srcGitRepoUrl
                                      }",
                                      srcGitRepoBranch: "${
                                        componentData.srcGitRepoBranch
                                      }",
                                      repositorySubPath: "${
                                        componentData.repositorySubPath
                                      }",
                                      oasFilePath: "${
                                        componentData.oasFilePath
                                      }"
                                      version: "1.0.0"
                                    } )
                                    { id,
                                      handle,
                                      organizationId,
                                      projectId,

                                    }
                          }`,
      };
      this.callGraphQL(query).then((res) => {
        const { id, projectId, handle } = res.body.createIntegrationComponent;
        Cypress.env("component", { id, projectId, handle });
        expect(res.status).to.be.eq(200);
      });
    });
    ChoreoHomePage.navigateToComponents();
    cy.get("tbody>tr p").should("be.visible");
    return cy.wrap({});
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
  static getDeployStatus(
    projectName: string,
    componentName: string,
    stage: Enums.DeploymentStages,
    status: Enums.ResponseStatus
  ) {
    const upperTime = Date.now() + 360000;
    this._getAPIInfo(projectName, componentName).then((comp) => {
      const { componentId, latestVersionId } = comp;
      this._getBuildsByVersion(componentId, latestVersionId).then((bv) => {
        const { buildId } = bv;
        const url = `${PROXY_DEPLOYER_EP}/${componentId}/versions/${latestVersionId}/builds/${buildId}/status`;
        this._getDeployStatus(url, stage, status, upperTime);
      });
    });
  }

  private static _getDeployStatus(
    url: string,
    stage: string,
    status: string,
    upperTime: number
  ) {
    Utils.sendGetRequest(url, AUTH_HEADER()).then((res) => {
      if (res.status === OK) {
        const stageInfo = res.body.stageInfo as {
          stage: string;
          status: string;
        }[];

        const deploymentStage = stageInfo.find((s) => s.stage === stage);

        cyLog(`Time diff ${upperTime - Date.now()}`);
        cyLog(deploymentStage);
        if (Date.now() < upperTime) {
          if (deploymentStage) {
            Utils.isError(
              deploymentStage.status,
              `Proxy With Mediation Policy Deployment Failed At ${deploymentStage.stage}`
            );
            if (deploymentStage.status === status) {
              return;
            } else {
              cy.wait(10000);
              this._getDeployStatus(url, stage, status, upperTime);
            }
          } else {
            cy.wait(10000);
            this._getDeployStatus(url, stage, status, upperTime);
          }
        } else {
          cyLog(`Upper time exceeded with status ${deploymentStage}`);
        }
      }
    });
  }

  static _getAPIInfo(projectName: string, componentName: string) {
    return this._getComponentInfo(projectName, componentName).then((comp) => {
      const { id } = comp.component;
      const latestVersion = comp.component.apiVersions.find((a) => a.latest);
      return Promise.resolve({
        componentId: id,
        latestVersionId: latestVersion.id,
      });
    });
  }

  static getPrmotionStatus(projectName: string, componentName: string) {
    this._getProjectEnvironments(projectName).then((projEnvs) => {
      const envs = projEnvs as { name: string; id: string }[];
      const { id } = envs.find((e) => e.name === Enums.Environment.PRODUCTION);
      this._getAPIInfo(projectName, componentName).then((comp) => {
        const { componentId, latestVersionId } = comp;
        const url = `${PROXY_DEPLOYER_EP}/${componentId}/versions/${latestVersionId}/deployments?environmentId=${id}&accessMode=external`;
        this._getPromotionStatus(url);
      });
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

  static _getProjectEnvironments(projectName: string) {
    const { uuid } = Cypress.env("userData");
    return this.getProjectByName(projectName).then((prj) => {
      const { id } = prj;
      const query = GraphQLQueryBuilder.getEnvironments(uuid, id);
      return this.callGraphQL(query).then((res) => {
        const projEnv: ProjectEnvironment[] = res.body.environments;
        return Promise.resolve(projEnv);
      });
    });
  }

  static _getComponentInfo(projectName: string, componentName: string) {
    return this.getProjects().then((res) => {
      const projects = res.projects;
      const project = projects.find((p) => p.name === projectName);

      return this.getComponents(project.id).then((resp) => {
        if (resp.status === SUCCESS_STATUS_CODE) {
          cyLog(resp);
          cyLog(componentName);
          const comp: Component = resp.components.find(
            (c) => c.displayName === componentName
          );
          const query = GraphQLQueryBuilder.getComponentDetails(
            project.id,
            comp.handler
          );
          return this.callGraphQL(query).then((res) => {
            const component: Component = res.body.component;
            return Promise.resolve({
              component,
            });
          });
        }
      });
    });
  }

  static _getComponentDeploymentStatus(
    projectName: string = "",
    componentName: string = "",
    count: number = 0
  ) {
    const { handle, uuid } = Cypress.env("userData");
    count++;
    this._getProjectEnvironments(projectName).then((projEnvs) => {
      const envs = projEnvs as { name: string; id: string }[];
      const { id } = envs.find((e) => e.name === Enums.Environment.DEVELOPMENT);
      this._getAPIInfo(projectName, componentName).then((info) => {
        const { componentId, latestVersionId } = info;
        const query = GraphQLQueryBuilder.getComponentDeploymentStatusQuery(
          handle,
          uuid,
          componentId,
          latestVersionId,
          id
        );
        this.callGraphQL(query).then((res) => {
          const { deploymentStatus, deploymentStatusV2 } =
            res.body.componentDeployment;
          if (deploymentStatusV2 === ERROR || deploymentStatus === ERROR) {
            throw new Error(" Deployment Failed");
          }

          if (deploymentStatusV2 === ACTIVE && deploymentStatus === ACTIVE) {
            return;
          } else {
            if (count < 10) {
              cy.wait(VERY_SHORT_TIME.timeout);
              this._getComponentDeploymentStatus(
                projectName,
                componentName,
                count
              );
            }
          }
        });
      });
    });
  }

  static getServiceEndpointStatus(
    projectName: string,
    componentName: string,
    env: Enums.Environment = Enums.Environment.DEVELOPMENT
  ) {
    this._getProjectEnvironments(projectName).then((projEnvs) => {
      const projEnv = projEnvs.find((p) => p.name === env);

      this._getComponentInfo(projectName, componentName).then((com) => {
        const apiVersion = com.component.apiVersions.find(
          (v) => v.latest === true
        );

        const appEnv = apiVersion.appEnvVersions.find(
          (av) => av.environmentId === projEnv.id
        );

        const query = GraphQLQueryBuilder.getEndpointStatusQuery(
          com.component.id,
          apiVersion.id,
          appEnv.releaseId
        );

        this._getServiceEndpointStatus(query);
      });
    });
  }

  private static _getServiceEndpointStatus(query, count = 0) {
    this.callGraphQL(query).then((res) => {
      const { state } = res.body.componentEndpoints[0];
      Utils.isError(state, "Deployment Endpoint status is ERROR");
      if (state === "Active") {
        return;
      } else {
        if (this.count < 20) {
          cy.wait(VERY_SHORT_TIME.timeout);
          count++;
          this._getServiceEndpointStatus(query, count);
        }
      }
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

  static _getProxyDeployment(
    projectName: string,
    componentName: string,
    environment: Enums.Environment
  ) {
    const { handle, uuid } = Cypress.env("userData");
    return GraphQL._getProjectEnvironments(projectName).then((env) => {
      const { id } = env.find((e) => e.name === environment);
      return GraphQL._getAPIInfo(projectName, componentName).then((comp) => {
        const { componentId, latestVersionId } = comp;

        const query = GraphQLQueryBuilder.getPrxoyDeployments(
          handle,
          uuid,
          componentId,
          latestVersionId,
          id
        );

        return this.callGraphQL(query).then((res) => {
          const { invokeUrl, apiId } = res.body.proxyDeployment;

          return Promise.resolve({ invokeUrl, apiId, uuid });
        });
      });
    });
  }

  static _getAuthHeaderKey(
    projectName: string,
    componentName: string,
    environment: Enums.Environment
  ) {
    return this._getProxyDeployment(
      projectName,
      componentName,
      environment
    ).then((proxy) => {
      const { invokeUrl, apiId, uuid } = proxy;
      const url = `${PUBLISHER_URL}/apis/${apiId}/generate-key?organizationId=${uuid}&keyType=${environment}`;

      return Utils.sendPostRequest(url, AUTH_HEADER(), "").then((res) => {
        const { apikey } = res.body;

        return Promise.resolve({ invokeUrl, apikey });
      });
    });
  }
}
