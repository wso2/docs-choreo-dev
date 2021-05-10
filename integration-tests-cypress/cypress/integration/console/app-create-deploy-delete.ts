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

describe('Application test run and deployment', () => {
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
        cy.createNewApp("service", appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello");
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty("var", "res", '"hello world"');
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
