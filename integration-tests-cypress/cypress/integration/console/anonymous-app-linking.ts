/// <reference types="cypress" />

import {generateAppName} from "../../support/common/choreo-utils";

describe('anonymous-app-linking', () => {
    let savedCookies;

    before(() => {
        cy.log("Login into Choreo using Google");
        cy.consoleUserLogin();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        });
    });

    after(() => {
        cy.userLogout();
    });

    it('linking-app', () => {
        let env = Cypress.env("choreoEnv");
        const appName = generateAppName("anon");
        cy.log("Start running in anonymous mode");
        cy.exec(`export CHOREO_ENV=${env} && ./cypress/applinking_test/run_annonapp.sh`).then(result => {
            const obsUrl = /visit (http[^\s]+)/i.exec(result.stdout)[1].replace('http://','https://');
            cy.log("Retrieved Observability URL for the anonymous app: " + obsUrl);
            cy.visit(obsUrl);
            cy.contains('Add to Choreo').should('be.visible').click();
            cy.get('[data-testid="link-app-name"]').should('be.visible');
            cy.log("Created a Choreo app for connection:- " + appName);
            cy.get('[data-testid="link-app-name"]').type(appName);
            cy.get('[data-testid="link-app-next-btn"]').click();
            cy.log("Linking the anonymous app to: "+ appName);

            cy.get('[data-testid="app-linking-command"]').find('input').then(($input) => {
               let linkingCmd = $input.attr('value');
                cy.exec(linkingCmd);
                cy.get('[data-testid="linked-app-name"]').should('be.visible');
            });
        });
        cy.visit("/");
        cy.deleteApp(appName, false, true);
    });
});
