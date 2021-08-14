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

import { SERVICES_TEXT, EX_LONG_TIME_OUT, NO_OF_RETRIES } from "../../../support/common/constants";
import { appNamePrefix } from "../../../support/common/utils";

/// <reference types="cypress" />

describe("Test successful deployment of sample services", () => {
    before(() => {
        cy.consoleUserLogin().then((user) => {
            const selectedOrgHandle = Cypress.env("selectedOrgHandle");
            const orgId = user?.orgs.find((org) => org.handle === selectedOrgHandle).uuid;
            cy.clearAllTestData(orgId);
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
        cy.url().then((url) => {
            const appName = url.split("app/").pop().split("/test")[0];
            cy.goBacktoAppsList();
            cy.cleanupApp(appName);
        });
    });
});
