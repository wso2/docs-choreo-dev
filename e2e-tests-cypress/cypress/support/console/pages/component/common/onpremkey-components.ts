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

import { Utils } from '../../../utils';

export class OnPremkeyComponent {
  static generateOnPremKey() {
    cy.get('[data-cy="/settings/onpremkeys"]').click();
    cy.get('[data-testid="generate-on-prem-key-btn"]').should('exist').click();
    const keyName = Utils.generateKeyName('key');
    cy.get('[data-testid="generate-key-dialog-content"]').should('exist');
    cy.get('[data-testid=on-prem-key]').type(keyName);
    cy.get('[data-testid="delete-on-prem-key-generate-btn"]').click();
    cy.get('[data-testid="generate-key-dialog-content"]').should('not.exist');
    cy.get('[data-testid="copy-key-dialog-content"]').should('be.visible');
    cy.get('[data-testid="delete-on-prem-key-cancel-btn"]')
      .should('exist')
      .click();
  }

  static editOnPremKey() {
    let keyName = Utils.generateKeyName('key');

    cy.get('tbody').should('be.visible');
    cy.contains(keyName).parent().find('[data-testid="key-edit-btn"]').click();
    cy.get('[data-testid="edit-key-dialog-content"]').should('exist');
    keyName += 'New';
    cy.get('[data-testid=on-prem-key]').clear();
    cy.get('[data-testid=on-prem-key]').type(keyName);
    cy.get('[data-testid="delete-on-prem-key-save-btn"]').click();
  }

  static regenerateOnPremKey() {
    const keyName = Utils.generateKeyName('key');
    cy.get('tbody').should('be.visible');
    cy.contains(keyName)
      .parent()
      .find('[data-testid="key-regenerate-btn"]')
      .click();
    cy.get('[data-testid="regenerate-key-dialog-content"]').should('exist');
    cy.get('[data-testid="regenerate-on-prem-key-regenerate-btn"]')
      .should('exist')
      .click();
    cy.get('[data-testid="copy-key-dialog-content"]').should('be.visible');
    cy.get('[data-testid="copy-key-close-btn"]').should('exist').click();
  }

  static deleteOnPremKey() {
    const keyName = Utils.generateKeyName('key');
    cy.get('tbody').should('be.visible');
    cy.contains(keyName)
      .parent()
      .find('[data-testid="key-delete-btn"]')
      .click();
    cy.get('[data-testid="delete-key-dialog-content"]').should('exist');
    cy.get('[data-testid="delete-on-prem-key-delete-btn"]')
      .should('exist')
      .click();
  }
}
