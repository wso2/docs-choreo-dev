import { DisplayType } from "../console/pages/enum/component-display-types"

export interface ComponentData {
    projectName: string
    componentName: string
    displayType: DisplayType
    triggerChannels: string
    triggerId: string
    srcGitRepoUrl: string
}