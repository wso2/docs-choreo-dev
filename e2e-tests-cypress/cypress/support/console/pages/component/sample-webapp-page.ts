/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

export class SampleWebAppPage {
    static visitSampleWebsite() {
        cy.origin(Cypress.env("webAppUrl"), () => {
            cy.visit('/');
            cy.contains('button', 'Login').click();
        });
    }

    static submitLoginCredentials() {
        cy.url().then((url) => {
            let uri = new URL(url.toString());
            let hostname = uri.hostname;
            cy.origin(hostname, () => {
                cy.get('input[id="username"]').should("be.visible");
                cy.get('input[id="username"]').type("e2e-webapp-user");
                cy.get('input[id="password"]').type("acme2@123");
                cy.contains('button', 'Sign In').click();
            });
        });
    }

    static verifyLoginAndLogout() {
        cy.origin(Cypress.env("webAppUrl"), () => {
            cy.get("[data-cyid=welcome-msg-box]").contains('john1@acme.org');
            cy.contains('button', 'Logout').click();
        });
    }

}
