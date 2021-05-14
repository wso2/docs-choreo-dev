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
import { DEVELOP, OVERVIEW, PATH_SEPARATOR } from "../../../support/console/apis/constants";
import { APIS_TEXT } from '../../../support/common/constants';

describe('Choreo APIM publisher scenarios', () => {
    const API_NAME = generateApiName('oas');

    before(() => {
        cy.log("Login to Choreo using github");
        cy.consoleUserLogin();
    });

    it('Creating and publishing an API from open API specification', () => {
        const filepath = 'console/apis/generation_oas.yaml';
        cy.log("Starting API Creation using open API specification");
        cy.navigateFromHomePage(APIS_TEXT);
        cy.get('[data-testid="create-api-btn"]').click({ force: true });
        cy.wait(3000);
        cy.get('[data-testid="upload-open-api-definition"]').click();
        cy.get('[data-testid="open-api-file"]').click();
        cy.get('input[type="file"]').attachFile(filepath);
        cy.get('[id="create-API-from-openAPI-def-btn"]').click();
        cy.get('[data-testid="api-name"]').findByRole('textbox').clear();
        cy.wait(2000);
        cy.get('[data-testid="api-name"]').findByRole('textbox').type(API_NAME);
        cy.get('[id="create-and-publish-api"]').click();
        cy.url().should('include', DEVELOP + OVERVIEW + PATH_SEPARATOR);
        cy.get('[data-testid="go-to-dev-portal-btn"]').should('exist');
        cy.get('[data-testid="go-to-dev-portal-btn"]').should('not.be.disabled');
        cy.get('[data-testid="overview-item-State"]').should('have.text', 'Published');
        cy.get('[data-testid="overview-item-Business Plans"]').should('have.text', 'Unlimited');
        cy.log('Successfully created API from open API specification');

        cy.updateEndpointConfiguration('https://api.carbonintensity.org.uk');
        cy.updateSubscriptionPlans();
        cy.createAndDeployRevision();
        cy.testApiInPublisherTestConsole();
    });

    after(() => {
        cy.wait(30000);
        cy.get('[data-testid="api-list"]').click({ force: true });
        cy.wait(2000);
        cy.searchApiFromListAndVisit(API_NAME);
        cy.deleteApiFromOverview();
        cy.log("Logout from Choreo");
        cy.userLogout();
    });
});
