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

import { cyGet } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";




export class AppsList {

    static createAnApplication(appName: string) {
        cy.get('[data-testid="create-application-btn"]').click();
        cy.get('[data-testid="app-name"]').type(appName);
        cy.get('[data-testid="application-description"]').type('Application for e2e testing');
        cy.get('[data-testid="create-button"]').click({ force: true });
        cy.wait(5000);
        cy.get('[data-testid="application-description"]').should('have.text', 'Application for e2e testing');
        cy.get('[data-testid="application-token-type"]').should('have.text', 'JWT');
        cy.log('Application created successfully!');
    }

    static editAnApplication(appName: string, permissionName: string) {
      cyGet('[data-testid="applications-appbar-btn"]').click()
      cyGet(`[data-testid="application-list-${appName}"]`).click();
      cyGet('[data-testid="appliation-edit-btn"]').click();
      cyGet('[data-testid="autocomplete-textfield"]').click();
      cyGet('li[data-option-index="0"]').contains(permissionName).then((option) => {
            option[0].click();
        });
      cyGet('[data-testid="create-button"]').click();
        // App name visible
    }

    static generateCredentials(env: Enums.Environment) {
        cy.get('[data-testid="link-production-keys"]').click();
        if (env === Enums.Environment.SANDBOX) {
            cy.get('[data-testid="sandbox-credentials-menu-item"]').click();
        }
        if (env === Enums.Environment.PRODUCTION) {
            cy.get('[data-testid="production-credentials-menu-item"]').click();
        }

        cy.get('[data-testid="generate-oauth-key"]').should('be.visible').click();
        cy.get('#consumer-key-text').invoke('val').should('not.be.empty')
    }

}
