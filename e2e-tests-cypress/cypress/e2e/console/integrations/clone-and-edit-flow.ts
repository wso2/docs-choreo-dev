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

import { INTEGRATIONS_TEXT } from "../../../support/common/constants";

describe('Clone and edit integrations', () => {
    let savedCookies;
    const gmailAccount = Cypress.env("invitationEmail");
    const fakeTwilioAccountSID = Cypress.env("fakeTwilioAccountSID");
    const fakeTwilioToken = Cypress.env("fakeTwilioToken");
    const fakeTwilioSenderNumber = Cypress.env("fakeTwilioSenderNumber");
    const fakeTwilioRecipientNumber = Cypress.env("fakeTwilioRecipientNumber");

    before(() => {
        cy.log("Login into Choreo");
        cy.consoleUserLogin();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        })
    })

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
    })

    after(() => {
        cy.userLogout();
    })

    it('clone and edit Google calender to twilio SMS', () => {
        cy.navigateFromHomePage(INTEGRATIONS_TEXT);
        cy.get('[data-testid="use-prebuilt-btn"]').click();
        cy.get('[data-testid="gcalendar-to-twilio"]').trigger('mouseover').within(() => {
            cy.contains('Clone & Edit').click({ force: true });
        });
        cy.get('[data-testid="diagram-canvas"]').should('be.visible');
        cy.get('[data-testid="settings-btn"]').click();
        cy.fillCalendarConfigs(gmailAccount);
        cy.fillTwilioConfigs(fakeTwilioAccountSID, fakeTwilioToken, fakeTwilioSenderNumber, fakeTwilioRecipientNumber);
        cy.get('[data-testid="config-save-btn"]').should('be.visible').click();
        cy.url().then((url) => {
            let appName = url.split('app/').pop().split('/develop')[0];
            cy.deployToChoreo("integration", appName);

            cy.goBacktoAppsList();
            cy.undeployAppViaRESTAPICall(appName);
            cy.cleanupApp(appName);
        });
    })
})
