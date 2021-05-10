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
import { generateAppName, servicesText } from '../../../support/common/utils';

/// <reference types="cypress" />

describe('Service test run and deployment', () => {
    let savedCookies
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google")
        cy.consoleUserLogin()
        cy.getCookies().then((cookies) => {
            savedCookies = cookies
        })
    })

    after(() => {
        cy.userLogout()
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        appName = generateAppName("app");
        cy.log('app name: ', appName);
        cy.createNewApp(servicesText, appName);
        cy.url().should('include', 'app/' + appName + '/develop');
        cy.configureResource("hello");
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty("var", "res", '"hello world"');
        cy.createRespond("res");
    });

    afterEach(() => {
        cy.goBacktoAppsList();
        cy.undeployApp("service", appName, true);
        cy.deleteApp("service", appName, true);
    });

    it('test run hello world service', () => {
        cy.testRunApp();

        cy.get('[data-testid="test-url"]').should('exist');
        cy.contains('[data-testid="log-panel"]', 'started HTTP/WS listener', {timeout: 600000}).should('exist');
        cy.log('Retrieving the test URL successful');
        
        cy.get('[data-testid="test-url"]').invoke('text').then((testUrl) => {
            cy.callExternalEndpoint((testUrl + "/hello"), 3, "hello world");
            cy.log('Successfully invoked test endpoint');
        });
    });

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
    })
})

describe('Test successful deployment of sample services', ()=>{
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google");
        cy.consoleUserLogin();
    }),

    after(() => {
        cy.goBacktoAppsList();
        cy.cleanupApp("service",appName,false);
        cy.userLogout();
    }),

    it('Test deployment of sample:- echo service', () => {
        let appSvcUrl = Cypress.env("appSvcURL");
        let orgName = Cypress.env("selectedOrgHandle");

        //This is the POST call we are interested in capturing to catch the app name handle of the created sample service
        cy.intercept('POST', `${appSvcUrl}/orgs/${orgName}/apps/template`).as('templateCall');

        cy.visit('/services');
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.get('#try-out-samples-btn').should('exist').click();
        cy.get('[data-testid="echo-service"]').should('exist').children().find('button').click({force:true});

        // Trying to capture the template call
        cy.wait('@templateCall',{timeout:30000}).then((interception) => {
            appName = interception.response.body[`name`];
            cy.deployToChoreo("service", appName);

            // Undeploy app via REST API call, because with UI the filtration of app name is not possible for samples.
            cy.request({
                method: "POST",
                url: `${appSvcUrl}/orgs/${orgName}/apps/${appName}/undeploy`,
                timeout: 60000
            }).then((resp) => {
                // Status code is expected to be 200
                expect(resp.status).to.eq(200);
                cy.log("Successfully undeployed the app: "+ appName);
                cy.get('[data-testid="deploy"]').should('exist');
            });
        });
    });
});
