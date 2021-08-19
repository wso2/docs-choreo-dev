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

/// <reference types="cypress" />

import { generateAppName, getSelectedOrgHandle } from "../../../support/common/utils";

describe('anonymous-app-linking', () => {
    let selectedOrgHandle: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            selectedOrgHandle = getSelectedOrgHandle(user);
        });
    });

    after(() => {
        cy.userLogout();
    });

    it('linking-app', () => {
        let reporterHostName = Cypress.env("reporterHostName");
        const appName = generateAppName("anon");
        cy.log("Start running in anonymous mode");
        cy.exec(`export REPORTER_HOST_NAME=${reporterHostName} && ./cypress/fixtures/console/applinking-configs/run_annonapp.sh`).then(result => {
            const obsUrl = /visit (http[^\s]+)/i.exec(result.stdout)[1].replace('http://','https://');
            cy.log("Retrieved Observability URL for the anonymous app: " + obsUrl);
            cy.visit(obsUrl);
            cy.get('[data-testid=link-to-choreo]').should('be.visible').click();
            cy.get('[data-testid="link-app-name"]').should('be.visible');
            cy.log("Created a Choreo app for connection:- " + appName);
            cy.get('[data-testid="link-app-name"]').type(appName);
            cy.get('[data-testid="link-app-next-btn"]').click();
            cy.log("Linking the anonymous app to: "+ appName);

            cy.get('[data-testid="app-linking-command"]').find('input').then(($input) => {
               let linkingCmd = $input.attr('value');
                cy.exec(linkingCmd);
                cy.get('[data-testid="linked-app-name"]').should('be.visible');
                cy.log("Successfully linked the app");
            });
        });
        cy.visit(Cypress.env("baseUrl"));
        cy.cleanupApp(appName, selectedOrgHandle);
    });
});
