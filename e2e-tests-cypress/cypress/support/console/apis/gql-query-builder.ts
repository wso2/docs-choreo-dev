import { ByocComponent } from "../../interfaces/byoc-component";
import { TestRunnerComponent } from "../../interfaces/choreo-components/testrunner-component";
import { WebappComponent } from "../../interfaces/choreo-components/webapp-component";
import { ComponentData } from "../../interfaces/component-data";
import { IntegrationComponentData } from "../../interfaces/integration-component-data";

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
`,
    };
  }

  static getEndpointStatusQuery(
    componentId: string,
    versionId: string,
    releaseId: string
  ) {
    return {
      query: `query List {
      componentEndpoints(
        input: {
          componentId: "${componentId}"
          versionId: "${versionId}"
        options: {
          filter: {
            releaseIds: ["${releaseId}"]
          }
        }
      })
        {
          id
          createdAt
          updatedAt
          releaseId
          environmentId
          displayName
          port
          type
          apiContext
          apiDefinitionPath
          invokeUrl
          visibility
          hostName
          apimId
          apimRevisionId
          apimName
          projectUrl
          organizationUrl
          publicUrl
          state
          stateReason {
            code
            message
            details
            workerId
          }
          isDeleted
          deletedAt
        }
      }`,
    };
  }

  static getComponentDeploymentStatusQuery(
    orgHandler: string,
    orgUuid: string,
    componentId: string,
    versionId: string,
    environmentId: string
  ) {
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
`,
    };
  }

  static getBYOCComponentCreationQuery(
    byocComponent: ByocComponent,
    projectId: string
  ) {
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
    }`,
    };
  }

  static getWebAppComponentCreationQuery(
    byocComponent: WebappComponent,
    projectId: string
  ) {
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
          accessibility: "${byocComponent.accessibility}",
          byocWebAppsConfig: {
            dockerContext:  "${byocComponent.byocWebAppsConfig.dockerContext}",
            srcGitRepoUrl: "${byocComponent.byocWebAppsConfig.srcGitRepoUrl}",
            srcGitRepoBranch: "${byocComponent.byocWebAppsConfig.srcGitRepoBranch}",
            webAppType: "${byocComponent.byocWebAppsConfig.webAppType}",
            webAppBuildCommand: "${byocComponent.byocWebAppsConfig.webAppBuildCommand}",
            webAppPackageManagerVersion: "${byocComponent.byocWebAppsConfig.webAppPackageManagerVersion}",
            webAppOutputDirectory: "${byocComponent.byocWebAppsConfig.webAppOutputDirectory}",
            isAppGatewayEnabled: ${byocComponent.byocWebAppsConfig.isAppGatewayEnabled},
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
    }`,
    };
  }

  static getTestRunnerComponentCreationQuery(
    testRunnerComponent: TestRunnerComponent,
    projectId: string
  ) {
    return {
      query: `mutation {
        createBuildpackComponent(
        component: {
          name: "${testRunnerComponent.name}",
          displayName: "${testRunnerComponent.displayName}",
          description: "${testRunnerComponent.description}",
          orgId: ${testRunnerComponent.orgId},
          orgHandler: "${testRunnerComponent.handle}",
          projectId: "${projectId}",
          labels: "",
          componentType: "${testRunnerComponent.componentType}",
          port: null,
          oasFilePath: "",
          accessibility: "${testRunnerComponent.accessibility}",
          buildpackConfig: {
            buildContext:  "${testRunnerComponent.buildpackConfig.buildContext}",
            srcGitRepoUrl: "${testRunnerComponent.buildpackConfig.srcGitRepoUrl}",
            srcGitRepoBranch: "${testRunnerComponent.buildpackConfig.srcGitRepoBranch}",
            languageVersion: "${testRunnerComponent.buildpackConfig.languageVersion}",
            buildpackId: "${testRunnerComponent.buildpackConfig.buildpackId}",
          }
          secretRef: "",
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
    }`,
    };
  }

  static getMIComponentCreationQuery(
    componentData: IntegrationComponentData,
    projectId: string
  ) {
    return {
      query: `mutation{
                      createIntegrationComponent(
                               component: {
                                    name: "${componentData.componentName}",
                                    displayName: "${componentData.componentName}",
                                    description: "",
                                    orgId: ${componentData.orgId},
                                    orgHandler: "${componentData.handle}",
                                    projectId: "${projectId}",
                                    labels: "",
                                    componentType: "${componentData.componentType}",
                                    accessibility: "${componentData.accessibility}",
                                    srcGitRepoUrl: "${componentData.srcGitRepoUrl}",
                                    srcGitRepoBranch: "${componentData.srcGitRepoBranch}",
                                    repositorySubPath: "${componentData.repositorySubPath}",
                                    oasFilePath: "${componentData.oasFilePath}"
                                    version: "1.0.0"
                                  } )
                                  { id,
                                    handle,
                                    organizationId,
                                    projectId,

                                  }
                        }`,
    };
  }

  static getRestComponentCreationQuery(
    componentData: ComponentData,
    projectId: string
  ) {
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

  static getBuildsByVersionQuery(
    orgHandler: string,
    componentId: string,
    versionId: string
  ) {
    return {
      query: `query {
      buildsByVersion(
        orgHandler: "${orgHandler}"
        build: {
          componentId: "${componentId}"
          versionId: "${versionId}"
    }
      ) {
        id,
        createdDate,
        versionId,
        buildId,
        commitHash,
        commitMessage,
        revisions {
          revisionId,
          createdDate,
          description,
          environments
        }
    }
}`,
    };
  }

  static getEnvironments(uuid: string, projectId: string) {
    return {
      query: `query {
        environments(orgUuid:"${uuid}", type: "external",
    projectId:"${projectId}"
    ){
          name,
          id,
          choreoEnv,
          vhost,
          apiEnvName,
          isMigrating,
          apimEnvId,
          namespace,
          sandboxVhost,
          critical,
          isPdp
        }
      }`,
    };
  }

  static getPrxoyDeployments(
    orgHandle: string,
    orgUUID: string,
    componentId: string,
    versionId: string,
    environmentId: string
  ) {
    return {
      query: `query {
        proxyDeployment(
          orgHandler: "${orgHandle}"
          orgUuid:"${orgUUID}"
          componentId: "${componentId}"
          versionId: "${versionId}"
          environmentId: "${environmentId}"
        ) {
          apiId,
          environment {
            choreoEnv,
            name,
            id
          },
          lifecycleStatus,
          version,
          invokeUrl,
          endpoint,
          sandboxEndpoint,
          apiRevision {
            id,
            displayName,
            createdTime
          },
          build {
            id
            baseRevisionId
            deployedRevisionId
          },
          deployedTime,
          successDeployedTime
        }
      }`,
    };
  }

  static getLifeCycleChangeQuery(projectId: string, componentHandler: string) {
    return {
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
  }
}
