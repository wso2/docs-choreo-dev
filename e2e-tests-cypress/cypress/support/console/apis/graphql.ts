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


import {
  ACTIVE,
  ERROR,
  ONE_HOUR,
} from "../../commons/constants";
import { cyLog } from "../../commons/cy";
import { AUTH_HEADER, OK } from "../../commons/http";
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
import { PROXY_DEPLOYER_EP } from "../../commons/urls";

export const SUCCESS_STATUS_CODE = 200;
export const NO_CONTENT_STATUS_CODE = 204;

export class GraphQL {
  static count = 0;

  static createComponent(
    projectName: string,
    repoName: string,
    componentData: AbsComponent,
    callback
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
        Cypress.env("component", { id, projectId, handler });
        if (componentData.initializeAsBallerinaProject) {
          this.getPullRequests(id, repoName);
        }
        this.getDeployedComponentDetails(projectId, handler);
      });
    });
    return cy.wrap({});
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
  static getComponentByName(projectId: string, componentName: string) {
    return this.getComponents(projectId).then((response) => {
      if (response.status == OK) {
        const component: Component = response.components.find((c) => c.displayName === componentName);
        return Promise.resolve(component);
      }
    })
  }

  static _getComponentByName(projectName: string, componentName: string) {
    this.getProjects().then((res) => {
      const projects = res.projects;
      const project = projects.find((p) => p.name === projectName);
      this.getComponents(project.id).then((resp) => {
        if (resp.status === SUCCESS_STATUS_CODE) {
          const comp: Component = resp.components.find((c) => c.displayName === componentName);
          if (!comp) {
            return Promise.reject(new Error(`${componentName} is not in ${projectName}`))
          }
          return Promise.resolve(comp)
        }
        return Promise.reject(new Error("Error Component Fetching Failed !!"))
      });
    });

  }

  static getProjectByName(projectName: string) {
    return this.getProjects().then((response) => {
      if (response.status == OK) {
        const project: Project = response.projects.find((p) => p.name === projectName);
        return Promise.resolve(project);
      }

      return Promise.resolve(null);
    })
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
                                      displayName: "${componentData.componentName
          }",
                                      description: "",
                                      orgId: ${orgId},
                                      orgHandler: "${Cypress.env(
            "choreoOrgHandle"
          )}",
                                      projectId: "${project["id"]}",
                                      labels: "",
                                      componentType: "${componentData.componentType
          }",
                                      accessibility: "${componentData.accessibility
          }",
                                      srcGitRepoUrl: "${componentData.srcGitRepoUrl
          }",
                                      srcGitRepoBranch: "${componentData.srcGitRepoBranch
          }",
                                      repositorySubPath: "${componentData.repositorySubPath
          }",
                                      oasFilePath: "${componentData.oasFilePath
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
        this.getDeployedComponentDetails(projectId, handle);
      });
    });
    ChoreoHomePage.navigateToMarketPlace();
    ChoreoHomePage.navigateToComponents();
    cy.get("tbody>tr p").should("be.visible");
    return cy.wrap({});
  }


  private static _getAPIinfo(projectId: string, componentHandler: string) {
    const query = GraphQLQueryBuilder.getComponentDetails(projectId, componentHandler);

    return this.callGraphQL(query).then(res => {
      const component: Component = res.body.component;
      const componentId = component.id;
      const latestVersion = component.apiVersions.find((a) => a.latest);
      return Promise.resolve({
        componentId,
        latestVersionId: latestVersion.id
      })
    })
  }
  static getBuildsByVersion(componentId: string, latestAPIVersionId: string) {

    const { handle } = Cypress.env("userData");
    const query = GraphQLQueryBuilder.getBuildsByVersionQuery(handle, componentId, latestAPIVersionId)
    return this.callGraphQL(query).then((res) => {
      if (res.status === OK) {
        const builds = res.body.buildsByVersion as []
        const { id, buildId, status } = builds[builds.length - 1]
        return Promise.resolve({ id, buildId, status })
      }
    })
  }
  static getDeployStatus(projectName: string, componentName: string, stage: Enums.DeploymentStages, status: Enums.ResponseStatus) {
    this._getAPIInfo(projectName, componentName).then(comp => {
      const { componentId, latestVersionId } = comp
      this.getBuildsByVersion(componentId, latestVersionId).then(bv => {
        const { buildId } = bv
        const url = `${PROXY_DEPLOYER_EP}/${componentId}/versions/${latestVersionId}/builds/${buildId}/status`
        this._getDeployStatus(url, stage, status)
      })
    })
  }

  static _getAPIInfo(projectName: string, componentName: string) {
    return this._getComponentInfo(projectName, componentName).then(comp => {
      const { id } = comp
      const latestVersion = comp.apiVersions.find((a) => a.latest);
      return Promise.resolve({ componentId: id, latestVersionId: latestVersion.id })
    })


  }

  static getPrmotionStatus(projectName: string, componentName: string) {
    this.getProjectByName(projectName).then(proj => {
      const { id } = proj
      this.getProjectEnvironments(id).then(projEnvs => {
        const envs = projEnvs as { name: string, id: string }[]
        const { id } = envs.find(e => e.name === Enums.Environment.PRODUCTION)
        this._getAPIInfo(projectName, componentName).then(comp => {
          const { componentId, latestVersionId } = comp
          const url = `${PROXY_DEPLOYER_EP}/${componentId}/versions/${latestVersionId}/deployments?environmentId=${id}&accessMode=external`
          Utils.sendGetRequest(url, AUTH_HEADER()).then(res => {
            const { deploymentStatus } = res.body
            Utils.isError(deploymentStatus, "Proxy With Mediation Policy Deployment Failed")
            if (deploymentStatus !== 'ACTIVE') {
              this.getPrmotionStatus(projectName, componentName)
              cy.wait(15000)
            }
            return
          })
        })
      })
    })

    // const { id } = Cypress.env(Enums.Environment.PRODUCTION);
    // const { componentId, latestAPIVersionId } = Cypress.env("apiInfo")

    // const url = `${PROXY_DEPLOYER_EP}/${componentId}/versions/${latestAPIVersionId}/deployments?environmentId=${id}&accessMode=external`
    // Utils.sendGetRequest(url, AUTH_HEADER()).then(res => {
    //   const { deploymentStatus } = res.body
    //   Utils.isError(deploymentStatus, "Proxy With Mediation Policy Deployment Failed")
    //   if (deploymentStatus !== 'ACTIVE') {
    //     this.getPrmotionStatus(projectName, componentName)
    //     cy.wait(15000)
    //   }
    //   return
    // })
  }

  private static _getDeployStatus(url: string, stage: string, status: string) {
    Utils.sendGetRequest(url, AUTH_HEADER()).then((res) => {
      if (res.status === OK) {
        const stageInfo = res.body.stageInfo as { stage: string, status: string }[]

        const deploymentStage = stageInfo.find(s => s.stage === stage)
        if (deploymentStage) {
          Utils.isError(deploymentStage.status, "Proxy With Mediation Policy Deployment Failed")
          if (deploymentStage.status === status) {
            return
          } else {
            cy.wait(10000)
            this._getDeployStatus(url, stage, status)
          }
        } else {
          cy.wait(10000)
          this._getDeployStatus(url, stage, status)
        }
      }
    })
  }

  static getProjectEnvironments(projectId: string) {
    const { uuid } = Cypress.env("userData");
    const query = GraphQLQueryBuilder.getEnvironments(uuid, projectId)
    return this.callGraphQL(query).then(res => {
      return Promise.resolve(res.body.environments)
    })
  }

  static getComponentInfo(projectName: string, componentName: string) {
    this.getProjects().then((res) => {
      const projects = res.projects;
      const project = projects.find((p) => p.name === projectName);
      return this.getComponents(project.id).then((resp) => {
        if (resp.status === SUCCESS_STATUS_CODE) {
          const comp = resp.components.find((c) => c.displayName === componentName);

          if (comp) {
            this.getProjectEnvironments(project.id).then(res => {
              this._getAPIinfo(project.id, comp.handler).then(info => {
                const { componentId, latestVersionId } = info
                const envs = res as { id: string, name: string }[]
                return Promise.resolve({
                  componentId,
                  versionId: latestVersionId,
                  envs
                })
              })
            })
            this.getDeployedComponentDetails(project.id, comp.handler);
          }
        }
      });
    });
  }

  static getDeployedComponentDetails(projectId: string, handler: string) {
    const query = GraphQLQueryBuilder.getComponentDetails(projectId, handler);
    this.callGraphQL(query).then((res) => {
      const component: Component = res.body.component;
      const componentId = component.id;
      const av: APIVersion[] = component.apiVersions;
      const latestAPIVersion = av.find((a) => a.latest);
      const latestAPIVersionId = latestAPIVersion.id;

      const apiInfo = { componentId, latestAPIVersionId };
      Cypress.env("apiInfo", apiInfo);
      const appENVS: AppEnvVersion[] = latestAPIVersion.appEnvVersions;
      appENVS.forEach((appEnv) => {
        const { release } = appEnv;
        const { id, environmentId } = release;
        const choreoEnv = release.metadata.choreoEnv;
        let releaseData = {
          componentId,
          latestAPIVersionId,
          environmentId,
          releaseId: id,
          choreoEnv,
        };
        Cypress.env(choreoEnv, releaseData);
      });
    });
  }

  static _getComponentInfo(projectName: string, componentName: string) {
    return this.getProjects().then(res => {
      const projects = res.projects;
      const project = projects.find((p) => p.name === projectName);

      return this.getComponents(project.id).then(resp => {

        if (resp.status === SUCCESS_STATUS_CODE) {
          const comp: Component = resp.components.find((c) => c.displayName === componentName);
          if (comp) {
            return Promise.resolve(comp)
          }
        }
      })
    })
  }

  static getComponentDeploymentStatus(env: string = "dev") {
    const { handle, uuid } = Cypress.env("userData");
    const { componentId, latestAPIVersionId, environmentId } = Cypress.env(env);
    const query = GraphQLQueryBuilder.getComponentDeploymentStatusQuery(
      handle,
      uuid,
      componentId,
      latestAPIVersionId,
      environmentId
    );
    this.callGraphQL(query).then((res) => {
      const { deploymentStatus, deploymentStatusV2 } =
        res.body.componentDeployment;
      if (
        deploymentStatusV2 === ERROR ||
        deploymentStatus === ERROR
      ) {
        throw new Error(" Deployment Failed");
      }
      if (
        deploymentStatusV2 === ACTIVE &&
        deploymentStatus === ACTIVE
      ) {
        return;
      } else {
        if (this.count < 10) {
          this.getComponentDeploymentStatus();
        }
      }
    });
  }

  static getServiceEndpointStatus(env: string = "dev") {
    const { componentId, latestAPIVersionId, releaseId } = Cypress.env(env);
    const query = GraphQLQueryBuilder.getEndpointStatusQuery(
      componentId,
      latestAPIVersionId,
      releaseId
    );

    this.callGraphQL(query).then((res) => {
      const { state } = res.body.componentEndpoints[0];
      cy.log("state", state);
      Utils.isError(state, "Deployment Endpoint status is ERROR")
      if (state === "Active") {
        return;
      } else {
        if (this.count < 20) {
          cy.wait(10000);
          this.count++;
          this.getServiceEndpointStatus();
        }
      }
    });
  }

  private static getPullRequests(componentId: string, repoName: string) {
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
        if (this.count < 10) {
          this.getPullRequests(componentId, repoName);
          this.count++;
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

  private static sendDeprecateRetireRequest(apiId: string) {
    APILifeCycleService.deprecateAPI(apiId);
    APILifeCycleService.retireAPI(apiId);
  }
}
