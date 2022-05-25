/// <reference types="cypress" />
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";

describe("Enterprise Login", () => {

    before(() => {
      cy.request(Cypress.env("auth0LogoutUrl"),{"client_id":Cypress.env("auth0ClientID"), "returnTo": Cypress.env("enterpriseLoginUrl")});
    })

    after(()=>{
        ChoreoHomePage.logout()
    })

    it('Enterprise login to console', () => {
        cy.visit(Cypress.env("enterpriseLoginUrl"))
        cy.get('button[id="enterprise-sign-in"]').should("be.visible", { timeout: 180000 });

        cy.get('button[id="enterprise-sign-in"]').click()

        cy.get("#outlined-basic").type(Cypress.env("enterpriseIDPUsername"));
        cy.contains('Continue').click();

        cy.get('input[id="username"]').should("be.visible", { timeout: 180000 });
        cy.get("#username").type(Cypress.env("enterpriseIDPUsername"));
        cy.get("#password").type(Cypress.env("enterpriseIDPPassword"), { log: false });
        cy.contains('Continue').click();


        cy.get('[data-testid="header-user-profile-menu"]', {
            timeout: 180000,
          }).should("be.visible");

        cy.window()
          .its("sessionStorage")
          .invoke("getItem", "sign_out_url")
          .then((url) => {
            Cypress.env("sign_out_url", url);
        });
    })
})

