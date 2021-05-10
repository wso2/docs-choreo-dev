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

describe("API creation from an existing endpoint", () => {
    before(() => {
        cy.consoleUserLogin();
    });

    it("select API page", () => {
        cy.log("Visit API listing");
        cy.get(':nth-child(5) > a > .MuiButtonBase-root > .MuiListItemIcon-root > div > img').click();
        cy.location('pathname').should('eq', '/apis/');
        cy.wait(5000);
        cy.get('[data-testid=api-table]').then(() => {  
            cy.get('[data-testid=create-api-btn]').click();
        });

        cy.contains('Create API').should('exist');
        cy.get('button').contains('Next').first().click({ force: true })

        cy.get('[data-testid=api-version] > .MuiInputBase-root > .MuiInputBase-input').clear();
        cy.get('[data-testid=api-name] > .MuiInputBase-root > .MuiInputBase-input').type('CyAPI2');
        cy.get('[data-testid=api-version] > .MuiInputBase-root > .MuiInputBase-input').type('V0.0.1');
        cy.get('[data-testid=api-endpoint] > .MuiInputBase-root > .MuiInputBase-input').type('https://api.domainsdb.info/v1/domains');

        cy.get('#create-API-from-restEp-btn').should('be.enabled');
        cy.get('#create-API-from-restEp-btn').click();

        // cy.get('[data-testid=api-search-btn]').click();
        // cy.get('.jss1688 > .MuiTypography-root').contains('Create API').should('exist');
        // cy.get(':nth-child(1) > .MuiGrid-grid-md-12 > .jss1951 > :nth-child(1) > .jss1960 > .jss1957 > .jss1958 > :nth-child(2) > .jss1954 > [data-testid=upload-rest-definition]')
        // cy.get('input').get('[placeholder=Search]').type('e2eTest_api_dv');

        // cy.get('[data-testid=create-api-btn]').click(); //to be removed
        // cy.contains('Create API').should('exist'); //to be removed

        // cy.get('button').contains('Next').first().click({force: true})
        // cy.get(':nth-child(1) > .MuiGrid-grid-md-12 > .jss1830').within(() => {
        //     cy.get('button').contains('Next').click({force: true});
        // });

        

        
        // cy.get('[data-testid=upload-rest-definition]').first().click({force: true});


        cy.log('Visit Resources');
        cy.get('[data-testid=Resources]').click();

        cy.get('#mui-component-select-verbs').click();
        cy.get('#menu-verbs > .MuiPaper-root > .MuiList-root').within(() => {
            cy.get('#menu-verbs > .MuiPaper-root > .MuiList-root > :nth-child(1)').click();
            // cy.get('#menu-verbs > .MuiPaper-root > .MuiList-root > :nth-child(2)').click();
        });

        cy.get('#menu-verbs').click();
        cy.get('#operation-target').type('search');
        cy.get('[data-testid=add-btn]').click();

        cy.get(':nth-child(2) > .jss2888 > .MuiCollapse-entered > :nth-child(1) > :nth-child(1) > #panel1a-content > :nth-child(1) > .MuiGrid-direction-xs-column > :nth-child(3) > .MuiPaper-root > #panel2a-header').click();
        // cy.get(':nth-child(3) > .jss2888 > #panel1a-header').click();

        cy.get('button').contains('Save').click();

        cy.get('.MuiDialogContent-root').within(() => {
            cy.get('button').contains('Save').click();
        })


        // visit deploy screen
        cy.get('[data-testid=deployments]').click();
        cy.get('button').contains('Create A Revision And Deploy').click();

        cy.get('.MuiDialogContent-root').within(() => {
            cy.get('button').contains('Deploy').click();
        })
        cy.wait(10000);

        //visit test console
        cy.get('[data-testid=test]').click();

    })
});