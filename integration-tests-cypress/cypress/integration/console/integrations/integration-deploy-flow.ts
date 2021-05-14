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
        cy.log("Login into Choreo using Github");
        cy.consoleUserLogin();
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.selectTrigger("Manual");
        cy.selectManualTriggerOptions("Statements","addLog");
        cy.createLogProperty("Info", "Hello World");
    })

    after(() => {
        cy.goBacktoAppsList();
        cy.undeployApp("integration", appName, true);
        cy.deleteApp("integration", appName, true);
        cy.userLogout();
    })

    it('test-run and deploy integration', () => {
        const loadRunTxt = "Running...";

        cy.testRunApp();
        cy.get('.product-tour-logs-panel').contains(loadRunTxt).should('exist');
        cy.get('.product-tour-logs-panel').contains(loadRunTxt).should('not.exist', 50000);
        cy.get('[data-testid="log-panel"]').should('contains.text',"message = \"Hello World\"");
        cy.log('Expression is logged successfully');

        cy.deployToChoreo("integration", appName);
    });
});

describe('Prebuilt integration test run and deployment', () => {
    let appName:string

    before(() => {
        cy.log("Login into Choreo using Github");
        cy.consoleUserLogin();
    })

    after(() => {
        cy.goBacktoAppsList();
        cy.cleanupApp(appName);
        cy.userLogout();
    })

    it('test-run and deploy integration', () => {
        let appSvcUrl = Cypress.env("appSvcURL");
        let orgName = Cypress.env("selectedOrgHandle");
        //This is the post call we are interested in capturing
        cy.intercept('POST', `${appSvcUrl}/orgs/${orgName}/apps`).as('templateCall');

        cy.navigateFromHomePage(INTEGRATIONS_TEXT);
        cy.get('[data-testid="use-prebuilt-btn"]').should('exist').click();
        cy.log("Prebuilt integrations page loaded successfully");
        cy.get('[data-testid="gcalendar-to-twilio"]').should('exist').children().contains('Use this').click({force:true});

        cy.wait('@templateCall',{timeout:30000}).then((interception) => {
            appName = interception.response.body[`name`];
            cy.log("Selected prebuilt integration with name: " + appName);
            cy.waitTillWorkSpace();
            cy.get('.diagram-canvas').should('exist');
            cy.fillCalendarConfigs("test.user.choreo@gmail.com");
            cy.fillTwilioConfigs("ACat9e5d3a348126a5fcabb03a03f1a1bb", "d976402933e8a4143015c4499e971a65", "+94786941431", "+94743149897");
            cy.get('[data-testid="config-save-btn"]').should('be.visible').click();

            cy.get('[data-testid="deploy-start-button"]').should('exist').click();
            cy.log('Deploying application...');
            cy.contains('Starting').should('exist');
            cy.get('[data-testid="initialize-stage-text"]').siblings('[src="/images/check.svg"]').should('exist');
            cy.get('[data-testid="build-stage-text"]').siblings('[src="/images/check.svg"]').should('exist');
            cy.get('[data-testid="deploy-stage-text"]').siblings('[src="/images/building.svg"]').should('exist');
            cy.get('[data-testid="deploy-stage-text"]').siblings('[src="/images/building.svg"]', {timeout: 600000}).should('not.exist');
            cy.contains('Starting').should('not.exist');
            cy.contains('Started').should('exist');
            cy.log("Successfully deployed");
            cy.get('[data-testid="deploy-stop-button"]').should('exist').click();
            cy.contains('Stopping').should('not.exist');
            cy.log("Successfully un-deployed");
            cy.get('[data-testid="deploy-start-button"]').should('exist');
        });
    })
});
