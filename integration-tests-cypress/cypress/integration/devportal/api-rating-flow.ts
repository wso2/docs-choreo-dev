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

/// <reference types="cypress" />

import { getApiName } from "../../support/devportal/utils";

describe('API overview rating scenario', () => {

    const apiName = getApiName();

    before(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    after(() => {
        cy.devportalLogout();
    });

    it('Adding and modifying the ratings of the API', () => {
        cy.log('Opening the rating box');
        cy.get('[class="MuiGrid-root MuiGrid-item MuiGrid-grid-xs-12 MuiGrid-grid-sm-12 MuiGrid-grid-md-12 MuiGrid-grid-lg-4"]').within(() => {
            cy.get('button')
                .first()
                .click();
        })

        cy.log('Adding 4 star rating');
        cy.get('[for="hover-feedback-4"]').trigger('focus');
        cy.wait(3000);
        cy.get('[for="hover-feedback-4"]').click({ force: true });
        cy.wait(3000);
        cy.get('[class="MuiPopover-root"]').click({ force: true });
        cy.log('Added 4 star');

        cy.log('Changing 4 star rating to 3 star');
        cy.get('[for="hover-feedback-3"]').trigger('focus');
        cy.wait(3000)
        cy.get('[for="hover-feedback-3"]').click({ force: true });
        cy.wait(3000);
        cy.get('[class="MuiPopover-root"]').click({ force: true });
        cy.log('Changed the rate to 3 stars');
        cy.get('body').type('{esc}');
    })

});
