/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

/// <reference types="cypress" />


import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { TryOut } from "../../support/devportal/pages/apis/try-out";
import { AppsList } from "../../support/devportal/pages/applications/apps-list";
import { generateAppName } from "../../support/devportal/utils";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";


describe("Application sharing scenario", () => {

    const appName = generateAppName("-e2etest");
    
    before(() => DevportalLoginPage.loginToDevportal());
    after(() => ChoreoHomePage.logout());

    it("Share an application", () => {
        DevPortalHomePage.navigateToAppsPage();
        AppsList.createAnApplication(appName);
        cy.intercept('POST', '**/share*').as('shareApplication');
        cy.get('[data-testid=appliation-share-btn]').click();
        cy.get('[data-testid=share-cancel-button]').click();
        cy.get('[data-cyid=app-share-dialog]').should('not.exist');
        cy.get('[data-testid=appliation-share-btn]').click();
        cy.get('[data-testid=share-button]').should('be.disabled', true);
        cy.get('[data-testid=app-share-user-email]').type("abc@example.com{enter}");

        cy.log('Check the email address validation');
        cy.get('[data-testid=app-share-user-email]').type("errorEmail{enter}");
        cy.get('[data-testid=invalid-email]').should("exist");

        cy.get('[data-testid=app-share-user-email]').type("errorEmail@example.com{enter}");
        cy.get('[data-testid=invalid-email]').should("not.exist");
        cy.get('[data-testid=app-share-email-address]').should("have.length", 2);

        cy.log('Remove newly added email address');
        cy.get('[data-testid="emails-wrapper"]') 
            .find('[data-testid="app-share-email-address"]') 
            .eq(1)
            .find('.MuiChip-deleteIcon')
            .click(); 
        cy.get('[data-testid=app-share-email-address]').should("have.length", 1);

        cy.get('[data-testid=share-button]').should('not.be.disabled');
        cy.get('[data-testid=shared-app-name]').should('have.text', appName);
        cy.get('[data-testid=share-button]').click();
        cy.wait('@shareApplication').its('response.statusCode').should('eq', 200);
        cy.get('[data-cyid=app-share-dialog]').should('not.exist');

        cy.log("Check existing user's email for the shared application");
        cy.get('[data-testid=appliation-share-btn]').click();
        cy.get('[data-testid=app-share-email-address]').should("have.length", 1);
        cy.get('[data-testid=share-cancel-button]').click();

        cy.log("Check whether the shared emails are listed under app overview");
        cy.get('[data-testid=app-share-email-addresses]').should("have.length", 1);

    });

    it("Delete a consumer application", () => {
        TryOut.DeleteApplication(appName);
    });

});
