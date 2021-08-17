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
import { generateAppName } from '../../../support/common/utils';
import { SERVICES_TEXT } from '../../../support/common/constants';
import { datamapperRequestBody } from '../../../fixtures/console/ai/datamapper-service-call-data';
import { datamapperExpectedResponseBody } from '../../../fixtures/console/ai/datamapper-service-call-data';
import { dataMapperTestURL } from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Data Mapper service call Test', () => {
    let savedCookies
    let appName: string

    before(() => {
        cy.consoleUserLogin();
        cy.clearAllTestData();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    })

    after(() => {
        cy.userLogout();
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(SERVICES_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello", "POST", "string ?");
    });

    afterEach(() => {
        cy.goBacktoAppsList();
        cy.undeployApp("service", appName, true);
        cy.deleteApp("service", appName, true);
    });

    it('Datamapper service call', () => {
        cy.log('Starting Datamapper service call');

        cy.request('POST', dataMapperTestURL + '/map/1.0.0', datamapperRequestBody).then(
            (response) => {

                cy.log("Response from Data Mapper service: " + JSON.stringify(response.body));
                expect(response.body).to.deep.equal(datamapperExpectedResponseBody)
            });

        cy.log("Expected response received successfully!");
    })
})
