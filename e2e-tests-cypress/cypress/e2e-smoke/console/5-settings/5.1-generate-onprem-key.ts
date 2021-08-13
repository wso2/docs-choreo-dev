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

import { generateKeyName } from '../../../support/common/utils';
import { SETTINGS_TEXT } from "../../../support/common/constants";

/// <reference types="cypress" />

describe('Generate on-prem keys', () => {

    before(() => {
        cy.consoleUserLogin();
        cy.clearAllTestData();
        cy.navigateFromHomePage(SETTINGS_TEXT);
    })

    after(() => {
        cy.userLogout();
    })

    it('generate on-prem key', () => {
        cy.get('[data-testid="on-prem-keys-tab"]').click();
        cy.get('[data-testid="generate-onprem-key-btn"]').should('exist').click();
        let keyName = generateKeyName("key");
        cy.get('[data-testid="generate-key-dialog-content"]').should('exist');
        cy.get('[data-testid=api-name]').type(keyName);
        cy.get('[data-testid="generate-key-generate-btn"]').click();
        cy.get('[data-testid="generate-key-dialog-content"]').should('not.exist');
        cy.get('[data-testid="copy-key-dialog-content"]').should('be.visible');
        cy.get('[data-testid="copy-key-close-btn"]').should('exist').click();

        //Edit on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().find('[data-testid="key-edit-btn"]').click();
        cy.get('[data-testid="edit-key-dialog-content"]').should('exist');
        keyName += "New";
        cy.get('[data-testid=api-name]').clear();
        cy.get('[data-testid=api-name]').type(keyName);
        cy.get('[data-testid="edit-key-save-btn"]').click();

        //Regenerate on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().find('[data-testid="key-regenerate-btn"]').click();
        cy.get('[data-testid="regenerate-key-dialog-content"]').should('exist');
        cy.get('.MuiDialogContent-root').findByText('Regenerate').click();
        cy.get('[data-testid="copy-key-dialog-content"]').should('be.visible');
        cy.get('[data-testid="copy-key-close-btn"]').should('exist').click();

        //Delete on-prem key
        cy.get('tbody').should('be.visible');
        cy.contains(keyName).parent().find('[data-testid="key-delete-btn"]').click();
        cy.get('[data-testid="delete-key-dialog-content"]').should('exist');
        cy.get('.MuiDialogContent-root').findByText('Delete').click();
    })
})
