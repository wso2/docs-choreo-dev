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

import { APIM_RESOURCE_PATH, APIS_TEXT, LONG_TIME_OUT, MEDIUM_TIME_OUT, PATH_SEPARATOR, STANDARD_TIME_OUT } from "../../../support/common/constants";
import { generateApiName } from "../../../support/common/utils";

describe("API creation from an existing endpoint", () => {
    const API_NAME = generateApiName('CYE2E');
    const API_VERSION = 'V0.0.1';
    const API_ENDPOINT = 'https://jsonplaceholder.typicode.com';
    const OPERATION_TARGET = '/users';
    let apiId: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const selectedOrgHandle = Cypress.env("selectedOrgHandle");
            const orgId = user?.orgs.find((org) => org.handle === selectedOrgHandle).uuid;
            cy.clearAllTestData(orgId);
        });
    });

    it("Create API from existing endpoint and deploy and publish", () => {
        cy.log("Visiting API listing");
        cy.navigateFromHomePage(APIS_TEXT);

        cy.log("Checking availability of the API list");
        cy.checkApiListAvailabilityAndVisitCreate();

        cy.log('Opening API creation dialog');
        cy.contains('Create API').should('exist');
        cy.get('[data-testid="create-api-from-proxy-btn"]').click();
        cy.get('[data-testid="create-api-from-rest-api-btn"]').click();

        cy.log('Filling API creation form data');
        cy.get('[data-testid=api-name]').type(API_NAME);
        cy.get('[data-testid=api-version]').clear();
        cy.get('[data-testid=api-version]').type(API_VERSION);
        cy.get('[data-testid=api-endpoint]').type(API_ENDPOINT);
        cy.get('#create-API-from-restEp-btn').should('be.enabled');

        cy.intercept({
            method: "POST",
            pathname: APIM_RESOURCE_PATH
        }).as("createApi");

        cy.get('#create-API-from-restEp-btn').click();

        cy.wait('@createApi', { timeout: 20000 }).then((interception) => {
            apiId = interception.response.body[ `id` ];

            cy.verifyApiOverview(API_NAME, API_VERSION);
            cy.updateDesignConfiguration();
            cy.addApiDocument();
            cy.updateRuntimeConfiguration();

            cy.log('Visiting and updating resources');
            cy.get('[data-testid=Resources]', { timeout: STANDARD_TIME_OUT }).click();
            cy.log('Deleting initial resources');
            cy.get('[data-testid=delete-all-operations-btn]').click();
            cy.get('#mui-component-select-verbs').click();
            cy.get('#menu-verbs').within(() => {
                cy.get('.MuiPaper-root > .MuiList-root > :nth-child(1)', { timeout: LONG_TIME_OUT })
                    .contains('GET').click();
            });
            cy.get('body').type('{esc}');
            cy.get('#operation-target').type(OPERATION_TARGET);
            cy.get('[data-testid=add-btn]').click();
            cy.get('button').contains('Save').click();
            cy.get('#circular-loader', { timeout: MEDIUM_TIME_OUT }).should('not.exist');
            cy.updateSubscriptionPlans();
            cy.deployInitialRevision();
            cy.get('[data-testid="api-revision-deploy-successful"]').should("be.visible");
            cy.get('[fill="green"]').should("be.visible");
            cy.testApiInPublisherTestConsole();
            cy.publishApi();
            cy.wait(2000);
        });
    });

    // TODO: add test case to check API delete flow

    after(() => {
        cy.userLogout();
    });
});
