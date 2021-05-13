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
    const memberEmail = Cypress.env('username');

    before(() => {
        cy.log("Login into Choreo")
        cy.consoleUserLogin()
    })

    after(() => {
        cy.userLogout()
    })

    afterEach(() => {
        cy.log('Deleting member invitation');
        cy.contains('td', memberEmail).trigger('mouseover');
        cy.get('[data-testid="api-delete-btn"]').click();
        cy.get('[data-testid="delete-invitation-btn"]').click();
        cy.log('Invitation deleted successfully');
    })

    it('invite a member to developer group', () => {
        cy.navigateFromHomePage(SETTINGS_TEXT);
        cy.get('.MuiCircularProgress-circle').should('not.exist');
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

            cy.get('[data-testid="invite-email"]').children().children().children('input').click({ force: true }).type(memberEmail+'{enter}', { force: true });
            cy.get('[data-testid="invite-btn"]').click();
            cy.contains('td', memberEmail).should('be.visible');
            cy.log('Invitation sent successfully');
        })
    })
})
