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


export class VSSourceControl {
  static commitChanges(commitMessage: string) {
    cy.wait(2000)
    cy.get('[aria-label="Changes"] .resource-group').click()
    cy.get('[title="Stage All Changes"]').should('be.visible').click();
    cy.get('[aria-label="Changes"] div[class="count"] div').invoke('text').should('eq', '0');
    cy.get('div[class="view-line"]').should('be.visible').eq(0).click().type(`{backspace}{backspace}${commitMessage}`).wait(10000)
    cy.get('[title="Commit"]').should('be.visible').eq(0).click();
    cy.get('[aria-label="Staged Changes"]', { timeout: 120000 }).should('not.exist');
  }
}
