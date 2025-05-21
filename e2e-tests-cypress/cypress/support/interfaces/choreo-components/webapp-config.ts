export interface WebappConfig {
    dockerContext: string,
    srcGitRepoUrl: string,
    srcGitRepoBranch: string,
    webAppType: string,
    webAppBuildCommand: string,
    webAppPackageManagerVersion: string,
    webAppOutputDirectory: string
    isAppGatewayEnabled: boolean
}