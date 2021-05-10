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

import { generateAppName } from '../../support/common/choreo-utils';

/// <reference types="cypress" />

describe('Schedule trigger test run and deployment', () => {
    let savedCookies
    let appName: string

    before(() => {
        cy.log("Logging into Choreo using google")
        cy.userLoginWithGmail()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
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
    })

    it('create schedule trigger integration app', () => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp("integration", appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.selectTrigger("Schedule");
        cy.createLogProperty("Info", "Hello world");
    })

    it('run schedule trigger integration', () => {
        cy.get('[data-testid="editor-run-btn"]').should('be.visible');
        cy.get('[data-testid="editor-run-btn"]').click();
        cy.log('Started test run');

        cy.contains('[data-testid="log-panel"]', 'Hello world', {timeout: 600000}).should('exist');
        cy.log('Schedule trigger printed the log successfully');
    })

    it('deploy schedule trigger integration', () => {
        cy.deployToChoreo("schedule", appName);
        cy.log('Waiting 1 minute before checking whether the scheduler rand');
        cy.wait(60000);
        cy.contains('button', 'Run & Test').click();
        cy.contains('button', 'Go Live').click();
        cy.contains('[data-testid="log-panel"]', 'Hello world', {timeout: 600000}).should('exist');
        cy.log('Deployed Scheduler ran successfully and  printed the log');
    })

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp("schedule", appName, true);
        cy.userLogout()
    })
    
})