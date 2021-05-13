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

import { generateKeyName } from '../../support/common/utils';
import {SETTINGS_TEXT} from "../../support/common/constants";

/// <reference types="cypress" />

describe('Generate on-prem keys', () => {
    let keyName: string

    before(() => {
        cy.log("Login into Choreo using Google");
        cy.consoleUserLogin();
        cy.navigateFromHomePage(SETTINGS_TEXT);
    })

    after(() => {
        cy.userLogout();
    })

    it('generate on-prem key', () => {
        cy.contains('On-prem Keys').click();
        cy.contains('button', 'Generate Key').should('exist').click();
        keyName = generateKeyName("key");
        cy.contains('Generate On-prem Key').should('exist');
        cy.contains('Key name').siblings().children().get('input').type(keyName);
        cy.get('[data-testid="api-name"]').siblings().children().contains('Generate').click();
        cy.contains('Generate On-prem Key').should('not.exist');
        cy.contains('Copy On-prem Key').should('be.visible');
        cy.get('[aria-label="close"]').eq(0).should('exist').click();

        //Edit on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().find('[data-testid="api-delete-btn"]').eq(0).click();
        cy.contains('Edit On-prem Key').should('exist');
        keyName += "New";
        cy.get('[data-testid=api-name] > .MuiInputBase-root > .MuiInputBase-input').clear();
        cy.get('[data-testid=api-name] > .MuiInputBase-root > .MuiInputBase-input').type(keyName);
        cy.get('[data-testid="api-name"]').siblings().children().contains('Save').click();

        //Regenerate on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().findByText('Regenerate').click();
        cy.contains('Regenerate Key').should('exist');
        cy.get('.MuiDialogContent-root').findByText('Regenerate').click();
        cy.contains('Copy On-prem Key').should('be.visible');
        cy.get('[aria-label="close"]').eq(1).should('exist').click();

        //Delete on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().find('[data-testid="api-delete-btn"]').eq(1).click();
        cy.contains('Delete Key').should('exist');
        cy.get('.MuiDialogContent-root').findByText('Delete').click();
    })
})
