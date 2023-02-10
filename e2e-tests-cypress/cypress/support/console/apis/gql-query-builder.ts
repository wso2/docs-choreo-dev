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


}