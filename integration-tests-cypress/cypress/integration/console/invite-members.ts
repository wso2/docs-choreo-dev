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

import { SETTINGS_TEXT } from '../../support/common/constants';

/// <reference types="cypress" />

describe('Invite members', () => {
    let savedCookies;
    const memberEmail = Cypress.env('invitationEmail');
    const groupMemberEmail = 'udyogi@wso2.com';
    const groupMemberName = 'Udyogi Munasinghe';

    before(() => {
        cy.log("Login into Choreo");
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

    it('invite a member', { retries: 2 }, () => {
        cy.get('[data-testid="invite-members-btn"]').click();
        cy.get('[data-testid="group-select"]').invoke('text').then((groupText) => {
            if (groupText == '') {
                cy.get('[data-testid="group-select"]').click();
                cy.contains('Developer').click();
            } else if (groupText == 'admin') {
                cy.get('[data-testid="group-select"]').click();
                cy.contains('Admin').click();
                cy.contains('Developer').click();
            } else if ((groupText == 'admin, developer') || (groupText == 'developer, admin')) {
                cy.contains('Admin').click();
            }

            cy.get('[data-testid="invite-email"]').within(() => {
                cy.get('input').type(memberEmail+'{enter}', { force: true });
            })
            cy.get('[data-testid="invite-btn"]').click({ force: true });
            cy.contains('td', memberEmail).should('be.visible');
            cy.log('Invitation sent successfully');

            cy.log('Deleting member invitation');
            cy.contains('td', memberEmail).trigger('mouseover');
            cy.get('[data-testid="api-delete-btn"]').click();
            cy.get('[data-testid="delete-invitation-btn"]').click();
            cy.log('Invitation deleted successfully');
        })
    })

    it('Add a member to a group', { retries: 2 }, () => {
        // Member should be already in the member list
        cy.contains(groupMemberEmail).should('be.visible');

        cy.get('[data-testid="/user-settings/organization/groups"]').click();
        cy.contains('td', 'Admin').click();
        cy.contains(groupMemberEmail).should('not.exist');
        cy.get('[id="tags-standard"]').click().type(groupMemberName);
        cy.contains('[id="tags-standard-popup"]', groupMemberName).should('be.visible');
        cy.contains('[id="tags-standard-popup"]', groupMemberName).click();
        cy.get('[data-testid="add-member-btn"]').click();
        cy.contains('td', groupMemberEmail).should('be.visible');
        cy.log('Member added to the group successfully');

        cy.log('Removing member from the group');
        cy.contains('tr', groupMemberEmail).within(() => {
            cy.get('[data-testid="api-delete-btn"]').click();
        })
        cy.get('[data-testid="delete-member-btn"]').click();
        cy.log('Member removed from the group successfully');
    })
})
