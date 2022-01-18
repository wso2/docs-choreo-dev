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
import { MEDIUM_TIME_OUT } from "../../constants";

export class Subscriptions {

    static addAnSubsriptionToApplication(apiName: string) {
    
        cy.get('[data-testid="subscriptions"]').click();
        cy.get('[data-testid="create-subscription-btn"]').click();
        cy.wait(2000);
        cy.log("Search API " + apiName + " to subscribe");
        cy.get('.MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input').type(apiName);
        cy.get('[data-testid="add-api-' + apiName + '"]').click();
        cy.get('[data-testid="subscription-dialog-close-btn"]').click();
        cy.log('Subscribed to the API successfully');
    }

    static validateResubscribingApi(apiName: string) {
    
        cy.log("Check whether user can re-subscribe to the API -  " + apiName + " ,that has already subscribed ");
        cy.get('[data-testid="create-subscription-btn"]').click();
        cy.wait(2000);
        cy.log("Search API " + apiName + " to subscribe");
        cy.get('.MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input').type(apiName);
        cy.wait(2000);
        cy.get('[index="0"] > :nth-child(3) > :nth-child(1)').contains('button', 'Add').should('be.disabled');
        cy.get('[data-testid="subscription-dialog-close-btn"]').click();

    }

}

