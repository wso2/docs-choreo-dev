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

import { Utils } from "../../../utils";

export class OnPremkeyComponent {
  static keyName = Utils.generateKeyName("key");

  static navigateToOpPremKeySettings() {
    cy.get('[data-cy="/onpremkeys"]').click();
  }

  static generateOnPremKey() {
    cy.wait(5000);
    cy.get('[title="Generate Key"]').should("exist").click();
    cy.get('[data-testid="on-prem-key"]').should("exist");
    cy.get("[data-testid=on-prem-key]").type(OnPremkeyComponent.keyName);
    cy.get('[data-testid="delete-on-prem-key-generate-btn"]').click();
    cy.get('[data-testid="on-prem-key"]').should("not.exist");
    cy.get('[data-testid="on-prem-key-copy-btn"]', { timeout: 120000 }).should(
      "be.visible"
    );
    cy.get('[aria-label="close"]').should("exist").click();
  }

  static editOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(OnPremkeyComponent.keyName)
      .parent()
      .find('[data-testid="key-edit-btn"]')
      .click();
    cy.get('[data-testid="on-prem-key"]').should("exist");
    OnPremkeyComponent.keyName += "New";
    cy.get("[data-testid=on-prem-key]").clear();
    cy.get("[data-testid=on-prem-key]").type(OnPremkeyComponent.keyName);
    cy.get('[data-testid="delete-on-prem-key-save-btn"]').click();
  }

  static regenerateOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(OnPremkeyComponent.keyName)
      .parent()
      .find('[data-testid="key-regenerate-btn"]')
      .click();
    cy.get('[data-testid="regenerate-on-prem-key-regenerate-btn"]')
      .should("exist")
      .click();
    cy.contains("Copy on-premises key").next().click();
  }

  static deleteOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(OnPremkeyComponent.keyName)
      .parent()
      .find('[data-testid="key-delete-btn"]')
      .click();
    cy.get('[data-testid="delete-on-prem-key-delete-btn"]')
      .should("exist")
      .click();
  }

 
}
