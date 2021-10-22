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

export class API {


    static createAPI() {
        cy.get('button > span >h5').contains('Create').click()
    }

    static createProxyAPI() {
        cy.get('[data-testid="create-api-from-proxy-btn"]').click()
    }


    static createFromScratch() {
        cy.get('[data-testid="create-api-from-scratch-btn"]').click()
    }

    static designANewRESTAPI(apiName, apiVersion, apiBasePath, endpoint) {
        cy.get('[data-testid="create-api-from-rest-api-btn"]').click()
        cy.get('[data-testid="create-api-status"]').should('exist')
        cy.get('[data-testid="api-name"] input').type(apiName)
        cy.get('[data-testid="api-version"] input').clear().type(apiVersion)
        if (apiBasePath) {
            cy.get('[data-testid="api-basepath"] input').clear().type(apiBasePath)
        }
        cy.get('[data-testid="api-endpoint"] input').clear().type(endpoint)
        cy.wait(2000)
        cy.get('#create-API-from-restEp-btn').should('be.enabled').click()

    }

    static importOpenAPI() {
        cy.get('[data-testid="create-api-from-open-api-btn"]').click()
    }



}