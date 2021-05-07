/// <reference types="cypress" />

import {generateAppName} from "../../support/common/choreo-utils";

describe('App test run and deployment from scratch', ()=>{
    let savedCookies
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.consoleUserLogin()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    })

    after(() => {
        cy.userLogout()
    })

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp("integration", appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.selectTrigger("Manual");
        cy.selectManualTriggerOptions("Statements","addLog");
        cy.createLogProperty("Info", "Hello World");
    })

    afterEach(() => {
        cy.goBacktoAppsList();
        cy.deleteApp("integration", appName, true);
    })

    it('test and run integration', () => {
        cy.testRunApp();

        cy.get('[data-testid="log-panel"]').should('contains.text',"message = \"Hello World\"");
        cy.log('Expression is logged successfully');
    })

    it('deploy integration', () => {
        cy.deployToChoreo("integration", appName);
        cy.get('#tabpanel-1').contains("Successfully deployed").should('exist');
    })
})
