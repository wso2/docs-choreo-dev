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

    const API_NAME = 'CyAPI109';
    const API_VERSION = 'V0.0.1';
    const API_ENDPOINT = 'https://api.domainsdb.info/v1/domains';

    before(() => {
        cy.consoleUserLogin();
    });

    it("Create API from existing endpoint and deploy and publish", () => {
        cy.log("Visiting API listing");
        cy.get(':nth-child(5) > a > .MuiButtonBase-root > .MuiListItemIcon-root > div > img').click();
        cy.location('pathname').should('eq', '/apis/');
        cy.wait(10000);

        cy.get('[data-testid=create-api-btn]').click();

        cy.log('Opening API creation dialog');
        cy.contains('Create API').should('exist');
        cy.get('button').contains('Next').first().click({ force: true });

        cy.log('Filling API creation form data');
        cy.get('[data-testid=api-name]').type(API_NAME);
        cy.get('[data-testid=api-version]').clear();
        cy.get('[data-testid=api-version]').type(API_VERSION);
        cy.get('[data-testid=api-endpoint]').type(API_ENDPOINT);
        cy.get('#create-API-from-restEp-btn').should('be.enabled');
        cy.get('#create-API-from-restEp-btn').click();

        // visiting overview
        cy.log('Visiting API overview page');
        cy.get('[data-testid=overview-title]').should('exist');
        cy.get('[data-testid=go-to-dev-portal-btn]').should('not.be.enabled');
        cy.get('[data-testid=api-name-nav-label]').should('have.text', API_NAME);
        cy.get('[data-testid=api-version-nav-label]').should('have.text', '(' + API_VERSION + ')');
        cy.get('[data-testid=resources-container]').should('be.visible', true);

        cy.log('visiting design configuration and update data');
        cy.get('[data-testid="Design Configurations"]').click();
        cy.get('[data-testid=toggle-edit-description]').click();
        cy.get('[data-testid=description-input]').type('Description by automated test runner');
        cy.get('[data-testid=toggle-add-tags]').click();
        cy.get('[data-testid=tag-input]').type('testTag{enter}');
        cy.get('[data-testid=design-config-save-btn]').click();
        cy.wait(5000);

        cy.log('Visiting Documents page');
        cy.get('[data-testid=Documents]').click();
        cy.get('[data-testid=add-new-doc]').click();
        cy.get('[data-testid=page-header]').contains('Add New Document').should('be.visible', true);
        cy.get('[data-testid=document-name]').type('Test document by test runner');
        cy.get('[data-testid=document-summary]').type('Test summary by test runner');
        cy.get('[data-testid=document-url]').type('https://www.example.com/how-to');
        cy.get('[data-testid=create-document]').click();
        cy.wait(1000);
        cy.get('[data-testid=document-wrapper]').should('exist');

        cy.get('[data-testid="Runtime Configurations"]').click();
        cy.get('[data-testid=switch-cors-config]').click();
        cy.get('[data-testid=cors-config-label]').click();
        cy.get('[data-testid=checkbox-allow-all-origins]').click();
        cy.get('[data-testid=addBtn-origin]').click();
        cy.get('[data-testid="type and press enter to add origins"]').type('localhost{enter}');
        cy.get('[data-testid=addBtn-header]').click();
        cy.get('[data-testid="type and press enter to add headers"]').type('tenantId{enter}');
        cy.get('[data-testid=addBtn-method]').click();
        cy.get('[data-testid=access-control-method-select]').click();
        cy.get('[data-testid=access-c-method-TRACE]').click();
        cy.get('[data-testid=access-control-method-select]').click();
        cy.get('[data-testid=access-c-method-CONNECT]').click();
        cy.get('[data-testid=runtime-config-save-btn]').click();
        cy.wait(5000);

        cy.log('Visiting Resources');
        cy.get('[data-testid=Resources]').click();
        cy.log('Deleting initial resources');
        cy.get('[data-testid=delete-all-operations-btn]').click();
        cy.get('#mui-component-select-verbs').click();
        cy.get('#menu-verbs').within(() => {
            cy.get('.MuiPaper-root > .MuiList-root > :nth-child(1)').contains('GET').click();
        })
        cy.get('body').type('{esc}');
        cy.get('#operation-target').type('search');
        cy.get('[data-testid=add-btn]').click();
        cy.get('button').contains('Save').click();
        cy.get('.MuiDialogContent-root').within(() => {
            cy.get('button').contains('Save').click();
        });
        cy.wait(5000);

        cy.log('Visiting subscription page');
        cy.get('[data-testid=Subscriptions]').click();
        cy.get('[data-testid=checkbox-Bronze]').click();
        cy.get('[data-testid=subscription-save-btn]').click();
        cy.wait(3000);

        // visit deploy screen
        cy.log('Visiting deployment tab');
        cy.get('[data-testid=deployments]').click();
        cy.get('[data-testid=create-deploy-revision-btn]').click();
        cy.get('.MuiDialogContent-root').within(() => {
            cy.get('button').contains('Deploy').click();
        });
        cy.wait(5000);

        //visit test console
        cy.log('Visiting test console');
        cy.get('[data-testid=test]').click();

        cy.get('.opblock-summary').click({ force: true });
        cy.get('#operations-default-get_search').within(() => {
            cy.get('button').contains('Try it out').click({ force: true });
            cy.get('.execute-wrapper > .btn').click();
            cy.wait(3000);
            cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
            cy.log('Invoked the API successfully');
        });

        //visit publish tab
        cy.log('Visiting api publishing');
        cy.get('[data-testid=lifecycle-management]').click();
        cy.get('[data-testid=Publish-lc-btn]').click();
        cy.wait(2000);
    })

    it('Deleting the created API', () => {
        cy.contains('API list').click();
        cy.log("Visiting API listing");
        cy.get(':nth-child(5) > a > .MuiButtonBase-root > .MuiListItemIcon-root > div > img').click();
        cy.location('pathname').should('eq', '/apis/');
        cy.wait(10000);

        cy.log('Searching the api from the table');
        cy.get('[data-testid=api-search-btn]').click();
        cy.get('[data-testid=api-search-text-field]').type(API_NAME);
        cy.get('[data-testid=apis-list-table]').within(() => {
            cy.contains(API_NAME).click();
        });

        cy.get('[data-testid=go-to-dev-portal-btn]').should('not.be.enabled');
        cy.get('[data-testid=resources-container]').within(() => {
            cy.get('[data-testid="resource-/search"]').should('exist');
        })
        cy.get('[data-testid=delete-api-btn]').click();
        cy.get('[data-testid=delete-api]').click();
    });
});
