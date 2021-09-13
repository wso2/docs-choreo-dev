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

import { SERVICES_TEXT, EX_LONG_TIME_OUT } from "../../../support/common/constants";
import { appNamePrefix, getSelectedOrgHandle } from "../../../support/common/utils";

/// <reference types="cypress" />

describe("Test successful deployment of sample services", () => {
    let selectedOrgHandle: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            selectedOrgHandle = getSelectedOrgHandle(user);
            const org = user?.orgs.find((org) => org.handle === selectedOrgHandle);
            cy.clearAllTestData(org);
        });
    });

    after(() => {
        cy.userLogout();
    });

    it("Test deployment of sample:- echo service", () => {
        cy.navigateFromHomePage(SERVICES_TEXT);
        cy.get('[id="backdrop-loader"').should("not.exist");
        cy.get('[data-testId="try-out-samples-btn"]').should("exist").click({ force: true });
        cy.log("Creating echo service!");
        cy.intercept('POST', '**/apps/template', (req) => {
            req.body.displayName = `${appNamePrefix} ${req.body.displayName}`;
        });

        cy.get('[data-testid="echo-service"]').should("exist").children().find("button").click({ force: true });
        cy.wait(10000);
        cy.get('[data-testid="diagram-loader"]').should("not.exist");
        cy.get('[data-testid="diagram-canvas"]').should("be.visible");
        cy.log("Created the echo service successfully!");
        cy.testRunApp();
        cy.get('[data-testid="test-url"]').should("exist");
        cy.contains('[data-testid="log-panel"]', "started HTTP/WS listener", { timeout: EX_LONG_TIME_OUT }).should(
            "exist"
        );
        cy.log("Tested the echo service successfully!");

        cy.get('.swagger-ui').within(() => {
            cy.get('.opblock-summary').click();
            cy.get('button').contains('Try it out').should('exist').click();
            cy.get('.opblock-section-header').contains('Cancel').should('exist');
            cy.get('.body-param').should('exist').type('Hello World'); 
            cy.get('.execute-wrapper > .btn').click();
            
            cy.get('.curl-command').should('exist');
            cy.get('.request-url').should('exist');
            cy.get("div[class='highlight-code'] pre[class=' microlight'] code span").should('have.text', 'Hello World');
            cy.log('service response is successfully returned');
            cy.get(':nth-child(1) > .responses-table > tbody > .response > .response-col_status').should('have.text', '200');
            cy.log('Service Tryout is successful!');

        });

        cy.url().then((url) => {
            const appName = url.split("app/").pop().split("/test")[0];
            cy.goBacktoAppsList();
            cy.cleanupApp(appName, selectedOrgHandle);
        });
    });
});
