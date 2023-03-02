import { APIVersion } from "./api-versions"
import { Repository } from "./repository"

export interface Component {
    createdAt: string
    description: string
    displayName: string
    displayType: string
    handler: string
    id: string
    name: string
    orgHandler: string
    projectId: string
    version: string
    ownerName: string
    orgId: 869
    labels: string[]
    updatedAt: string
    apiId: string
    httpBased: boolean
    isMigrationCompleted: boolean
    repository: Repository
    apiVersions: APIVersion[]
}