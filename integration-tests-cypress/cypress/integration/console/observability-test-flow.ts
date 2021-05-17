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
import { SERVICES_TEXT, NO_OF_RETIRES } from '../../support/common/constants';

/// <reference types="cypress" />

describe('Observability tests', () => {
    const obsUrlRegexp = /.+\/observe\/app\/(.{36})\/(.{36})\b/;
    let savedCookies
    let appName: string
    let obsId: string
    let version: string

    before(() => {
        cy.log("Login into Choreo")
        cy.consoleUserLogin()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })

        appName = generateAppName("app");
        cy.log('App name: ', appName);
        cy.createNewApp(SERVICES_TEXT, appName);
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
        cy.preserveCookiesForTest(savedCookies);
        cy.restoreLocalStorage();
        cy.visit(Cypress.env("baseUrl") + '/observe/app/' + obsId + '/' + version + '?isSample=true');
    });

    afterEach(() => {
        cy.saveLocalStorage();
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteApp("service", appName, true);
        cy.userLogout();
    })

    it('test logs view', { retries: NO_OF_RETIRES }, () => {
        const connectionErrorLogEntry = 'error while connecting to the hr-service';
        const employeeInfoNotFoundLogEntry = 'employee information not found in the hr-service';
        // const systemLogEntry = 'ballerina: started publishing metrics to Choreo'
        // const downloadedLogEntry = '[INFO] [ballerina/http] started HTTP/WS listener 0.0.0.0:8090'

        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible');
        cy.get('[data-testid="panel-Logs-btn"]').click();

        cy.log('Asserting mandatory log entry without any filter');
        cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, {timeout: 600000}).should('exist');

        cy.log('Asserting mandatory log entry by providing a search phrase');
        cy.get('[data-testid="log-search"]').type(connectionErrorLogEntry);
        cy.get('[data-testid="log-search-btn"]').click();
        cy.contains('[data-testid="log-panel"]', connectionErrorLogEntry, {timeout: 600000}).should('exist');
        cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, {timeout: 600000}).should('not.exist');

        // TODO: Enable following assertion once https://github.com/wso2-enterprise/choreo/issues/4058 is fixed
        // cy.log('Asserting log download');
        // cy.get('[data-testid="log-search"]').click().clear().type("ballerina");
        // cy.get('[data-testid="log-search-btn"]').click();
        // cy.contains('[data-testid="log-panel"]', systemLogEntry, {timeout: 600000}).should('exist');
        // cy.get('[data-testid="log-download-btn"]').click();
        // cy.readFile('./cypress/downloads/employee-service-logs.txt').should('contain', downloadedLogEntry);
    })

    it('test observability overview', { retries: NO_OF_RETIRES }, () => {
        const employeeInfoNotFoundLogEntry = 'employee information not found in the hr-service';
        const httpStatusCodeRegexp = /[1-5]\d{2}/;
        const responseTimeRegexp = /\d+\sms/;
        let d;
        let prevY;
        let finalX;
        let finalY;
        cy.get('.diagram-canvas').should('exist');
        cy.get('.worker-line').should('exist');
        cy.get('[data-testid="refresh-btn"]').should('not.exist');
        cy.get('[data-testid="preloader"]').should('not.exist');
        cy.get('.metrics-text').contains('100% Success', {timeout: 600000}).should('exist');

        cy.log('Asserting the default log panel');
        cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, {timeout: 600000}).should('exist');

        cy.get('[data-testid="histogram-throughput"]').get('g.recharts-layer.recharts-area').should('exist');
        cy.get('[data-testid="histogram-response-time"]').get('g.recharts-layer.recharts-area').should('exist');

        cy.get('[data-testid="histogram-response-time"]').find('g.recharts-layer.recharts-area').find('path').then(($path) => {
            cy.log('Getting coordinates to click on the latency graph');
            d = $path.attr('d');
            d = d.replace('Z', '');
            const newD = d.split("L");
            for (const v of newD) {
                const arr = v.split(',');
                if (prevY !== undefined && prevY !== arr[1]) {
                    finalX = arr[0];
                    finalY = arr[1];
                    break;
                }
                prevY = arr[1];
            }

            cy.get('[data-testid="histogram-throughput"]').find('svg').click(Math.round(finalX), Math.round(finalY));
            cy.get('[data-testid="preloader"]').should('not.exist');

            cy.log('Asserting the log panel after clicking on the graph');
            cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, {timeout: 600000}).should('not.exist');

            cy.log('Asserting the request list');
            cy.get('[data-testid="request-table"]').should('be.visible');
            cy.get('[data-testid="request-information"]').its('length').should('be.gte', 1);

            cy.get('[data-testid="request-information"]').eq(0).find('div>div').then(($elements) => {
                expect($elements[0].textContent).to.match(responseTimeRegexp);
                expect($elements[1].textContent).to.contain(':');
                expect($elements[2].textContent).to.be.empty;
            });
            cy.get('[data-testid="request-information"]').eq(1).click().find('div>div').then(($elements) => {
                expect($elements[0].textContent).to.match(responseTimeRegexp);
                expect($elements[1].textContent).to.contain(':');
                expect($elements[2].textContent).to.match(httpStatusCodeRegexp);
            });
         });
    })
})
