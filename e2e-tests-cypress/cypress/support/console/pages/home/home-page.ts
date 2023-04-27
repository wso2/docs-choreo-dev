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


import { LoginPage } from "../login-page";


export class ChoreoHomePage {

    static navigateToHome() {
        const { handle } = Cypress.env("userData");
        cy.get(`div[class*="choreo-header"]>div>a[href="/organizations/${handle}/home"]`).click();
    }

    static navigateToMarketPlace() {
        cy.get('[data-testid="main-left-nav-item-Marketplace"]').click({ force: true })
    }

    static navigateToProjects() {
        cy.get('[data-testid="main-left-nav-item-Project"]').click();
    }

    static navigateToComponents() {
        cy.get('[data-testid="main-left-nav-item-Components"]').click()
    }

    static navigateToInsights() {
        cy.get('[data-testid="main-left-nav-item-Insights"]').click();
    }

    static isOrgHandleVisible(orgHandle: string) {
        cy.get('[id="org-picker"]').click();
        cy.get('[data-value="' + orgHandle + '"]');
    }

    static logout() {
        cy.request(Cypress.env("sign_out_url"));
    }

    static navigateToSettings() {
        cy.get("#backdrop-loader").should("not.exist");
        cy.get('[data-testid="header-user-profile-menu"]').click();
        cy.get('[data-testid="header-user-profile-item-settings"]')
            .should("be.visible")
            .contains("Settings")
            .click();
    }

    static switchOrganization() {
        if (Cypress.env("isPrivateOrg")) {
            cy.get("#org-picker").click();
            cy.get(`[data-value="${Cypress.env("privateOrgName")}"]`).click();
            LoginPage.persistApimToken();
        }
    }

    static changeToAPIPerspective() {
        cy.get('#perspective-picker').click();
        cy.get('.MuiList-root')
            .should("be.visible")
            .get(`[data-value="apim"]`).click();
    }

    static changeToIDevPerspective() {
        cy.get('[data-testid="perspective-pickerapim"]').click();
        cy.get('.MuiList-root')
            .should("be.visible")
            .get(`[data-value="idevp"]`).click();
    }

}
