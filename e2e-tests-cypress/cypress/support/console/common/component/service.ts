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

// Login > Home > Services
export class Services {


    static createService(serviceName: string) {
        cy.get('button > span >h5').contains('Create').click()
        cy.get('[data-testid="application-name"] div').click()
        cy.get('input[placeholder="Type name"]').type(serviceName)
        cy.get('#create-with-choreo-btn').click()
        this.waitTill()
    }

    static selectService(serviceName: string) {
        const app = `tr > td[value="${serviceName.toLowerCase()}"] >div`
        cy.contains(serviceName).click()
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }

    static deleteService(serviceName: string) {

        this.searchSrevice(serviceName)
        cy.get(`tr > td[value="${serviceName.toLowerCase()}"]`).then($tr => {
            cy.deleteRecord(serviceName)
        })
    }

    static searchSrevice(serviceName: string) {
        cy.get('[data-testid="search-btn"]').trigger('mouseover')
        cy.get('[data-testid="search-app"]').within(() => {
            cy.get('input').type(serviceName.replace(/ /g,'-') + '{enter}')
        })
    }

    private static waitTill() {
        cy.contains('Configure Resource').should('be.visible');
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }
}