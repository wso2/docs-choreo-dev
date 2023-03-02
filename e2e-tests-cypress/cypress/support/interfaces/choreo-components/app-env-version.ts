import { Release } from "./release"

export interface AppEnvVersion{

    environmentId: string
    releaseId: string
    release: Release
}