/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */
import {generateAppName} from "../../support/common/choreo-utils";

/// <reference types="cypress" />

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
