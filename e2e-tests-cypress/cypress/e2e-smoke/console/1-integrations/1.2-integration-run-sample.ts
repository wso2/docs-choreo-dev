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

import {
    INTEGRATIONS_TEXT, FAKE_TWILIO_ACCOUNT_SID, FAKE_TWILIO_TOKEN, FAKE_TWILIO_SENDER_NUMBER,
    FAKE_TWILIO_RECIPIENT_NUMBER, INVITATION_EMAIL, EX_LONG_TIME_OUT 
} from "../../../support/common/constants";
import { appNamePrefix } from "../../../support/common/utils";

describe('Integration sample flow', () => {
    let savedCookies;

    before(() => {
        cy.consoleUserLogin();
        cy.clearAllTestData();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        });
        cy.navigateFromHomePage(INTEGRATIONS_TEXT);
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.restoreLocalStorage();
        cy.visit(Cypress.env("baseUrl") + '/' + INTEGRATIONS_TEXT);
        cy.get('[id="backdrop-loader"]').should('not.exist');

        cy.get('body').then($body => {
            const preBuiltBtnAvailable = ($body.find('[data-testid="use-prebuilt-btn"]').length > 0) ? true : false;
            if (preBuiltBtnAvailable) {
                cy.get('[data-testid="use-prebuilt-btn"]').should('exist').click();
            }
        });
    });

    afterEach(() => {
        cy.saveLocalStorage();
    });

    after(() => {
        cy.userLogout();
    });

    it('clone and edit Google calender to twilio SMS', () => {
        cy.intercept('POST', '**/apps/template', (req) => {
            req.body.displayName = `${appNamePrefix} ${req.body.displayName}`;
        });
        cy.get('[data-testid="gcalendar-to-twilio"]').trigger('mouseover').within(() => {
            cy.contains('Clone & Edit').click({ force: true });
        });
        cy.get('[data-testid="diagram-canvas"]').should('be.visible');
        cy.get('[data-testid="settings-btn"]').click();
        cy.fillCalendarConfigs(INVITATION_EMAIL);
        cy.fillTwilioConfigs(FAKE_TWILIO_ACCOUNT_SID, FAKE_TWILIO_TOKEN, FAKE_TWILIO_SENDER_NUMBER, FAKE_TWILIO_RECIPIENT_NUMBER);
        cy.get('[data-testid="config-save-btn"]').should('be.visible').click();

        cy.testRunApp();
        cy.get('[data-testid="test-url"]').should('exist');
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', {timeout: EX_LONG_TIME_OUT}).should('exist');
        cy.log('Retrieved the test URL successfully');

        cy.url().then((url) => {
            const appName = url.split('app/').pop().split('/test')[0];
            cy.goBacktoAppsList();
            cy.cleanupApp(appName);
        });
    })

    it('test-run sample integration', () => {
        cy.log("Prebuilt integrations page loaded successfully");
        cy.intercept('POST', `**/${Cypress.env("selectedOrgHandle")}/apps`, (req) => {
            req.body.displayName = `${appNamePrefix} ${req.body.displayName}`;
        });
        cy.get('[data-testid="gcalendar-to-twilio"]').should('exist').children().contains('Use this').click({ force: true });

        cy.waitTillWorkSpace();
        cy.get('.diagram-canvas').should('exist');
        cy.fillCalendarConfigs(INVITATION_EMAIL);
        cy.fillTwilioConfigs(FAKE_TWILIO_ACCOUNT_SID, FAKE_TWILIO_TOKEN, FAKE_TWILIO_SENDER_NUMBER, FAKE_TWILIO_RECIPIENT_NUMBER);
        cy.get('[data-testid="config-save-btn"]').should('be.visible').click();

        cy.get('[data-testid="test-url"]').should('exist');
        cy.log('Retrieved the test URL successfully');

        cy.url().then((url) => {
            const appName = url.split('app/').pop().split('/deploy')[0];
            cy.goBacktoAppsList();
            cy.cleanupApp(appName);
        });
    })
})
