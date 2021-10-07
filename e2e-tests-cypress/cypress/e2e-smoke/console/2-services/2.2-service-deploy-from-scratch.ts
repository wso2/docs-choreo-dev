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

import { generateAppName, getSelectedOrgHandle } from '../../../support/common/utils';
import { Home } from '../../../support/console/common/component/home';
import { Services } from '../../../support/console/common/component/service';
import { Develop } from '../../../support/console/common/component/develop';
import { HTTPMethod } from '../../../support/console/common/component/enums/http-method-enum';
import { ReturnType } from '../../../support/console/common/component/enums/return-type-enum';
import { TestView } from '../../../support/console/common/component/test-view';
import { Deploy } from '../../../support/console/common/component/deploy';

/// <reference types="cypress" />

describe('Service deployment and delete deployed service', () => {
    let savedCookies: Cypress.Cookie[];
    let appName: string;
    const urlName = "url";

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const org = user?.orgs.find((org) => org.handle === getSelectedOrgHandle(user));
            cy.clearAllTestData(org);
        });
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        });
        appName = generateAppName("app");
        cy.log('app name: ' + appName);
    });

    beforeEach(() => {
        cy.hideWelcomeMessage();
    })

    after(() => {
        cy.userLogout();
    })

    it('crate service', () => {
        Home.selectSrvice()
        Services.createService(appName)
        cy.verifyAppName(appName);
        Develop.configureResources(HTTPMethod.GET, "hello", ReturnType.JSON)
    })

    it('low code form AI suggestions', () => {
        const variableSourceFields = 'string url = "https://postman-echo.com/get"';

        Develop.addStatements()
        Develop.addVariable("string", urlName, 'https://postman-echo.com/get')
        Develop.addAPICalls()

        cy.log('Adding HTTP connector with AI suggestion of previous variable');
        Develop.addHTTPConnector(urlName, HTTPMethod.GET, ReturnType.JSON)
        cy.checkSourceCodeForValidation(variableSourceFields);
        cy.log('Data Mapper AI suggestion added to Low Code form successfully!');
    })




    it('test run hello world service', () => {
        Develop.addStatements()
        Develop.addVariable("var", "res", 'hello world')
        Develop.addStatements()
        Develop.addResponse("res")

        TestView.navigatTestView()
        TestView.clickTestRunButton()
        TestView.getTestURL().should('not.be.empty')

        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', { timeout: 40000 }).should('exist');
        cy.log('Retrieving the test URL successful');

        cy.get('[data-testid="product-tour-log-panel"] input').eq(0).invoke('attr', 'value').then((testUrl) => {
            cy.callExternalEndpoint((testUrl + "/hello"), 3, "hello world");
            cy.log('Successfully invoked test endpoint');
        });
    })

    it('Add test view', () => {
        cy.get('[data-testid="test"]').click();
        cy.log("verify test operation");
        cy.get('[data-testid="backdrop-loader"]').should('not.exist');
        cy.get('.swagger-ui').within(() => {
            cy.get('.opblock-summary').click();
            cy.get('button').contains('Try it out').should('exist').click();
            cy.get('.opblock-section-header').contains('Cancel').should('exist');
            cy.get('.execute-wrapper > .btn').click();
            cy.get('.curl-command').should('exist');
            cy.get('.request-url').should('exist');
            cy.get("div[class='highlight-code'] pre[class=' microlight'] code span").should('have.text', 'hello world')
            cy.log('service response is successfully returned');
            cy.get('tr[class="response"]>td[class="response-col_status"]').should('have.text', '200');
            cy.log('Service Tryout is successful!');

        });
    });

    it('test postman view', () => {
        cy.log('Testing invalid API key validation attempt scenario');
        TestView.executePostmanTest("dummykey")
    });

    it('Deploy hello world service', () => {
        Deploy.navigateDeploy()
        Deploy.deploy()
        // TODO: Remove wait after fixing https://github.com/wso2-enterprise/choreo/issues/7308

    });

})
