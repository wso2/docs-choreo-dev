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
import { ServiceDevelop } from '../../../support/console/common/component/services/service-develop';
import { HTTPMethod } from '../../../support/console/common/component/enums/http-method-enum';
import { ReturnType } from '../../../support/console/common/component/enums/return-type-enum';
import { TestView } from '../../../support/console/common/component/test-view';
import { Deploy } from '../../../support/console/common/component/services/deploy';
import { SwaggerUI } from '../../../support/console/common/swagger-ui-component';
import { CurlComponent } from '../../../support/console/common/curl-component';

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
        ServiceDevelop.configureResources(HTTPMethod.GET, "hello", ReturnType.JSON)
    })

    it('low code form AI suggestions', () => {
        const variableSourceFields = 'string url = "https://postman-echo.com/get"';

        ServiceDevelop.addStatements()
        ServiceDevelop.addVariable("string", urlName, 'https://postman-echo.com/get')
        ServiceDevelop.addAPICalls()

        cy.log('Adding HTTP connector with AI suggestion of previous variable');
        ServiceDevelop.addHTTPConnector(urlName, HTTPMethod.GET, ReturnType.JSON)
        cy.checkSourceCodeForValidation(variableSourceFields);
        cy.log('Data Mapper AI suggestion added to Low Code form successfully!');
    })

    it('test run hello world service', () => {
        ServiceDevelop.addStatements()
        ServiceDevelop.addVariable("var", "res", 'hello world')
        ServiceDevelop.addStatements()
        ServiceDevelop.addResponse("res")

        TestView.navigatTestView()
        TestView.clickTestRunButton()
        TestView.getTestURL().should('not.be.empty')

        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', { timeout: 40000 }).should('exist');
        cy.log('Retrieving the test URL successful');
        CurlComponent.generateRequest(HTTPMethod.GET, "")
    })

    it('Add test view', () => {
        SwaggerUI.SelectResource(HTTPMethod.GET, "/hello")
        SwaggerUI.TryoutAPI()
        SwaggerUI.ExecuteResourceFunction()
        SwaggerUI.GetResponse()
    });

    it('test postman view', () => {
        cy.log('Testing invalid API key validation attempt scenario');
        TestView.executePostmanTest("dummykey")
    });

    it('Deploy hello world service', () => {
        Deploy.navigateDeploy()
        Deploy.deploy()
        CurlComponent.generateRequest(HTTPMethod.GET, "")
        Deploy.undeloy()
        // TODO: Remove wait after fixing https://github.com/wso2-enterprise/choreo/issues/7308

    });

})
