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

import { APIS_TEXT, APP_SVC_URL, SELECTED_ORG_HANDLE, SERVICES_TEXT } from '../../../support/common/constants';
import { appNamePrefix } from '../../../support/common/utils';

describe('API creation from choreo service', () => {
    let serviceName: string;
    let applicationId: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const org = user?.orgs.find((org) => org.handle === SELECTED_ORG_HANDLE);
            cy.clearAllTestData(org);
        });
    });

    it('Creating and trying out an API from choreo service', () => {
        cy.log("Starting API creation using choreo service");
        cy.navigateFromHomePage(SERVICES_TEXT);

        // Intercepting the service creation call to capture the randomized service name
        cy.intercept('POST', `${APP_SVC_URL}/orgs/${SELECTED_ORG_HANDLE}/apps/template`,(req) => {
            req.body.displayName = `${appNamePrefix} ${req.body.displayName}`;
        }).as('createService');

        cy.get('[data-testId="try-out-samples-btn"]', { timeout: 60000 }).should('exist').click();
        cy.get('[data-testid="worldbank-data-to-covid19-statistics"]').should('exist').children().find('button').click({ force: true });

        cy.wait('@createService', { timeout: 60000 }).then((interception) => {
            serviceName = interception.response.body[`name`];
            applicationId = interception.response.body[`id`];
            cy.log(`serviceName: ${serviceName}`);
            cy.log(`applicationId: ${applicationId}`);
            const displayName = interception.response.body[`displayName`];
            const apiName = displayName.replace(/\s+/g, '').replace(/-/g, '_');

            // Deploy a sample service
            cy.url().should('include', `app/${serviceName}/develop`);
            cy.get('[data-testid="editor-run-btn"]').should('exist');
            cy.deployToChoreo("service", serviceName);
            cy.log('Service deployed successfully!');
            // TODO: Remove wait after fixing https://github.com/wso2-enterprise/choreo/issues/7308
            cy.wait(2.5 * 60 * 1000);

            cy.goBacktoAppsList();
            cy.navigateFromHomePage(APIS_TEXT);
            cy.searchApiFromListAndVisit(apiName);
            cy.get('[data-testid="backdrop-loader"]').should('not.exist');
            cy.verifyApiOverview(apiName, "1.0.0");
            cy.updateRuntimeConfiguration();

            // Tries out the API proxies the sample service
            cy.wait(2000);
            cy.get('[data-testid="test"]').click();
            cy.wait(3000);
            cy.log("Invoking the API");
            cy.get('[data-testid="backdrop-loader"]').should('not.exist');
            cy.get('[data-testid="swagger-ui"]').within(() => {
                cy.get('.opblock-summary').click();
                cy.get('button').contains('Try it out').should('exist').click();
                cy.get('input[type="text"]').should('exist').type('1');
                cy.get('button').contains('Execute').should('exist').click();
            });
            // TODO - add assertion for response once the domain name issue is resolved
            cy.log('Invoked the API successfully');
        });
    });

    after(() => {
        cy.userLogout();
    });
});
