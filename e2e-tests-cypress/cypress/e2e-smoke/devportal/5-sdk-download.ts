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

import { LONG_TIME_OUT, STANDARD_TIME_OUT } from "../../support/common/constants";
import { getApiName } from "../../support/devportal/utils";

describe('Downloading the API SDK scenario', () => {

    const apiName = getApiName();
    const sdkFile = apiName + "_1.0.0_android.zip";
    const path = require("path");

    before(() => {
        cy.devportalLogin();
        cy.navigateToOverviewInDevportal(apiName);
    });

    after(() => {
        cy.devportalLogout(); 
    });

    it('Navigating to SDK page and download SDK ', () => {
        cy.wait(5000)
        cy.url().should('not.include', 'undefined',{ timeout: STANDARD_TIME_OUT })
        cy.get('[data-testid=sdks-item-link]')
            .should("be.visible", { timeout: STANDARD_TIME_OUT }).click({ force: true });
        cy.log("clicking for download Android SDK ");
        cy.get('[data-testid=sdk-android-button]')
            .should("be.visible", { timeout: STANDARD_TIME_OUT }).click()
    })

    it('Verify the downloaded SDK file', () => {
        cy.log(" Looking for the SDK file under downloads folder : " +  sdkFile);
        const downloadsFolder = Cypress.config("downloadsFolder");
        cy.readFile(path.join(downloadsFolder, sdkFile)).should("exist", {timeout: LONG_TIME_OUT});
     });

});
