import { generateAppName } from '../../support/choreo-utils';

/// <reference types="cypress" />

describe('Application test run and deployment', () => {
    let savedCookies
    let appName: string

    before(() => {
        cy.log("Logging into Choreo using google")
        cy.userLoginWithGmail()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    })

    after(() => {
        cy.userLogout()
    })

    beforeEach(() => {
        savedCookies.map((cookie) => {
            cy.setCookie(cookie.name, cookie.value, {
                domain: cookie.domain,
                expiry: cookie.expires,
                httpOnly: cookie.httpOnly,
                path: cookie.path,
                secure: cookie.secure
            })

            Cypress.Cookies.defaults({
                preserve: cookie.name
            })
        })

        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.selectTrigger("API", "hello");
        cy.createProperty("var", "res", '"hello world"');
        cy.createRespond("res");
    })

    afterEach(() => {
        cy.goBacktoAppsList();
        cy.deleteAppWithoutUndeploy("service", appName, true);
    })

    it('deploy hello world service', () => {
        cy.deployToChoreo(appName);
        cy.get('[data-testid="prod-url"]').invoke('text').then((appURL) => {
            expect(appURL).not.to.equal('');
            cy.log("test url: ", appURL);
            expect(appURL).to.contain('https://');
            cy.callExternalEndpoint((appURL + "/hello"), 3, "hello world");
            cy.log('Hello world string recieved successfully!');
        })
    })
})