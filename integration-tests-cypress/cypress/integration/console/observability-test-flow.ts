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

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.userLoginWithGmail()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
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
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp(appName, true);
        cy.userLogout()
    })

    it('navigate to sample service observability view', () => {
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(appName);
        cy.url().should('include', 'app/' + appName + '/develop');

        cy.get('[data-testid="observe"]').should('be.visible');
        cy.get('[data-testid="observe"]').click();

        var obsUrl = '';
        cy.window().then((win) => {
            cy.stub(win, 'open').as('windowOpen').callsFake(url => {
                obsUrl = url;
            });
        })

        cy.contains('button', 'Sample Service').click();
        cy.get('@windowOpen').should('be.called');
        cy.wait(4000).then(() => {
            cy.visit(obsUrl.replace('http://', 'https://'))
        })
    })

    it('test logs view', () => {
        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible');
        cy.get('[data-testid="panel-Logs-btn"]').click();
    })

})
