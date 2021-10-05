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

import { HTTPMethod } from "./enums/http-method-enum";
import { ReturnType } from "./enums/return-type-enum";

export class Develop {

    static configureResources(httpMethod: HTTPMethod, uri: string, returnType: ReturnType) {
        this.selectHTTPMethod(httpMethod)
        cy.get('[data-testid="api-path"]').click()
        cy.get('[placeholder="Relative path from host"]').type(uri)
        this.setReturnType(returnType)
        cy.get('[data-testid="save-btn"]').click()
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }


    private static selectHTTPMethod(httpMethod: HTTPMethod) {
        cy.get('[aria-haspopup="listbox"]').click()
        cy.get(`li[data-value="${httpMethod}"]`).click()
    }

    private static setReturnType(returnType: ReturnType) {
        cy.get('#listReturnTypes').invoke('text').then(text => {
            cy.log(text)
            if (text !== returnType) {
                cy.get('#listReturnTypes button').click()
                this.addReturnType(returnType)
            }
        })
    }

    static addReturnType(returnTyp: ReturnType) {
        cy.contains('Add Return Type').click()
        cy.get('[aria-haspopup="listbox"] span').contains('Select').click()
        const returntType = `li[data-value="${returnTyp}"]`
        cy.get(returntType).click()
        cy.get('[data-testid="api-return-save-btn"]').click()
    }
    static navigateServiceList() {
        cy.contains('Service list').click()
    }



    static addStatements() {
        this.addSmallPlus()
        cy.contains('Statements').click()

    }

    static addAPICalls() {
        this.addSmallPlus()
        cy.contains('API Calls').click()
    }

    private static addSmallPlus() {
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        cy.get('#canvas').then(val => {
            const f = val.find('#SmallPlus')
            if (f.length > 0) {
                cy.log(f.length.toString())
                cy.wrap(f[0]).click()
            }
        })
    }


    static addVariable(varType: string, varName: string, expression: string) {
        cy.contains('Variable').click()
        cy.get('[data-testid="undefinedvar"]').click()
        cy.get(`li[data-value="${varType}"]`).click()
        cy.get('[data-testid="variable-name"]').click()
        cy.get('[placeholder="Enter variable name"]').type(varName)
        cy.typeOnNthExpressionEditor(0, expression, true)
        this.saveStatements()
    }
    static addResponse(response) {
        cy.contains('Respond').click()
        cy.typeOnNthExpressionEditor(0, response, true)
        this.saveStatements()
    }
    static addHTTPConnector(urlName: string, httpMethod: HTTPMethod, payloadType: ReturnType) {
        cy.get('[data-testid="http"]').click();
        cy.get('.exp-editor').click().type(`{selectall}{del} ${urlName}`);
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.get('body').type('{enter}', { force: true });
        this.invokeAPI()
        cy.get('[role="combobox"]').click().within
        cy.get('#combo-box-demo').type(`${httpMethod.toLowerCase()}{enter}`)
        cy.get('[data-testid="Select TypeString"]').click()
        cy.get(`li[data-value="${payloadType.toUpperCase()}"]`).click()
        cy.get('[data-testid="http-save-done"]').click()
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }

    private static saveStatements() {
        cy.get('[data-testid="save-btn"]').click()
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }

    private static invokeAPI() {
        cy.contains('Continue to Invoke API').click()
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
    }
}