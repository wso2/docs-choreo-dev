import { AppEnvVersion } from "./app-env-version"

export interface APIVersion {
    apiVersion: string
    proxyName: string
    proxyUrl: string
    proxyId: string
    id: string
    state: string
    latest: boolean
    branch: string
    accessibility: string
    appEnvVersions: AppEnvVersion[]
}