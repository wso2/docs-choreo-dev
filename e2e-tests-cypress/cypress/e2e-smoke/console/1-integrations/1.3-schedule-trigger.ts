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

import { generateAppName, getSelectedOrgHandle } from '../../../support/common/utils';
import { INTEGRATIONS_TEXT } from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Schedule trigger test run and deployment', () => {
    let savedCookies: Cypress.Cookie[];
    let appName: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const org = user?.orgs.find((org) => org.handle === getSelectedOrgHandle(user));
            cy.clearAllTestData(org);
        });
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    })

    after(() => {
        cy.userLogout();
    })

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
    })

    it('create schedule trigger integration app', () => {
        appName = generateAppName("app");
        cy.log('Generated application name: ', appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);
        cy.verifyAppName(appName);
        cy.selectTrigger("Schedule");
        cy.selectManualTriggerOptions("Statements", "addLog");
        cy.createLogProperty("Info", "Hello world");
    })

    it('check LS diagnostics in expression editor', () => {
        const firstVariableName = 'numVar';
        cy.log('Creating integer variable');
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('int', firstVariableName, '1');

        cy.log('Assigning integer variable to string variable');
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('string', 'stringVar', firstVariableName, false);

        cy.log('Checking expression editor diagnostics is visible');
        cy.get('[data-testid="expr-diagnostics"]').should('be.visible');

        cy.log('Updating the input with a valid expression');
        cy.get('.exp-editor').get('.monaco-editor').get('.view-line').eq(0).click().type('.toString()');

        cy.log('Checking expression editor diagnostics is not visible and save button is enabled');
        cy.get('[data-testid="save-btn"').should('not.have.attr', 'disabled');
        cy.get('[data-testid="expr-diagnostics"]').should('not.exist');

        cy.log("Creating variable with valid expression");
        cy.get('[data-testid="save-btn"]').click();
        cy.get('[data-testid="diagram-loader"]').should('not.exist');

        // this hack will add a sleep to the code using editor. This make sure the cronjob pod is not killed immediately
        cy.get('#EditCodeIcon').click();
        cy.get( '[data-testid="code-editor"] .monaco-editor textarea:first' ).click().focused()
            .type('{pageup}').type('import ballerina/lang.runtime;{enter}')
            .type('{downarrow}{downarrow}{downarrow}{downarrow}{downarrow}{downarrow}{end}{enter}')
            .type('runtime:sleep(30);{enter}');
        cy.get('#CommitCodeIcon_Default').click();
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    })

    it('run schedule trigger integration', () => {
        cy.get('[data-testid="editor-run-btn"]').should('be.visible');
        cy.get('[data-testid="editor-run-btn"]').click({ force: true });
        cy.log('Started test run');
        cy.contains('[data-testid="log-panel"]', 'Hello world', { timeout: 1000 * 60 * 2 }).should('exist');
        cy.contains('[data-testid="log-panel"]', 'Application exited', { timeout: 1000 * 60 * 2 }).should('exist');

        cy.log('Schedule trigger printed the log successfully');
    })

    it('deploy schedule trigger integration', () => {
        cy.wait(10000);
        cy.deployToChoreo("schedule", appName);
        cy.log('Awaiting 5 minutes to check if the expected log is printed');
        cy.contains('[data-testid="log-panel-entry"]', 'Hello world', { timeout: 1000 * 60 * 5 }).should('exist');
        cy.log('Deployed Scheduler ran successfully and  printed the log');
    })

    it("Undeploy app and delete the app", () => {
        cy.goBacktoAppsList();
        cy.undeployApp("integration", appName, true);
        cy.deleteApp("integration", appName, true);
    });
})
