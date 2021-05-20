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

import {APP_SVC_URL, ORG_NAME, SERVICES_TEXT, SUCCESS_STATUS_CODE} from '../../../support/common/constants';

/// <reference types="cypress" />

describe('Test successful deployment of sample services', ()=>{
    let appName: string

    before(() => {
        cy.log("Login into Choreo using Google");
        cy.consoleUserLogin();
    }),

    after(() => {
        cy.goBacktoAppsList();
        cy.cleanupApp(appName);
        cy.userLogout();
    }),

    it('Test deployment of sample:- echo service', () => {
        //This is the POST call we are interested in capturing to catch the app name handle of the created sample service
        cy.intercept('POST', `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/template`).as('templateCall');

        cy.navigateFromHomePage(SERVICES_TEXT);
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
                url: `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/${appName}/undeploy`,
                timeout: 60000
            }).then((resp) => {
                // Status code is expected to be 200
                expect(resp.status).to.eq(SUCCESS_STATUS_CODE);
                cy.log("Successfully undeployed the app: "+ appName);
                cy.get('[data-testid="deploy"]').should('exist');
            });
        });
    });
});
