/// <reference types="cypress" />

declare namespace Cypress {
    interface Chainable {
        devportalLogin(): Chainable<Element>
        navigateToOverviewInDevportal(apiName: string): Chainable<Element>
        findByText(apiaName: string): Chainable<Element>
        findByRole(apiaName: string, object: any): Chainable<Element>
    }
}
