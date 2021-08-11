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

import { NO_OF_RETRIES, STANDARD_TIME_OUT, LONG_TIME_OUT } from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Observability tests', () => {
    const obsUrlRegexp = /.+\/observe\/app\/(.{36})\/(.{36})\b/;
    let savedCookies
    let obsId: string
    let version: string

    before(() => {
        cy.consoleUserLogin();
        cy.clearAllTestData();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        });
        cy.visit(Cypress.env("baseUrl") + "/observability");
        cy.get('[data-testid="btn-observability-try-sample"]').should('be.visible').click();
        cy.url({ timeout: LONG_TIME_OUT * 10}).should('contain', '/observe/app/').then(url => {
            let obsUrlRegexMatch = url.match(obsUrlRegexp);
            expect(obsUrlRegexMatch).to.have.lengthOf(3);
            obsId = obsUrlRegexMatch[1];
            version = obsUrlRegexMatch[2];
        });
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.restoreLocalStorage();
        cy.visit(Cypress.env("baseUrl") + '/observe/app/' + obsId + '/' + version + '?isSample=true');
        cy.viewport(1536, 683);
    });

    afterEach(() => {
        cy.saveLocalStorage();
    });

    after(() => {
        cy.userLogout();
    });

    it('test logs view', { retries: NO_OF_RETRIES }, () => {
        const connectionErrorLogEntry = 'error while connecting to the hr-service';
        const employeeInfoNotFoundLogEntry = 'employee information not found in the hr-service';
        // const systemLogEntry = 'ballerina: started publishing metrics to Choreo'
        // const downloadedLogEntry = '[INFO] [ballerina/http] started HTTP/WS listener 0.0.0.0:8090'

        cy.get('[data-testid="panel-Logs-btn"]').should('be.visible');
        cy.get('[data-testid="panel-Logs-btn"]').click();

        cy.log('Asserting mandatory log entry without any filter');
        cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, { timeout: 600000 }).should('exist');

        cy.log('Asserting mandatory log entry by providing a search phrase');
        cy.get('[data-testid="log-search"]').type(connectionErrorLogEntry);
        cy.get('[data-testid="log-search-btn"]').click();
        cy.contains('[data-testid="log-panel"]', connectionErrorLogEntry, { timeout: 600000 }).should('exist');
        cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, { timeout: 600000 }).should('not.exist');

        // TODO: Enable following assertion once https://github.com/wso2-enterprise/choreo/issues/4058 is fixed
        // cy.log('Asserting log download');
        // cy.get('[data-testid="log-search"]').click().clear().type("ballerina");
        // cy.get('[data-testid="log-search-btn"]').click();
        // cy.contains('[data-testid="log-panel"]', systemLogEntry, {timeout: 600000}).should('exist');
        // cy.get('[data-testid="log-download-btn"]').click();
        // cy.readFile('./cypress/downloads/employee-service-logs.txt').should('contain', downloadedLogEntry);
    })

    it('test observability overview', { retries: NO_OF_RETRIES }, () => {
        const employeeInfoNotFoundLogEntry = 'employee information not found in the hr-service';
        const emptyHistogramMessage = 'No requests received during the selected time period';
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
        cy.get('.metrics-text').contains('100% Success', { timeout: 600000 }).should('exist');

        cy.contains('[data-testid="histogram-throughput"]', emptyHistogramMessage).should('not.exist');
        cy.contains('[data-testid="histogram-response-time"]', emptyHistogramMessage).should('not.exist');
        cy.get('[data-testid="histogram-throughput"]').get('g.recharts-layer.recharts-area').should('exist');
        cy.get('[data-testid="histogram-response-time"]').get('g.recharts-layer.recharts-area').should('exist');

        cy.log('Asserting the default log panel');
        cy.get('[data-testid="log-panel"]').should('exist');

        cy.log('Waiting on graphs to be expanded');
        cy.wait(STANDARD_TIME_OUT);
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

            cy.log('Asserting the log panel after clicking on the very first point in the latency graph');
            cy.contains('[data-testid="log-panel"]', employeeInfoNotFoundLogEntry, { timeout: 600000 }).should('not.exist');

            cy.log('Asserting the request list');
            cy.get('[data-testid="request-table"]').should('exist');
            cy.get('[data-testid="request-information"]').its('length').should('be.gte', 1);

            cy.get('[data-testid="request-information"]').eq(0).find('div>div').then(($elements) => {
                expect($elements[0].textContent).to.match(responseTimeRegexp);
                expect($elements[1].textContent).to.contain(':');
                expect($elements[2].textContent).to.be.empty;
            });
            // TODO: Uncomment the following once https://github.com/wso2-enterprise/choreo/issues/4310 is fixed
            // cy.get('[data-testid="request-information"]').eq(1).click().find('div>div').then(($elements) => {
            //     expect($elements[0].textContent).to.match(responseTimeRegexp);
            //     expect($elements[1].textContent).to.contain(':');
            //     expect($elements[2].textContent).to.match(httpStatusCodeRegexp);
            // });
        });
    });

    it('test diagnostics view', { retries: NO_OF_RETRIES }, () => {
        const timestampRegex = /(0[1-9]|[12]\d|3[01])\/(0[1-9]|1[0-2])\/\d{4}\s([01]\d|2[0-3]):([0-5]\d):([0-5]\d)/;
        const numberOfBins = 5;

        cy.log('Waiting for the diagram to be rendered');
        cy.get('.diagram-canvas').should('exist');
        cy.get('[data-testid="diagnostics-view-tab"]').should('be.visible');

        cy.log('Accessing the diagnostics view');
        cy.get('[data-testid="diagnostics-view-tab"]').click().then(() => {
            cy.get('[data-testid="time-interval-loader"]').should('not.exist');
            cy.get('[data-testid="logs-loader"]').should('not.exist');
            cy.get('[data-testid="error-graph-loader"]').should('not.exist');
            cy.get('[data-testid="throughput-graph-loader"]').should('not.exist');
            cy.get('[data-testid="latency-graph-loader"]').should('not.exist');
            cy.get('[data-testid="cpu-graph-loader"]').should('not.exist');
            cy.get('[data-testid="memory-graph-loader"]').should('not.exist');

            cy.log('Asserting the Date/Time and Logs columns');
            for (let i = 0; i < numberOfBins; i++) {
                cy.contains('[data-testid="time-interval-' + i + '"]', timestampRegex).should('exist');
                cy.get('[data-testid="logs-partition-' + i + '"]').should('exist');
            }
            cy.get('[data-testid="time-interval-5"]').should('not.exist');
            cy.get('[data-testid="logs-partition-5"]').should('not.exist');

            cy.log('Verifying whether all the graphs are rendered');
            cy.get('[data-testid="error-graph"]').should('exist');
            cy.get('[data-testid="throughput-graph"]').should('exist');
            cy.get('[data-testid="latency-graph"]').should('exist');
            cy.get('[data-testid="cpu-graph"]').should('exist');
            cy.get('[data-testid="memory-graph"]').should('exist');

            cy.get('[data-testid="diagnostics-view-slider"]').should('be.visible');

            cy.log('Finding the position to move the slider for accessing the flame graph');
            cy.get('[data-testid="bin-divider-1"]').invoke('position').then((d1) => {
                cy.get('[data-testid="bin-divider-2"]').invoke('position').then((d2) => {

                    let middleOfdiv1Ndiv2 = d1.top + Math.round((d2.top - d1.top) / 2);

                    cy.log('Dragging the diagnostics view slider');
                    cy.get('[data-testid="diagnostics-view-slider"]').then(($el) => {
                        cy.wrap($el)
                            .trigger('mousedown', { button: 0 })
                            .trigger('mousemove', { clientX: 0, clientY: middleOfdiv1Ndiv2 })
                            .trigger('mouseup', { force: true })
                    })

                    cy.log('Asserting the flame graph');
                    cy.get('[data-testid="flame-graph-btn"]').should('exist');
                    cy.get('[data-testid="flame-graph-btn"]').click().then(() => {
                        cy.get('[data-testid="flame-graph-loader"]').should('not.exist');
                        cy.get('[data-testid="flame-graph-message-container"]').should('not.exist');

                        cy.get('[data-testid="flame-graph"]').should('exist');
                        cy.get('[data-testid="latencies-for-flame-graph"]').should('exist');
                        cy.get('[data-testid="flame-graph-slider"]').should('be.visible');

                        // TODO: Move the flame graph slider and assert the flame graph once https://github.com/wso2-enterprise/choreo/issues/4310 is fixed

                        cy.log('Close the flame graph and navigate to the diagnostics view again');
                        cy.get('[data-testid="flame-graph-close-btn"]').should('be.visible');
                        cy.get('[data-testid="flame-graph-close-btn"]').click().then(() => {
                            cy.get('[data-testid="cpu-graph-loader"]').should('not.exist');
                            cy.get('[data-testid="cpu-graph"]').should('exist');
                        });
                    });
                });
            });
        });
    })
});
