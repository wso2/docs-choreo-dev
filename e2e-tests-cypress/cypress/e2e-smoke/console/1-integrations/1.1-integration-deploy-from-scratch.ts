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
import { INTEGRATIONS_TEXT } from "../../../support/common/constants";

/// <reference types="cypress" />

describe("Integrations test run and deployment from scratch", () => {
    let appName: string;

    before(() => {
        cy.consoleUserLogin();
    });

    after(() => {
        cy.userLogout();
    });

    it("Test Integration app", () => {
        appName = generateAppName("app");
        cy.log("app name: " + appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);
        cy.url().should("include", "app/" + appName + "/develop"); 
        cy.selectTrigger("Manual");
        cy.selectManualTriggerOptions("Statements", "addLog");
        cy.createLogProperty("Info", "Hello World"); 
        cy.get('[id="SmallPlus"]').eq(1).click({force: true});
        cy.get('[data-testid="api-options"]').click();
        cy.get('[data-testid="covid 19 api"]').click({force:true});
        cy.contains('Save Connection').click()
        cy.waitTillWorkSpace();

        // Test-run and deploy integration
        cy.log('Test app:' + appName);
        cy.testRunApp();
        cy.get('[data-testid="log-panel"]', { timeout: 60000 }).should("contains.text", 'Starting application');
        cy.get('[data-testid="log-panel"]').should("contains.text", 'message = "Hello World"');
        cy.log("Expression is logged successfully");
        cy.deployToChoreo("integration", appName);

        // Undeploy app from UI and delete the app
        cy.goBacktoAppsList();
        cy.deleteAppWithoutUndeploy(appName, true);
        cy.undeployApp("integration", appName, true);
        cy.hideWelcomeMessage();
        cy.deleteApp("integration", appName, true);
        cy.log("App deleted successfully!")
    });
});
