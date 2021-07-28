// in cypress/support/index.d.ts
// load type definitions that come with Cypress module
/// <reference types="cypress" />

declare namespace Cypress {
    interface Chainable {
        consoleUserLogin(): Chainable<Element>
        userLoginWithGmail(): Chainable<Element>
        userLoginWithGithub(): Chainable<Element>
        userLogout(): Chainable<Element>
        preserveCookiesForTest(cookies: Cookie[]): Chainable<Element>
        saveLocalStorage(): Chainable<Element>
        restoreLocalStorage(): Chainable<Element>
        waitTillWorkSpace(): Chainable<Element>
        createNewApp(type: string, name: string): Chainable<Element>
        checkSourceCodeForValidation(sourceLines: string): Chainable<Element>
        configureResource(relativePath: string | null, method: string | null, returnType: string | null): Chainable<Element>
        selectTrigger(type: string): Chainable<Element>
        selectManualTrigger(): Chainable<Element>
        selectManualTriggerOptions(type: string, option: string): Chainable<Element>
        selectSpecificOption(option: string): Chainable<Element>
        selectGitHubTrigger(): Chainable<Element>
        configureGitHubTrigger(repoName: string, triggerEventType: string, triggerAction: string): Chainable<Element>
        setupGmailConnection(): Chainable<Element>
        sendGmailMessage(plusBtnIndex: number, gmailConnectionIndex: number, emailAddress: string, emailSubject: string, emailBody: string): Chainable<Element>
        fillCalendarConfigs(calendar: string): Chainable<Element>
        fillTwilioConfigs(accountSID: string, token: string, senderNumber: string, recipientNumber: string): Chainable<Element>
        typeOnNthExpressionEditor(n: number, expression: string, withinQuotes: boolean, waitForEnable?: string, validExpression?: boolean): Chainable<Element>
        createVariableProperty(type: string, name: string, expression: string, validExpression?: boolean): Chainable<Element>
        createLogProperty(type: string, expression: string): Chainable<Element>
        goBacktoAppsList(): Chainable<Element>
        searchApps(name: string): Chainable<Element>
        resetAppSearch(): Chainable<Element>
        undeployApp(type: string, name: string, strict: boolean): Chainable<Element>
        deleteApp(type: string, name: string, strict: boolean): Chainable<Element>
        cleanupApp(name: string): Chainable<Element>
        createRespond(expression: string, skipSmallPlus?: boolean): Chainable<Element>
        callExternalEndpoint(URL: string, attempts: number, expectedRes: string): Chainable<Element>
        switchToDeployView(appName: string): Chainable<Element>
        deployToChoreo(type: string, appName: string): Chainable<Element>
        testRunApp(): Chainable<Element>
        selectScheduleTrigger(): Chainable<Element>
        navigateFromHomePage(pageName: string): Chainable<Element>
        undeployAppViaRESTAPICall(appName: string): Chainable<Element>
        cleanOnPremKey(keyName: string): Chainable<Element>
        deleteAppWithoutUndeploy(name: string, strict: boolean): Chainable<Element>
        testPerformanceAnalyzerLocalStorage(localStorageKey: string): Chainable<Element>
        hideWelcomeMessage(): Chainable<Element>
        getByTestId(id: string): Chainable<Element>
        deleteApiByApplicationId(id: string): Chainable<Element>
        deleteApiByApiId(id: string): Chainable<Element>
        clearAllTestData(): Chainable<Element>
        clearAPIs(): Chainable<Element>
        clearApps(): Chainable<Element>
        clearConfigurations(): Chainable<Element>
        clearConnections(): Chainable<Element>
        clearOnPremKeys(): Chainable<Element>
    }
}
