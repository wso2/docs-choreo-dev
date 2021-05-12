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

import { generateAppName } from '../../support/common/utils';
import { servicesText } from '../../support/common/constants';

/// <reference types="cypress" />

describe('Observability tests', () => {
    const obsUrlRegexp = /.+\/observe\/app\/(.{36})\/(.{36})\b/;
    let savedCookies
    let appName: string
    let obsId: string
    let version: string

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.consoleUserLogin()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })

        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(servicesText, appName);
        cy.url().should('include', 'app/' + appName + '/develop');

        cy.get('[data-testid="observe"]').should('be.visible');
        cy.get('[data-testid="observe"]').click();

        let obsUrl: string
        cy.window().then((win) => {
            cy.stub(win, 'open').as('windowOpen').callsFake(url => {
                obsUrl = url;
            });
        })

        cy.get('[data-testid="skip-sample-service-btn"]').should('be.visible');
        cy.get('[data-testid="skip-sample-service-btn"]').click();

        cy.get('[data-testid="sample-service-popup"]').should('not.exist');
        
        cy.get('[data-testid="sample-service-accessor"]').should('be.visible');
        cy.get('[data-testid="sample-service-accessor"]').click();

        cy.get('[data-testid="try-sample-service-btn"]').should('be.visible');
        cy.get('[data-testid="try-sample-service-btn"]').click();

        cy.get('@windowOpen').should('be.called');
        cy.wait(1000).then(() => {
            let obsUrlRegexMatch = obsUrl.match(obsUrlRegexp);
            expect(obsUrlRegexMatch).to.have.lengthOf(3);
            obsId = obsUrlRegexMatch[1];
            version = obsUrlRegexMatch[2];
        })
    })
    
    beforeEach(() => {
        savedCookies.map((cookie) => {
            cy.preserveCookiesForTest(savedCookies);
        })

        cy.visit('/observe/app/' + obsId + '/' + version);
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp("service", appName, true);
        cy.userLogout();
    })

    it('test logs view', () => {
        const defaultLogEntry = 'error while connecting to the hr-service';
        const logEntryToBeSearched = 'employee information not found in the hr-service';
        const systemLogEntry = 'ballerina: started publishing metrics to Choreo'
        const downloadedLogEntry = '[INFO] [ballerina/http] started HTTP/WS listener 0.0.0.0:8090'

        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible');
        cy.get('[data-testid="panel-Logs-btn"]').click();

        cy.log('asseting mandatory log entry without any filter');
        cy.contains('[data-testid="log-panel"]', defaultLogEntry, {timeout: 600000}).should('exist');

        cy.log('asseting mandatory log entry by providing a search phrase');
        cy.get('[data-testid="log-search"]').type(logEntryToBeSearched);
        cy.get('[data-testid="log-search-btn"]').click();
        cy.contains('[data-testid="log-panel"]', logEntryToBeSearched, {timeout: 600000}).should('exist');
        cy.contains('[data-testid="log-panel"]', defaultLogEntry, {timeout: 600000}).should('not.exist');

        cy.log('asseting log download');
        cy.get('[data-testid="log-search"]').click().clear().type("ballerina");
        cy.get('[data-testid="log-search-btn"]').click();
        cy.contains('[data-testid="log-panel"]', systemLogEntry, {timeout: 600000}).should('exist');
        cy.contains('button', 'Download').click();
        cy.get('[data-testid="log-download-btn"]').click();
        cy.readFile('./cypress/downloads/employee-service-logs.txt').should('contain', downloadedLogEntry);
    })
})
