import { AbsComponent } from "../../interfaces/abs-component";
import { ByocComponent } from "../../interfaces/byoc-component"
import { ComponentData } from "../../interfaces/component-data";
import { Enums } from "../enums";

export class GraphQLQueryBuilder {


  static getComponentDetails(projectId: string, componentHandler: string) {
    return {
      query: `query{
      component(
        projectId: "${projectId}"
        componentHandler: "${componentHandler}"
      ){
        id,
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
          organizationApp,
          organizationConfig,
          isUserManage
        },
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
            release{
              id,
              metadata{
                choreoEnv
              },
              environmentId,
              environment,
              gitHash,
              gitOpsHash,
            }
          }
        }
      }
    }
`
    }
  }

  static getComponentDeploymentStatus(orgHandler: string, orgUuid: string, componentId: string, versionId: string, environmentId: string) {
    return {
      query: `query {
                         componentDeployment(
                                orgHandler: "${orgHandler}"
                                orgUuid: "${orgUuid}"
                                componentId: "${componentId}"
                                versionId: "${versionId}"
                                environmentId: "${environmentId}"
                                             ) 
                                             {
        environmentId
        configCount
        apiId
        releaseId
        apiRevision {
            id
            displayName
        }
        build{
          buildId
          deployedAt
          commit {
            author {
              name
              date
              email
              avatarUrl
            }
            sha
            message
            isLatest
          }
        }
        invokeUrl
        versionId
        deploymentStatus
        deploymentStatusV2
        version
        cron
      }
    }
`
    }
  }


  static getBYOCComponentCreationQuery(byocComponent: ByocComponent, projectId: string) {
    return {
      query: `mutation {
      createByocComponent(
        component: {
          name: "${byocComponent.name}",
          displayName: "${byocComponent.displayName}",
          description: "${byocComponent.description}",
          orgId: ${byocComponent.orgId},
          orgHandler: "${byocComponent.handle}",
          projectId: "${projectId}",
          labels: "",
          componentType: "${byocComponent.componentType}",
          port: ${byocComponent.port},
          oasFilePath: "${byocComponent.oasFilePath}",
          accessibility: "${byocComponent.accessibility}",
          byocConfig: {
            dockerfilePath:  "${byocComponent.byocConfig.dockerfilePath}",
            dockerContext:"${byocComponent.byocConfig.dockerContext}",
            srcGitRepoUrl:"${byocComponent.byocConfig.srcGitRepoUrl}",
            srcGitRepoBranch: "${byocComponent.byocConfig.srcGitRepoBranch}",
          }
        }
      ) 
      {
        id,
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
        httpBased
      }
    }`
    }

  }


  static getRestComponentCreationQuery(componentData: ComponentData, projectId: string) {
    return {
      query: `mutation{
                  createComponent(
                             component: {
                                  name: "${componentData.componentName}",
                                  orgId: ${componentData.orgId},
                                  orgHandler: "${componentData.handle}",
                                  displayName: "${componentData.componentName}",
                                  displayType: "${componentData.displayType}",
                                  projectId: "${projectId}",
                                  labels: "",
                                  version: "1.0.0",
                                  description: "",
                                  apiId: "",
                                  ballerinaVersion: "swan-lake-alpha5",
                                  triggerChannels: "${componentData.triggerChannels}",
                                  triggerID: ${componentData.triggerId},
                                  httpBase: true,
                                  sampleTemplate: "${componentData.sampleTemplate}",
                                  accessibility: "${componentData.accessibility}",
                                  srcGitRepoUrl: "${componentData.srcGitRepoUrl}"
                                  repositorySubPath: "${componentData.repositorySubPath}",
                                  repositoryType: "${componentData.repositoryType}",
                                  repositoryBranch: "main",
                                  initializeAsBallerinaProject: ${componentData.initializeAsBallerinaProject},
                                } )
                                {id, orgId, projectId, handler    }
                      }`,
    };

  }

}