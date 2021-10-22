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


export class APIDevelop {
    static configureRuntimeOptions(isCORSenable: boolean, isAllOriginsAllowed: boolean, allowedOrigins: string[], allowedHeaders: string[], allowedMethods: string[]) {
        cy.get('[data-testid="Runtime Configurations"]').click()
        if (isCORSenable) {
            cy.get('[data-testid="switch-cors-config"]').click()
            cy.get('[data-testid="cors-config-label"]').click()
            if (!isAllOriginsAllowed) {
                cy.get('[data-testid="checkbox-allow-all-origins"]').click()
            }
            this.addAllowedOrigins(allowedOrigins)
            this.addAllowedAccessControlHeaders(allowedHeaders)
            this.addAllowedAccessMethods(allowedMethods)
            cy.get('[data-testid="runtime-config-save-btn"]').click()
            cy.contains('Successfully updated the runtime configurations.').should('be.visible')
        }

    }


    private static addAllowedOrigins(origins: string[]) {
        if (origins.length > 0) {
            cy.get('[data-testid="addBtn-origin"]').click()
            origins.forEach(ori =>
                cy.get('[placeholder="Type and press enter to add Origins"]').type(`${ori}`).wait(1000).type('{enter}')
            )
        }
    }

    private static addAllowedAccessControlHeaders(headers: string[]) {
        if (headers.length > 0) {
            cy.get('[data-testid="addBtn-header"]').click()
            headers.forEach(meth =>
                cy.get('[placeholder="Type and press enter to add Headers"]').type(`${meth}`).wait(1000).type('{enter}')
            )
        }

    }

    private static addAllowedAccessMethods(methods: string[]) {
        if (methods.length > 0) {
            cy.get('[data-testid="addBtn-method"]').click()
            methods.forEach(meth => {
                cy.get('[aria-labelledby="demo-mutiple-name-label"]').click()
                cy.get(`[data-value=${meth.toUpperCase()}]`).click()
                cy.wait(1000)
            })

        }
    }
    static addResources(path: string, ...verbs) {
        cy.get('[data-testid="Resources"').click()
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.get('[data-testid="delete-all-operations-btn"]').click()
        this.addHTTPVerb(verbs)
        cy.get('#operation-target').type(path)
        cy.get('[data-testid="add-btn"]').click()
        cy.get('button > span > h5').contains('Save').click()
        cy.contains('Successfully updated the definition.').should('be.visible')
    }

    private static addHTTPVerb(verbs: string[]) {
        cy.get('#mui-component-select-verbs').click()

        verbs.forEach(verb => {
            cy.contains(verb.toUpperCase()).click()
            cy.wait(1000)
        })
        cy.get('body').type('{esc}')
    }

    public static updateSubscriptionPlan(...plans) {
        cy.get('[data-testid="Subscriptions"]').click()
        cy.get('[data-testid="checkbox-Bronze"]').click()
        plans.forEach(plan => {
            let pln = `[data-testid="checkbox-${plan}"]`
            cy.get(pln).click()
        })
        cy.get('button > span > h5').contains('Save').click()
        cy.contains('Successfully updated the subscription plans.').should('be.visible')
    }
}