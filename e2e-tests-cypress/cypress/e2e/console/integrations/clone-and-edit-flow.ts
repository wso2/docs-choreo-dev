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
    FAKE_TWILIO_RECIPIENT_NUMBER, INVITATION_EMAIL
} from "../../../support/common/constants";

describe('Clone and edit integrations', () => {
    let savedCookies;

    before(() => {
        cy.log("Login into Choreo");
        cy.consoleUserLogin();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        });
        cy.navigateFromHomePage(INTEGRATIONS_TEXT);
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.restoreLocalStorage();
        cy.visit(Cypress.env("baseUrl") + '/' + INTEGRATIONS_TEXT);
    });

    afterEach(() => {
        cy.saveLocalStorage();
    });

    after(() => {
        cy.userLogout();
    });

    it('clone and edit Google calender to twilio SMS', () => {
        cy.get('[data-testid="use-prebuilt-btn"]').click();
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
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', {timeout: 180000}).should('exist');
        cy.log('Retrieving the test URL successful');

        cy.url().then((url) => {
            let appName = url.split('app/').pop().split('/develop')[0];

            cy.goBacktoAppsList();
            cy.cleanupApp(appName);
        });
    })
})
