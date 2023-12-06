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

export class OrganizationSettingsPage {

    static navigateToApplicationSecurity() {
        cy.get('[data-cyid="nav-link-application-security-link-tabs-link-tab"]').click();
    }

    static navigateToChoreoInbuiltIDP() {
        cy.get('[data-cyid="link-button"]').click();
    }

    static waitTillUserstoreLoad() {
        cy.get('[data-cyid="table-title"]').find('tbody tr').should('have.length', 1);
    }

    static addTestUsers() {
        cy.fixture("users.csv").then((users) => {
            cy.get('input[type="file"]').attachFile({
                fileContent: users,
                fileName: "users.csv",
                mimeType: "text/csv",
            });
            cy.get('[data-cyid="upload-csv-button"]').click();
        });
    }
}
