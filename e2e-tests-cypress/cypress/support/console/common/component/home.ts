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

// Loign > Home
export class Home {


    static selectSrvice() {
        cy.get('[data-testid="left-navbar-services-btn"]').click()
    }


    static selectAPI() {
        cy.get('[data-testid="left-navbar-apis-btn"]').click()
    }


    static selectIntegration() {
        cy.get('[data-testid="left-navbar-integrations-btn"]').click()
    }


    /**
     * If user is in multiple organizations, this method selects the given name.
     * Then Choreo will load related services,APIs, and Intg
     * @param orgName - Organization name
     */
    static selectOrganization(orgName: string = "") {
        const configUser = Cypress.env('selectedOrgHandle')
        cy.get('header').then(header=>{
            const uorg = header.find('#org-select')
            if(uorg.length>0){
                if(!orgName && !configUser){
                    cy.get('#current-user p').invoke('text').then(currentUser=>{
                        this.setOrganization(currentUser)
                    })
                }if(orgName){
                    this.setOrganization(orgName)
                }
            }
        })

    }

   

    private static setOrganization(orgName: string) {
        const user = orgName.replace(' ', '').toLowerCase();
        const dvalue = `[data-value=${user}]`
        cy.get('#org-select').click();
        cy.get(dvalue).click()
    }

}