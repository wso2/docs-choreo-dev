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
import { SERVICES_TEXT } from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Performance drill down test', () => {
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
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.undeployApp("service", appName, true);
        cy.deleteApp("service", appName, true);
        cy.userLogout();
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(SERVICES_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello", null, "string ?");
        
        // Create new http connector
        cy.log('creating http connection');

        cy.get('[data-testid=api-options]').click();
        cy.get('[data-testid=http] > .connector-details').click();
        cy.get('.exp-editor > .react-monaco-editor-container > .no-user-select ' +
                '> .overflow-guard > .monaco-scrollable-element > .lines-content ' +
                '> .view-lines > .view-line').click()
        .type('"https://int-test-endpoint-t2puhg9-nilushancosta.dv.choreoapps.dev/hello"');

        cy.get('[data-testid=http-save]').click();
       
        // Add get call to the existing http connector
        cy.get(':nth-child(1) > .main-plus-wrapper > :nth-child(2) > [data-testid=plus-button] ' +
                '> :nth-child(1) > .plus-holder > #SmallPlus > .product-tour-small-plus ' +
                '> #Oval_Copy_15-2 > [r="6"]').click({force: true });

        cy.get('[data-testid=api-options]').click();
        cy.get('.existing-connector-name').click({ force: true });
        cy.get('#combo-box-demo').type('get{enter}');

        // Select payload type to json
        cy.get('[data-testid="Select TypeString"]').click();
        cy.get('[data-testid=connector-payload-json]').click();

        // Save HTTP call
        cy.get('[data-testid=http-save-done]').click();
    });

    it('Performance drill down', () => {
        cy.log('Starting performance drill down test');
        // Open performance forecast view
        cy.get('[data-testid=analyze-btn]').click();

        // Check for "performance forecast not available" element not present
        cy.get('.MuiTypography-root.jss1917.MuiTypography-body1').should('not.exist');

        // Check graph to exist
        cy.get('[data-testid=analyze-graph] > :nth-child(1) > .recharts-wrapper' +
                '> .recharts-surface > .recharts-area > .recharts-layer > .recharts-area-area').should('exist');
    })
})

