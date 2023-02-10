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

import { computeHeadingLevel } from "@testing-library/dom";
import { GitHub } from "../../github/github";
import { ComponentData } from "../../interfaces/component-data";
import { IntegrationComponentData } from "../../interfaces/integration-component-data";
import { PR } from "../../interfaces/pr";
import { ONE_HOUR } from "../constants";
import { ChoreoHomePage } from "../pages/home/home-page";
import { Utils } from "../utils";
import { GraphQLQueryBuilder } from "./gql-query-builder";

export const SUCCESS_STATUS_CODE = 200;
export const CREATED_STATUS_CODE = 201;
export const NO_CONTENT_STATUS_CODE = 204;
export class GraphQL {
  static count = 0
  static createDefaultProjectIfNotExists(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjects(orgId).then((response) => {
      expect(response.status).to.eq(SUCCESS_STATUS_CODE);
    });
  }

  static deleteProjectsCreatedByTests(
    orgId: number,
    orgHandle: string,
    token: string
  ) {
    this.getProjects(orgId).then((response) => {
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

      projects.forEach((project) => {
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

  static getComponents(projectId: string, orgHandle: string, token: string) {
    const query = {
      query: `query{ components(orgHandler: "${orgHandle}", projectId: "${projectId}"){
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

    return this.callGraphQL(query);
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

    this.callGraphQL(query).then((response) => {
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

  static getProjects(orgId: number) {
    const query = {
      query: `query{projects(orgId: ${orgId}){ id, orgId, name, version, createdDate,handler }}`,
    };

    return this.callGraphQL(query);
  }

  static callGraphQL(query: any) {
    const appSvcURL = Cypress.env("newAppSvcURL");
    const header = {
      Authorization: `Bearer ${Cypress.env("apim_token")}`,
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

  static createComponentWithRepo(
    componentData: ComponentData,
    repoName?: string
  ) {
    const { id } = Cypress.env("current_org");
    this.getProjects(id).then((res) => {
      const projects = res.body.data.projects as [];
      const project = projects.find(
        (p) => p["name"] === componentData.projectName
      );
      cy.log(`Project Id :: ${project["id"]}`);
      const query = {
        query: `mutation{
                  createComponent(
                             component: {
                                  name: "${componentData.componentName}",
                                  orgId: ${id},
                                  orgHandler: "${Cypress.env(
          "choreoOrgHandle"
        )}",
                                  displayName: "${componentData.componentName}",
                                  displayType: "${componentData.displayType}",
                                  projectId: "${project["id"]}",
                                  labels: "",
                                  version: "1.0.0",
                                  description: "",
                                  apiId: "",
                                  ballerinaVersion: "swan-lake-alpha5",
                                  triggerChannels: "${componentData.triggerChannels
          }",
                                  triggerID: ${componentData.triggerId},
                                  httpBase: true,
                                  sampleTemplate: "${componentData.sampleTemplate
          }",
                                  accessibility: "${componentData.accessibility
          }",
                                  srcGitRepoUrl: "${componentData.srcGitRepoUrl
          }"
                                  repositorySubPath: "${componentData.repositorySubPath
          }",
                                  repositoryType: "${componentData.repositoryType
          }",
                                  repositoryBranch: "main",
                                  initializeAsBallerinaProject: ${componentData.initializeAsBallerinaProject
          },
                                } )
                                {id, orgId, projectId, handler    }
                      }`,
      };

      this.callGraphQL(query).then((res) => {
        const { id, projectId, handler } = res.body.data.createComponent;
        Cypress.env("component", { id, projectId, handler })
        if (componentData.initializeAsBallerinaProject) {
          this.getPullRequests(id, repoName);
        }
        expect(res.status).to.be.eq(200);
        this.getDeployedComponentDetails(projectId, handler)
      });
    });
    ChoreoHomePage.navigateToMarketPlace();
    ChoreoHomePage.navigateToProjects();
    cy.get("tbody>tr p").should("be.visible");
    return cy.wrap({})
  }

  static createIntegrationComponent(
    componentData: IntegrationComponentData
  ) {
    const { id } = Cypress.env("current_org");
    this.getProjects(id).then((res) => {
      const projects = res.body.data.projects as [];
      const project = projects.find(
        (p) => p["name"] === componentData.projectName
      );
      cy.log(`Project Id :: ${project["id"]}`);
      const query = {
        query: `mutation{
                    createIntegrationComponent(
                             component: {
                                  name: "${componentData.componentName}",
                                  displayName: "${componentData.componentName}",
                                  description: "",
                                  orgId: ${id},
                                  orgHandler: "${Cypress.env(
                                    "choreoOrgHandle"
                                  )}",
                                  projectId: "${project["id"]}",
                                  labels: "",
                                  componentType: "${componentData.componentType}",
                                  accessibility: "${
                                    componentData.accessibility
                                  }",
                                  srcGitRepoUrl: "${
                                    componentData.srcGitRepoUrl
                                  }",
                                  srcGitRepoBranch: "${componentData.branch}",
                                  oasFilePath: "${componentData.oasFilePath}"
                                  version: "1.0.0"
                                } )
                                { id,
                                  createdAt,
                                  updatedAt,
                                  name,
                                  handle,
                                  organizationId,
                                  projectId,
                                  orgHandle,
                                  type,
                                  description,
                                  imageRegistryId,
                                  imageRegistry {
                                  id,
                                  createdAt,
                                  updatedAt,
                                  cloudConnectorId,
                                  imageRepositoryName
                                  },
                                  componentType,
                                  httpBased }
                      }`,
      };
      this.callGraphQL(query).then((res) => {
        const { id, projectId, handle } = res.body.data.createIntegrationComponent;
        expect(res.status).to.be.eq(200);
        this.getDeployedComponentDetails(projectId, handle)
      });
    });
    ChoreoHomePage.navigateToMarketPlace();
    ChoreoHomePage.navigateToProjects();
    cy.get("tbody>tr p").should("be.visible");
    return cy.wrap({})
  }

  static getDeployedComponentDetails(projectId: string, handler: string) {
    const query = GraphQLQueryBuilder.getComponentDetails(projectId, handler)
    this.callGraphQL(query).then(res => {
      const { apiVersions } = res.body.data.component
      const componentId = res.body.data.component["id"];
      const av = apiVersions as []
      const latestAPIversion = av.find(a => a["latest"])
      const latestAPIVersionId = latestAPIversion["id"]
      const appENVS = latestAPIversion["appEnvVersions"] as []
      appENVS.forEach(a => {
        const { release } = a
        const { id, environmentId } = release
        const choreoEnv = release["metadata"]["choreoEnv"]

        let releaseData = { componentId, latestAPIVersionId, environmentId, releaseId: id, choreoEnv }

        Cypress.env(choreoEnv, releaseData)
      })
    })
  }

  static getComponentDeploymentStatus(env: string = "dev") {
    const { handle, uuid, } = Cypress.env("userData");
    cy.log(JSON.stringify(Cypress.env(env)))
    const { componentId, latestAPIVersionId, environmentId } = Cypress.env(env)
    const query = GraphQLQueryBuilder.getComponentDeploymentStatus(handle, uuid, componentId, latestAPIVersionId, environmentId)
    let isActive: boolean = false

    this.callGraphQL(query).then(res => {
      const { deploymentStatus, deploymentStatusV2 } = res.body.data.componentDeployment
      cy.log(deploymentStatus, deploymentStatusV2)
      if (deploymentStatusV2 === "ERROR" || deploymentStatus === "ERROR") {
        throw new Error(' Deployment Failed');
      }
      if (deploymentStatusV2 === "ACTIVE" && deploymentStatus === "ACTIVE") {
        return;
      } else {
        if (this.count < 10) {
          this.getComponentDeploymentStatus()
        }
      }
    })
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
      const prs: PR[] = res.body.data.componentPullRequests as [];
      if (prs.length > 0) {
        const { number } = prs[0];
        GitHub.mergePR(repoName, number).then((resp) => expect(resp.status).to.be.eq(200));
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
        const apiVersion: [] = res.body.data.component.apiVersions;
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
    const { uuid } = Cypress.env("current_org");
    cy.log(`Current UUID ==> ${uuid}`);

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
    const url = `${Cypress.env(
      "balRegistryURL"
    )}/packages/${organization}/${name}/${version}?force=true`;
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
    const { handle } = Cypress.env("current_org");
    cy.log(`Current handle ==> ${handle}`);
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
