export interface APIVersion {
    accessibility: string
    apiVersion: string
    branch: string
    id: string
    latest: boolean
    proxyId: string
    proxyName: string
    proxyUrl: string
    state: string
    apiVersions:APIVersion[]
}