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
import { generateAppName } from '../../support/common/choreo-utils';

/// <reference types="cypress" />

describe('Observability tests', () => {
    let savedCookies
    let appName: string
    let obsUrl: string

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.userLoginWithGmail()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })

        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(appName);
        cy.url().should('include', 'app/' + appName + '/develop');

        cy.get('[data-testid="observe"]').should('be.visible');
        cy.get('[data-testid="observe"]').click();

        cy.window().then((win) => {
            cy.stub(win, 'open').as('windowOpen').callsFake(url => {
                obsUrl = url.replace('http://', 'https://');
            });
        })

        cy.contains('button', 'Sample Service').click();
        cy.get('@windowOpen').should('be.called');
        cy.wait(1000).then(() => {
            cy.visit(obsUrl)
        })
    })
    
    beforeEach(() => {
        savedCookies.map((cookie) => {
            cy.setCookie(cookie.name, cookie.value, {
                domain: cookie.domain,
                expiry: cookie.expires,
                httpOnly: cookie.httpOnly,
                path: cookie.path,
                secure: cookie.secure
            })

            Cypress.Cookies.defaults({
                preserve: cookie.name
            })
        })

        cy.visit(obsUrl)
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp(appName, true);
        cy.userLogout()
    })

    it('test logs view', () => {
        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible');
        cy.get('[data-testid="panel-Logs-btn"]').click();
        cy.get('[data-testid="log-panel"]').should('be.visible');

        cy.log('asseting mandatory log entry without any filter');
        cy.get('span').contains('error while connecting to the hr-service');

        cy.log('asseting mandatory log entry by providing a search phrace');
        cy.get('[id="log-search"]').type('employee information not found in the hr-service');
        cy.contains('button', 'Apply').click();
        cy.get('[data-testid="log-panel"]').should('be.visible');
        cy.get('span').contains('employee information not found in the hr-service');
        cy.get('span').contains('error while connecting to the hr-service').should('not.exist');

        cy.log('asseting log download');
        cy.get('[id="log-search"]').click().clear().type("ballerina");
        cy.contains('button', 'Apply').click();
        cy.get('[data-testid="log-panel"]').should('be.visible');
        cy.contains('button', 'Download').click();
        cy.readFile('./cypress/downloads/employee-service-logs.txt').should('contain', '[INFO] [ballerina/http] started HTTP/WS listener 0.0.0.0:8090');
    })

})
