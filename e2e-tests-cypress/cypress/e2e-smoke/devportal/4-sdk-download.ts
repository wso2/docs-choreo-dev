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

import {
  LONG_TIME_OUT,
  STANDARD_TIME_OUT,
} from "../../support/devportal/constants";
import { LoginPage } from "../../support/devportal/pages/login/login-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { DevportalHomePage } from "../../support/devportal/pages/home/home-page";
import path from "path";

describe("Downloading the API SDK scenario", () => {
  const apiName = "e2eapiCYE2Eoas";
  const sdkFile = apiName + "_1.0.0_android.zip";

  before(() => {
    LoginPage.loginToDevportal();
    Apis.navigateToApiOverview("e2eapiCYE2Eoas");
  });

  after(() => {
    DevportalHomePage.logout();
  });

  it("Navigating to SDK page and download SDK ", () => {
    cy.get("[data-testid=txt-api-endpoint]").should("be.visible");
    cy.url().should("not.include", "undefined", { timeout: STANDARD_TIME_OUT });
    cy.log("clicking SDKs item link for navigate to download page ");
    cy.get("[data-testid=sdks-item-link]")
      .should("be.visible", { timeout: STANDARD_TIME_OUT })
      .click({ force: true });
    cy.log("create a workaround for give a proper loading time for sdk page");
    cy.wait(6000);
    cy.get("[data-testid=overview-item-link]").click();
    cy.get("[data-testid=txt-api-endpoint]").should("be.visible");
    cy.get("[data-testid=sdks-item-link]")
      .should("be.visible", { timeout: STANDARD_TIME_OUT })
      .click({ force: true });

    cy.get('[data-testid="sdk-android-button"]', { timeout: LONG_TIME_OUT })
      .should("be.visible")
      .click();
  });

  it("Verify the downloaded SDK file", () => {
    cy.log(" Looking for the SDK file under downloads folder : " + sdkFile);
    cy.log(sdkFile);
    const downloadsFolder = Cypress.config("downloadsFolder");
    cy.readFile(path.join(downloadsFolder, sdkFile)).should("exist", {
      timeout: LONG_TIME_OUT,
    });
    cy.log("SDK file verification is successful");
  });
});
