// in cypress/support/index.d.ts
// load type definitions that come with Cypress module
/// <reference types="cypress" />

declare namespace Cypress {
    interface Chainable {
        consoleUserLogin(): Chainable<Element>
        userLoginWithGmail(): Chainable<Element>
        userLoginWithGithub(): Chainable<Element>
        userLogout(): Chainable<Element>
        waitTillWorkSpace(): Chainable<Element>
        createNewApp(name: string): Chainable<Element>
        checkSourceCodeForValidation(sourceLines: string): Chainable<Element>
        selectAPITrigger(method: string, relativePath?: string): Chainable<Element>
        selectTrigger(type: string, relativePath?: string, method?: string): Chainable<Element>
        selectStatementOption(option: string): Chainable<Element>
        typeOnNthExpressionEditor(n: number, expression: string, withinQuotes: boolean, waitForEnable?: string): Chainable<Element>
        createProperty(type: string, name: string, expression: string): Chainable<Element>
        goBacktoAppsList(): Chainable<Element>
        searchApps(name: string): Chainable<Element>
        resetAppSearch(): Chainable<Element>
        undeployApp(ame: string, strict: boolean): Chainable<Element>
        deleteApp(name: string, strict: boolean): Chainable<Element>
        createRespond(expression: string, skipSmallPlus?: boolean): Chainable<Element>
        callExternalEndpoint(URL: string, attempts: number, expectedRes: string): Chainable<Element>
        switchToDeployView(appName: string): Chainable<Element>
        deployToChoreo(appName: string): Chainable<Element>
    }
  }
