import { DisplayType } from "../console/enums"

export interface ComponentData {
    projectName: string
    componentName: string
    displayType: DisplayType
    triggerChannels: string
    triggerId: string
    srcGitRepoUrl: string
}