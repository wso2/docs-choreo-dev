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

import { generateApiName, getSelectedOrgHandle } from "../../../support/common/utils";
import { APIM_RESOURCE_PATH, APIS_TEXT, DEVELOP, OVERVIEW, PATH_SEPARATOR } from '../../../support/common/constants';

describe('Choreo APIM publisher scenarios', () => {
    const API_NAME = generateApiName('oas');
    let apiId: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const org = user?.orgs.find((org) => org.handle === getSelectedOrgHandle(user));
            cy.clearAllTestData(org);
        });
    });

    it('Creating and publishing an API from open API specification', () => {
        const filepath = 'console/apis/generation_oas.yaml';

        cy.log("Starting API Creation using open API specification");
        cy.navigateFromHomePage(APIS_TEXT);

        cy.log("Checking availability of the API list");
        cy.checkApiListAvailabilityAndVisitCreate();

        cy.get('[data-testid="create-api-from-proxy-btn"]').click();
        cy.get('[data-testid="create-api-from-open-api-btn"]').click();
        cy.get('[data-testid="open-api-file"]').click();
        cy.get('input[type="file"]').attachFile(filepath);
        cy.get('[id="create-API-from-openAPI-def-btn"]').click();
        cy.get('[data-testid="api-name"]').findByRole('textbox').clear();
        cy.wait(2000);
        cy.get('[data-testid="api-name"]').findByRole('textbox').type(API_NAME);
        cy.get('[data-testid="api-basepath"]').within(() => {
            cy.get('input').clear().type(API_NAME);
        });
        cy.get('[data-testid="api-endpoint"]').within(() => {
            cy.get('p').contains('Mui-error').should('not.exist');
        });

        cy.intercept({
            method: "POST",
            pathname: APIM_RESOURCE_PATH + PATH_SEPARATOR + "import-openapi",
        }).as("createApi");

        cy.get('[id="create-and-publish-api"]').click();

        cy.wait('@createApi', { timeout: 20000 }).then((interception) => {
            apiId = interception.response.body[`id`];

            cy.url().should('include', DEVELOP + OVERVIEW + PATH_SEPARATOR);
            cy.get('[data-testid="go-to-dev-portal-btn"]').should('exist');
            cy.get('[data-testid="go-to-dev-portal-btn"]').should('not.be.disabled');
            cy.get('[data-testid="overview-item-State"]').should('have.text', 'Published');
            cy.log('Successfully created API from open API specification');

            cy.updateEndpointConfiguration('https://api.carbonintensity.org.uk');
            cy.updateSubscriptionPlans();
            cy.createAndDeployRevision();
            // TODO : Enable this test once the tryout 404 error fixed in dev
            // cy.testApiInPublisherTestConsole();
        });
    });

    after(() => {
        cy.userLogout();
    });
});
