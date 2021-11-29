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

export class APIPublish{


    static navigateToAPIPublish(){
        cy.get('[data-testid="lifecycle-management"] > div').click()
        cy.get('[id="backdrop-loader"]').should('not.exist');
    }

    static publishAPI(){
        cy.get('button > span > h5').contains('Publish').click()
        cy.contains('Successfully updated API Lifecycle state').should('be.visible')
        cy.get('[data-testid="go-to-dev-portal-btn"]').should('be.enabled')
        cy.contains('Lifecycle state has changed from CREATED to PUBLISHED').should('be.visible')
    }
}