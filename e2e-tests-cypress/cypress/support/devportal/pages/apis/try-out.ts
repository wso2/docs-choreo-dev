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
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import {
  DEV_PORTAL_APP_TOKEN_GEN_URL,
} from "../../../commons/urls";
import { Utils } from "../../../commons/utils";

export class TryOut {

  static SelectApplication(applicationName: string) {
    Utils.getRenderedElement(
      '[data-testid="application-selector"]',
      3000
    ).click();
    Utils.getRenderedElement(`[data-value="${applicationName}"]`, 2000).click();
  }

  static generateTestKeyAndVerify() {
    cyGet('[data-testid="get-test-key-btn"]').click({ force: true });
    cy.contains("Successfully created the access token").should("be.visible");
    cyGet("#accessTokenInput").invoke("val").should("not.be.empty");
  }

  static SelectResource(path: string) {
    cyGet('[data-testid="get-test-key-btn"]').should("be.enabled");
    const pathVariable = `[data-path="/${path}"]`;
    Utils.getRenderedElement(".swagger-ui", 3000).within(() => {
      cy.get(pathVariable).should("have.length", "1").realHover().realClick();
    });
  }

  static TryoutAPI() {
    cyGet('[class="try-out"]').find("button").scrollIntoView().click();
    cyGet('[class="try-out"]')
      .contains(new RegExp(/Cancel/, "g"))
      .should("exist");
  }

  static InputQueryParamater(paramName: string, paramValue: any) {
    cyGet(
      `tr[data-param-name="${paramName}"]>td[class="parameters-col_description"]>input`
    )
      .clear()
      .type(paramValue);
  }

  static ExecuteResourceFunction() {
    cyGet(".execute-wrapper").within(() => {
      cy.get("button").click({ force: true });
    });
    cy.log("Execution is successful");
  }

  static ExecuteResourceInRetryFunction() {
    Utils.getRenderedElement('[class="btn-group"]').within(() => {
      cy.get("button.execute.opblock-control__btn")
        .contains("Execute")
        .realClick();
    });
  }

  static GetResponse() {
    cyGet(".curl-command").should("exist");
    cyGet(".request-url").should("exist");
    cy.log("Response is successfully returned");
    this.RetryExecuteResourceFunction();
    cy.log("API Tryout is successful!");
  }

  static RetryExecuteResourceFunction(
    retryCount: number = 0,
    retryDelay: number = VERY_SHORT_TIME.timeout
  ) {
    retryCount++;
    if (retryCount < 4) {
      cy.log("Retry Count: " + retryCount);
      cy.get(
        ":nth-child(1) > .responses-table > tbody > .response > .response-col_status"
      )
        .invoke("text")
        .then((text) => {
          cy.log("Response Status Code: " + text.trim());
          if (text.trim() == "200") {
            expect(text.trim()).equal("200");
            return;
          } else {
            cy.log("API Tryout is not successful!");
            cy.wait(retryDelay);
            this.ExecuteResourceInRetryFunction();
            this.RetryExecuteResourceFunction(retryCount, retryDelay);
          }
        });
    } else {
      cy.log("API Tryout Retrying was not successful!");
      expect(false).to.be.true;
    }
  }

  static ValidateResponse(statusCode) {
    cyGet(".curl-command").should("exist");
    cyGet(".request-url").should("exist");
    cy.log("Response is successfully returned");
    cyGet(
      ":nth-child(1) > .responses-table > tbody > .response > .response-col_status"
    ).should("contain", statusCode);
  }

  static DeleteApplication(appName: string) {
    cyGet('[data-testid="applications-appbar-btn"]').click();
    cyGet('[data-testid="search-btn"]').trigger("mouseover");
    cyGet('[data-testid="search-app"] [placeholder="Search"]').type(appName);
    cy.contains(appName).trigger("mouseover");
    cyGet(`[data-testid="delete-btn-${appName}"]`).trigger("mouseover").click();
    cyGet('[data-testid="delete-dialog-ok-button"]').click();
    cyGet('[data-testid="create-application-btn"]', SHORT_TIME).should(
      "be.visible"
    );
  }

  static GenerateAccessToken() {
    cy.log("Generating an access token");
    cy.intercept({
      method: "POST",
      url: DEV_PORTAL_APP_TOKEN_GEN_URL,
      times: 1,
    }).as("generateAppToken");

    Utils.getRenderedElement('[data-testid="get-test-key-btn"]').should(
      "be.enabled"
    );
    Utils.getRenderedElement('[data-testid="get-test-key-btn"]').click({
      force: true,
    });

    cy.wait("@generateAppToken", SHORT_TIME).then(() => {
      Utils.getRenderedElement('[data-testid="get-test-key-btn"]')
        .contains('role="progressbar"')
        .should("not.exist");
      cyGet("[data-testid=accessTokenInput]").should("not.be.empty");
      cy.log("Successfully generated an access token");
    });
  }

  static selectEndpoint(endpoint: string) {
    Utils.getRenderedElement('[data-testid="endpoint-selector"]').click();
    cyGet(`[data-cyid="endpoint-list-item-${endpoint}"]`).click();
  }
}
