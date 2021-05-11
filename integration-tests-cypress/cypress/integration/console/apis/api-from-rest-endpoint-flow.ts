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

import { generateApiName } from "../../../support/common/utils";

describe("API creation from an existing endpoint", () => {

    const API_NAME = generateApiName('CYE2E');
    const API_VERSION = 'V0.0.1';
    const API_ENDPOINT = 'https://api.domainsdb.info/v1/domains';

    before(() => {
        cy.consoleUserLogin();
    });

    it("Create API from existing endpoint and deploy and publish", () => {
        cy.log("Visiting API listing");
        cy.navigateFromHomePage('apis');
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

        cy.verifyApiOverview(API_NAME, API_VERSION);
        cy.wait(5000);

        cy.updateDesignConfiguration();
        cy.wait(5000);

        cy.addApiDocument();
        cy.wait(5000);

        cy.updateRuntimeConfiguration();
        cy.wait(5000);

        cy.log('Visiting and updating resources');
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

        cy.updateSubscriptionPlans();
        cy.wait(3000);

        cy.deployInitialRevision();
        cy.wait(5000);

        cy.testApiInPublisherTestConsole();
        cy.wait(2000);

        cy.publishApi();
        cy.wait(2000);
    })

    it('Deleting the created API', () => {
        cy.contains('API list').click();
        cy.log("Visiting API listing");
        cy.navigateFromHomePage('apis');
        cy.wait(10000);

        cy.searchApiFromListAndVisit(API_NAME);

        cy.get('[data-testid=go-to-dev-portal-btn]').should('be.enabled');
        cy.get('[data-testid=resources-container]').within(() => {
            cy.get('[data-testid="resource-/search"]').should('exist');
        })
        cy.deleteApiFromOverview();
    });
});
