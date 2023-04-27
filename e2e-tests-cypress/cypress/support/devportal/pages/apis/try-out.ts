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

import {
  DEVPORTAL_APP_TOKEN_GEN_URL,
  VERY_SHORT_TIME,
} from "../../../console/constants";
import { Utils } from "../../../console/utils";

export class TryOut {
  static navigateToTryOutMenu() {
    cy.get('[data-testid="tryout-item-link"]').click();
  }

  static SelectApplication(applicationName: string) {
    cy.get('[data-testid="application-selector-wrapper"]').within(() => {
      cy.get('[data-testid="application-selector"]').click();
    });
    cy.get(`[data-value="${applicationName}"]`).click().wait(1000);
  }

  static generateTestKeyAndVerify() {
    cy.get('[data-testid="get-test-key-btn"]').click({ force: true });
    cy.contains("Successfully created the access token").should("be.visible");
    cy.get("#accessTokenInput").invoke("val").should("not.be.empty");
  }

  static SelectResource(httpMethod: string, path: string) {
    const pathVariable = `[data-path="/${path}"]`;
    Utils.getRenderedElement(".swagger-ui").within(() => {
      cy.get(pathVariable).click();
    });
  }

  static TryoutAPI() {
    cy.get('[id*="operations-"] button')
      .contains("Try it out")
      .should("exist")
      .click();
    cy.get(".opblock-section-header").contains("Cancel").should("exist");
  }

  static TryoutApplication() {
    cy.get(".try-out__btn").should("exist").click();
    cy.get(".opblock-section-header").contains("Cancel").should("exist");
  }

  static InputQueryParamater(paramName: string, paramValue: any) {
    cy.get(
      `tr[data-param-name="${paramName}"]>td[class="parameters-col_description"]>input`
    )
      .clear()
      .type(paramValue);
  }

  static ExecuteResourceFunction() {
    cy.get(".execute-wrapper").click();
    cy.log("Execution is successful");
  }

  static GetResponse() {
    cy.get(".curl-command").should("exist");
    cy.get(".request-url").should("exist");
    cy.log("Response is successfully returned");
    cy.get(
      ":nth-child(1) > .responses-table > tbody > .response > .response-col_status"
    ).should("have.text", "200");
    cy.log("API Tryout is successful!");
  }

  static ValidateResponse(statusCode: string) {
    cy.get(".curl-command").should("exist");
    cy.get(".request-url").should("exist");
    cy.log("Response is successfully returned");
    cy.get(
      ":nth-child(1) > .responses-table > tbody > .response > .response-col_status"
    ).should("contain", statusCode);
  }

  static DeleteApplication(appName: string) {
    cy.get('[data-testid="applications-appbar-btn"]').click();
    cy.get('[data-testid="search-btn"]').trigger("mouseover");
    cy.get('[data-testid="search-app"] [placeholder="Search"]').type(appName);
    cy.contains(appName).trigger("mouseover");
    cy.get(`[data-testid="delete-btn-${appName}"]`)
      .trigger("mouseover")
      .click();
    cy.get('[data-testid="delete-dialog-ok-button"]').click();
    cy.get('[data-testid="create-application-btn"]', { timeout: 50000 }).should(
      "be.visible"
    );
  }

  static GenerateAccessToken() {
    cy.log("Generating an access token");
    cy.intercept({
      method: "POST",
      url: DEVPORTAL_APP_TOKEN_GEN_URL,
      times: 1,
    }).as("generateAppToken");

    Utils.getRenderedElement('[data-testid="get-test-key-btn"]').should(
      "be.enabled"
    );
    Utils.getRenderedElement('[data-testid="get-test-key-btn"]').click();

    cy.wait("@generateAppToken", { timeout: VERY_SHORT_TIME }).then(() => {
      Utils.getRenderedElement('[data-testid="get-test-key-btn"]')
        .contains('role="progressbar"')
        .should("not.exist");
      cy.get("[data-testid=accessTokenInput]").should("not.be.empty");
      cy.log("Successfully generated an access token");
    });
  }
}
