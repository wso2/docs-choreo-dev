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

import { getApiName } from "../../support/devportal/utils";
import { SwaggerUI } from '../../support/console/common/swagger-ui-component';
import { eq } from "cypress/types/lodash";

describe('Credentials generation & API tryout scenario', () => {

    const apiName = getApiName();

    beforeEach(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    afterEach(() => {
        cy.devportalLogout();
    });

    it('Generate credentials and tryout the API', () => {
        cy.log("Navigating to credentials tab");
        cy.wait(5000);
        cy.get('[data-testid="credentials-item-link"]').click();
        cy.url().should('include', '/credentials');
        cy.log("Successfully navigated to credentials tab");

        cy.log("Generating credentials")
        cy.get('.MuiSelect-root').click({ force: true });
        cy.get('.MuiList-root > [tabindex="0"]').click()
        cy.get('[data-testid="generate-creds-btn"]').click();
        cy.wait(6000);
        cy.get('[data-testid="generate-access-token-btn"]').should('exist');
        cy.log("Successfully generated credentials");

        cy.log("Navigating to Tryout tab");
        cy.get('[data-testid="tryout-item-link"]').click();
        cy.url().should('include', '/tryout');
        cy.log("Successfully navigated to tryout tab");

        cy.wait(4000);
        cy.log("Generating an access token");
        cy.get('[data-testid="application-selector"]').click();
        cy.get('body #menu- div ul li').eq(0).click();
        cy.get('[data-testid="get-test-key-btn"]').should('be.enabled').click();
        cy.get('[data-testid=accessTokenInput]').should('not.be.empty');
        cy.log("Successfully generated an access token");
        cy.wait(4000);

        cy.log("Invoking the API");
        SwaggerUI.SelectResource("GET","/v3/covid-19/states");
        SwaggerUI.TryoutAPI();
        
        cy.get('tr[data-param-name="sort"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('todayCases').should('have.value', 'todayCases');
            });
        });
        cy.get('tr[data-param-name="yesterday"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('true').should('have.value', 'true');
            });
        });
        cy.get('tr[data-param-name="allowNull"]').within(() => {
            cy.get('.parameters-col_description').within(() => {
                cy.get('select').select('0').should('have.value', '0');
            });
        });
        SwaggerUI.ExecuteResourceFunction();
        SwaggerUI.GetResponse();
   
    });

    it('Remove generated credentials', () => {
        cy.log("Navigating to Credentials tab to remove credentials");
        cy.wait(5000);
        cy.get('[data-testid="credentials-item-link"]').click();
        cy.url().should('include', '/credentials');
        cy.getByTestId("keys-info-cell").should("not.exist");
        cy.get('[data-testid="remove-creds-btn"]').click();
        cy.get('[data-testid="remove-creds-confirmation-ok"]').click();
        cy.wait(4000);
        cy.getByTestId("keys-info-cell").should("be.visible");
        cy.log("Successfully removed credentials");
    });
});
