// in cypress/support/index.d.ts
// load type definitions that come with Cypress module
/// <reference types="cypress" />

declare namespace Cypress {
    interface Chainable {
        consoleUserLogin(): Chainable<Element>
        userLoginWithGmail(): Chainable<Element>
        userLoginWithGithub(): Chainable<Element>
        userLogout(): Chainable<Element>
        preserveCookiesForTest(cookies:Cookie[]): Chainable<Element>
        waitTillWorkSpace(): Chainable<Element>
        createNewApp(type: string, name: string): Chainable<Element>
        checkSourceCodeForValidation(sourceLines: string): Chainable<Element>
        configureResource(relativePath?: string, method?: string): Chainable<Element>
        selectTrigger(type: string): Chainable<Element>
        selectManualTrigger(): Chainable<Element>
        selectManualTriggerOptions(type: string, option:string): Chainable<Element>
        selectSpecificOption(option: string): Chainable<Element>
        selectGitHubTrigger(repoName: string, triggerEventType: string, triggerAction: string): Chainable<Element>
        configureGmailConnector(emailAddress: string, action: string, emailSubject: string, emailBody: string): Chainable<Element>
        typeOnNthExpressionEditor(n: number, expression: string, withinQuotes: boolean, waitForEnable?: string): Chainable<Element>
        createVariableProperty(type: string, name: string, expression: string): Chainable<Element>
        createLogProperty(type: string, expression: string): Chainable<Element>
        goBacktoAppsList(): Chainable<Element>
        searchApps(name: string): Chainable<Element>
        resetAppSearch(): Chainable<Element>
        undeployApp(type:string, name: string, strict: boolean): Chainable<Element>
        deleteApp(type:string, name: string, strict: boolean): Chainable<Element>
        cleanupApp(type:string, name: string, strict: boolean): Chainable<Element>
        createRespond(expression: string, skipSmallPlus?: boolean): Chainable<Element>
        callExternalEndpoint(URL: string, attempts: number, expectedRes: string): Chainable<Element>
        switchToDeployView(appName: string): Chainable<Element>
        deployToChoreo(type:string, appName: string): Chainable<Element>
        testRunApp(): Chainable<Element>
        selectScheduleTrigger(): Chainable<Element>
        navigateFromHomePage(pageName: string): Chainable<Element>
    }
}
