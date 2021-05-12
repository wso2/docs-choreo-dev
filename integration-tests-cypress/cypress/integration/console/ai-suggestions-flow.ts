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
import { SERVICES_TEXT } from '../../support/common/constants';

/// <reference types="cypress" />

describe('Data Mapper AI suggestion', () => {
    before(() => {
        cy.log("Login into Choreo")
        cy.consoleUserLogin()
    })

    after(() => {
        cy.userLogout()
    })

    it('low code form AI suggestions', () => {
        cy.log("Starting Data Mapper Low code form AI suggestion test...");
        const appName = generateAppName("datamapper");
        const urlName = "url"
        cy.createNewApp(SERVICES_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("test");
        cy.createVariableProperty("string", urlName, '"https://postman-echo.com/get"');

        cy.log('Adding HTTP connector with AI suggestion of previous variable');
        cy.get('[id="SmallPlus"]').eq(0).click();
        cy.get('[id=Plus_a]').eq(0).click({force: true});
        cy.get('[data-testid="api-options"]').click();
        cy.get('[data-testid="http"]').click();
        cy.get('.exp-editor').click().type('{selectall}{del}' + urlName);
        cy.get('body').type('{enter}', {force: true});
        cy.contains('label', 'GET').click();
        cy.get('[data-testid="http-save-next"]').click();
        cy.contains('No Payload').click();
        cy.get('[data-testid="http-save-done"]').click();
        cy.log("HTTP connector added successfully!");

        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        const variableSourceFields = 'http:Client httpEndpoint = check new (url);';
        cy.checkSourceCodeForValidation(variableSourceFields);
        cy.log('Data Mapper AI suggestion added to Low Code form successfully!');
        cy.goBacktoAppsList();
        cy.undeployApp("service", appName, true);
        cy.deleteApp("service", appName, true);
    })
})
