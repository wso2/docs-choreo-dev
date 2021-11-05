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
     FAKE_TWILIO_ACCOUNT_SID, FAKE_TWILIO_TOKEN, FAKE_TWILIO_SENDER_NUMBER,
    FAKE_TWILIO_RECIPIENT_NUMBER, EX_LONG_TIME_OUT 
} from "../../../support/common/constants";
import {  getSelectedOrgHandle } from "../../../support/common/utils";
import { Home } from "../../../support/console/common/component/home";
import { Integration } from "../../../support/console/common/component/integration";
import { GCTwillioIntegration } from "../../../support/console/common/component/integrations/google-calendar-event-to-twillio-sms";
import { Deploy } from "../../../support/console/common/component/services/deploy";
import { TestView } from "../../../support/console/common/component/test-view";





describe('Integration sample flow', () => {
    let savedCookies: Cypress.Cookie[];
    let selectedOrgHandle: string;
    const INVITATION_EMAIL="test.user.choreo@gmail.com"

    before(() => {
        cy.consoleUserLogin().then((user) => {
            selectedOrgHandle = getSelectedOrgHandle(user);
            const org = user?.orgs.find((org) => org.handle === selectedOrgHandle);
            cy.clearAllTestData(org);
        });
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        });
       Home.selectIntegration()
       Home.selectOrganization()
       Integration.selectSample()
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.restoreLocalStorage();

     });

    afterEach(() => {
        cy.saveLocalStorage();
    });

    after(() => {
        cy.userLogout();
    });

    it('clone and edit Google calender to twilio SMS', () => {
         GCTwillioIntegration.cloneEdit()
         GCTwillioIntegration.configureSettings(INVITATION_EMAIL,FAKE_TWILIO_ACCOUNT_SID, FAKE_TWILIO_TOKEN, FAKE_TWILIO_SENDER_NUMBER, FAKE_TWILIO_RECIPIENT_NUMBER)  
    })

    it('test-run sample integration', () => {
        TestView.navigatTestView()
        TestView.clickTestRunButton()
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', {timeout: EX_LONG_TIME_OUT}).should('exist');
        TestView.clickTestRunButton()
    })



    it('Deploy integration',()=>{
        Deploy.navigateDeploy()
        Deploy.deploy()
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', {timeout: EX_LONG_TIME_OUT}).should('exist');
        Deploy.undeloy()
        Home.navigateBackToIntegration()
        Integration.clearIntegrations()
    })
})
