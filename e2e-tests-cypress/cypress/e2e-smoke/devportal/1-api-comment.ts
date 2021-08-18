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

import { MEDIUM_TIME_OUT, STANDARD_TIME_OUT } from "../../support/common/constants";
import { getApiName } from "../../support/devportal/utils";

describe('API overview comment scenario', () => {

    const apiName = getApiName();

    before(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    after(() => {
        cy.devportalLogout();
    });

    it('Adding and deleting comment for the API', () => {
        cy.getByTestId("btn-add-comment-open-close").click();
        cy.log('Opened the comment box');
        cy.get('[name="newComment"]').type('Test comment from Cypress Test Runner').wait(STANDARD_TIME_OUT);
        cy.getByTestId("btn-add-comment").click();
        cy.get('[data-testid="txt-comments-count"]').should("have.text", "Comments (1)",  { timeout: MEDIUM_TIME_OUT});
        cy.get('[data-testid=txt-no-comments]').should('not.exist');
        cy.log('Comment added successfully');

        cy.log('Deleting the comment');
        cy.get('table > tbody > tr:first').within(() => {
            cy.get('[data-testid="btn-delete-comment"]').click({ force: true });
        });
        cy.get('[class="MuiPopover-root"]').get('button').contains('Yes').click({ force: true });
        cy.get('[data-testid="txt-comments-count"]').should("have.text", "Comments (0)");
        cy.get('[data-testid=txt-no-comments]').should('exist');
        cy.log('Successfully deleted the comment');
    });
});
