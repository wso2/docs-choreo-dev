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
    let savedCookies
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.consoleUserLogin()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(SERVICES_TEXT, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello");
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty("var", "res", '"hello world"');
        cy.createRespond("res");
    });

    after(() => {
        cy.goBacktoAppsList();
        cy.deleteAppWithoutUndeploy(appName, true);
        cy.userLogout()
    })

    it('deploy hello world service', () => {
        cy.deployToChoreo("service", appName);
    })
})
