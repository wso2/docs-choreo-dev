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

export class SwaggerComponent {

    static SelectResource(httpMethod: string, path: string){
        
        const pathVariable = `[data-path="${path}"]`;
        cy.get('.swagger-ui').within(() => {
                cy.get(pathVariable).click();
        });      
    }

    static TryoutAPI(){
        cy.get('[id*="operations-"] button').contains('Try it out').should('exist').click();
        cy.get('.opblock-section-header').contains('Cancel').should('exist'); 
    }
    
    static ExecuteResourceFunction(){
        cy.get('.execute-wrapper').click();
        cy.log('Execution is successful');
    }

    static GetResponse(){

        cy.get('.curl-command').should('exist');
        cy.get('.request-url').should('exist');
        cy.log('Response is successfully returned');
        cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
        cy.log('API Tryout is successful!');
    }
}