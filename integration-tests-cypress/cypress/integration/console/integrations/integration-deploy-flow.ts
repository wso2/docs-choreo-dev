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
import { generateAppName } from "../../../support/common/utils";
import { INTEGRATIONS_TEXT } from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Integrations test run and deployment from scratch', ()=>{
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google");
        cy.consoleUserLogin();
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.selectTrigger("Manual");
        cy.selectManualTriggerOptions("Statements","addLog");
        cy.createLogProperty("Info", "Hello World");
    }),

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp("integration", appName, true);
        cy.userLogout();
    }),

    it('test-run and deploy integration', () => {
        const loadRunTxt = "Running...";

        cy.testRunApp();
        cy.get('.product-tour-logs-panel').contains(loadRunTxt).should('exist');
        cy.get('.product-tour-logs-panel').contains(loadRunTxt).should('not.exist', 50000);
        cy.get('[data-testid="log-panel"]').should('contains.text',"message = \"Hello World\"");
        cy.log('Expression is logged successfully');

        cy.deployToChoreo("integration", appName);
        cy.get('#tabpanel-1').contains("Successfully deployed").should('exist');
    });
});
