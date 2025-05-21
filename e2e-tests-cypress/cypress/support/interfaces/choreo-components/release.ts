import { MetaData } from "./metadata"

export interface Release {
    id: string
    metadata: MetaData
    environmentId: string
    environment: string
    gitHash: string
    gitOpsHash: string
}