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

/// <reference types="cypress" />

describe('Service deployment and delete deployed service', () => {
    let savedCookies;
    let appName: string;
    const urlName = "url";

    before(() => {
        cy.consoleUserLogin();
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        });

        appName = generateAppName("app");
        cy.log('app name: '+ appName);
        cy.createNewApp(SERVICES_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello", null, "string ?");
    });

    after(() => {
        cy.userLogout();
    })

    it('low code form AI suggestions', () => {
        const variableSourceFields = 'string url = "https://postman-echo.com/get"';

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty("string", urlName, '"https://postman-echo.com/get"');

        cy.log('Adding HTTP connector with AI suggestion of previous variable');
        cy.get('[id="SmallPlus"]').eq(0).click({force: true});
        cy.get('[data-testid="api-options"]').click();
        cy.get('[data-testid="http"]').click();
        cy.get('.exp-editor').click().type('{selectall}{del}' + urlName);
        cy.get('body').type('{enter}', { force: true });
        cy.get('[data-testid="http-save-next"]').click();
        cy.log("HTTP connector added successfully!");

        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        cy.checkSourceCodeForValidation(variableSourceFields);
        cy.log('Data Mapper AI suggestion added to Low Code form successfully!');
    })

    it('test run hello world service', () => {
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty("var", "res", '"hello world"');
        cy.createRespond("res");

        cy.testRunApp();

        cy.get('[data-testid="test-url"]').should('exist');
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', { timeout: 600000 }).should('exist');
        cy.log('Retrieving the test URL successful');

        cy.get('[data-testid="test-url"]').invoke('text').then((testUrl) => {
            cy.callExternalEndpoint((testUrl + "/hello"), 3, "hello world");
            cy.log('Successfully invoked test endpoint');
        });
    })

    it('test postman view', () => {
        cy.get('[data-testid="test"]').click();
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.url().should('include', 'app/' + appName + '/test');

        cy.log('Testing invalid API key validation attempt scenario');
        cy.get('[data-testid="postman"]').should('exist');
        cy.get('[data-testid="postman"]').eq(0).click();
        cy.get('[data-testid="click-here"]').should('exist');
        cy.get('[data-testid="click-here"]').click();
        cy.get('[data-testid="api-key"]').should('exist');
        cy.get('[data-testid="api-key"]').type('dummyapikey');
        cy.get('[data-testid="api-key-error"]').should('exist');
        cy.log('Test phase successful!');
    });

    it('Deploy hello world service', () => {
        cy.deployToChoreo("service", appName);
        cy.wait(120000);
    });

    it('Undeploy from UI and delete the service', () => {
        cy.goBacktoAppsList();
        cy.deleteAppWithoutUndeploy(appName, true);
        cy.undeployApp("service", appName, true);
        cy.deleteApp("service", appName, true);
    });
})
