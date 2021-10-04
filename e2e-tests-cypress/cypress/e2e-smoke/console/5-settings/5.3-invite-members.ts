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

import { SETTINGS_TEXT, INVITATION_EMAIL } from '../../../support/common/constants';
import { OrganizationComponent } from '../../../support/console/common/organizations-component';

/// <reference types="cypress" />

describe('Invite members', () => {
    let savedCookies;

    before(() => {
        cy.consoleUserLogin();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        })
    })

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.navigateFromHomePage(SETTINGS_TEXT);
        cy.get('[role="progressbar"]').should('not.exist');
    })

    after(() => {
        cy.userLogout();
    })

    it('invite a member', () => {
        OrganizationComponent.inviteMembers(INVITATION_EMAIL,"developer");
        OrganizationComponent.selectPendingInvitation()
        cy.contains('td', INVITATION_EMAIL).should('be.visible');
        OrganizationComponent.deleteRecord(INVITATION_EMAIL); 
        
    });
})
