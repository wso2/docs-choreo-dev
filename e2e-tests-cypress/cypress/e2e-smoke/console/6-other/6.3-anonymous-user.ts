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

/// <reference types="cypress" />

import { generateKeyName } from '../../../support/common/utils';
import { APIS_TEXT, DEVELOP, OVERVIEW, PATH_SEPARATOR } from '../../../support/common/constants';

if (Cypress.env("isDev")) {
    describe('Try choreo as anonymouse user', () => {

        const API_NAME = 'Anonymous_user_api';

        before(() => {
            cy.log('Initiating anonnymous login');
            cy.visit(Cypress.env('baseUrl'));
            cy.get('#anonymous-user-login').click()
        });

        after(() => {
            cy.userLogout();
        });

        it('verify Settings page and APIs functionality', () => {
            const filepath = 'console/apis/generation_oas.yaml';
            cy.hideWelcomeMessage();

            cy.get('[id="current-user"]').click({ force: true });
            cy.getByTestId("user-settings-btn").click({ force: true });

            // Check organization tab is disabled
            cy.getByTestId('organization-tab').should('be.disabled')
            cy.getByTestId('organization-tab').should('be.disabled')

            // Go to each tab
            cy.getByTestId('connections-tab').click()
            cy.getByTestId('configurations-tab').click()
            cy.getByTestId('testing-tab').click()

            // Generate on prem key just Validate Settings page is working
            cy.getByTestId('on-prem-keys-tab').click()
            cy.getByTestId("generate-onprem-key-btn").should('exist').click();
            let keyName = generateKeyName("key");
            cy.getByTestId("generate-key-dialog-content").should('exist');
            cy.getByTestId("api-name").type(keyName);
            cy.getByTestId("generate-key-generate-btn").click();
            cy.getByTestId("generate-key-dialog-content").should('not.exist');
            cy.getByTestId("copy-key-dialog-content").should('be.visible');
            cy.getByTestId("copy-key-close-btn").should('exist').click();

            //Create and publish an OAS API 
            cy.log("Starting API creation using choreo service");
            cy.navigateFromHomePage(APIS_TEXT);
            cy.getByTestId("create-api-from-scratch-btn").should('be.visible').click({ force: true });
            cy.get('button').contains('Next').first().click({ force: true });
            cy.log('Filling API creation form data');
            cy.getByTestId("create-api-from-open-api-btn").click();
            cy.getByTestId("open-api-file").click();
            cy.get('input[type="file"]').attachFile(filepath);
            cy.get('[id="create-API-from-openAPI-def-btn"]').click();
            cy.getByTestId("api-name").findByRole('textbox').should('be.visible').clear();
            cy.getByTestId("api-name").findByRole('textbox').wait(500).type(API_NAME);
            cy.getByTestId("api-basepath").within(() => {
                cy.get('input').clear().type(API_NAME);
            });
            cy.get('[id="create-and-publish-api"]').click();
            cy.url().should('include', DEVELOP + OVERVIEW + PATH_SEPARATOR);

            // Assert that go to devportal button is not visible
            cy.getByTestId("go-to-dev-portal-btn").should('not.exist');
            cy.log("Devportal button is not visible");
            cy.getByTestId("overview-item-State").should('have.text', 'Published');
            cy.getByTestId("lifecycle-management").click();
            cy.getByTestId("Block-lc-btn").should('exist');

            // Assert that go to devportal button is not visible
            cy.getByTestId("go-to-dev-portal-btn").should('not.exist');
            cy.log("Devportal button is not visible");
            cy.getByTestId("api-list").click({ force: true });

            // Assert that go to devportal button is not visible
            cy.get('[id="goto-devportal-home-btn"]').should('not.exist');
            cy.log("Devportal button is not visible");
        });
    });
}
