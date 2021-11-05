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

export class Deploy{

static navigateDeploy(){
    cy.get('#deploy').click()
    cy.get('[data-testid="backdrop-loader"]').should('not.exist');
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
}

static deploy(){
    cy.get('#deploy-button').click()
    cy.get('#stop-button', { timeout: 1000 * 60 * 8 }).should('exist');
}

static undeloy(){
    cy.get('button[aria-label="close"]').click({ multiple: true })
    cy.get('#stop-button').click()
    cy.get('#deploy-button', { timeout: 1000 * 60 * 8 }).should('exist');
}




}