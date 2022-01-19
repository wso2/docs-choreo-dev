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

export class TryOut {
  static tryOutApi(appName: string) {
    // Tryout the added API
    cy.xpath(
      '//input[@placeholder="Select an Environment"]//../div[@role="button"]'
    ).click({ force: true });
    cy.get('[data-value="dev-us-east-azure"]').click();
    cy.xpath(
      '//input[@placeholder="Select an Application"]//../div[@role="button"]'
    ).click({ force: true });
    cy.get("option").click();
    cy.wait(5000);
    cy.get('[data-testid="get-test-key-btn"]').should("not.be.disabled");
    cy.get('[data-testid="get-test-key-btn"]').click();
    cy.wait(4000);
  }

  static navigateToTryOut(apiName: string) {
    // Tryout the added API
    cy.get(apiName).click();
    cy.wait(2000);
    cy.get('[data-testid="tryout-item-link"]').click();
    cy.wait(5000);
  }

  static SelectResource(httpMethod: string, path: string) {
    const pathVariable = `[data-path="${path}"]`;
    cy.get(".swagger-ui").within(() => {
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

  static ExecuteResourceFunction() {
    cy.get(".execute-wrapper").click();
    cy.log("Execution is successful");
  }

  static VerifyAPI() {
    cy.get('tr[data-param-name="sort"]').within(() => {
      cy.get(".parameters-col_description").within(() => {
        cy.get("select")
          .select("todayCases")
          .should("have.value", "todayCases");
      });
    });
    cy.get('tr[data-param-name="yesterday"]').within(() => {
      cy.get(".parameters-col_description").within(() => {
        cy.get("select").select("true").should("have.value", "true");
      });
    });
    cy.get('tr[data-param-name="allowNull"]').within(() => {
      cy.get(".parameters-col_description").within(() => {
        cy.get("select").select("0").should("have.value", "0");
      });
    });
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

  static DeleteApplication(appName: string) {
    cy.get('[data-testid="applications-appbar-btn"]').click();
    cy.get(appName).trigger("mouseover");
    cy.get("button").click;
    cy.get("button").click();
    cy.get('[data-testid="delete-dialog-ok-button"]').click();
  }

  static GenerateAccessToken() {
    cy.log("Generating an access token");
    cy.get('[data-testid="application-selector"]').click();
    cy.get("body #menu- div ul li").eq(0).click();
    cy.get('[data-testid="get-test-key-btn"]').should("be.enabled").click();
    cy.get("[data-testid=accessTokenInput]").should("not.be.empty");
    cy.log("Successfully generated an access token");
    cy.wait(4000);
  }

  static removeGeneratedCredentials() {
    cy.log("Navigating to Credentials tab to remove credentials");
    cy.wait(5000);
    cy.get('[data-testid="credentials-item-link"]').click();
    cy.url().should("include", "/credentials");
    cy.get("keys-info-cell").should("not.exist");
    cy.get('[data-testid="remove-creds-btn"]').click();
    cy.get('[data-testid="remove-creds-confirmation-ok"]').click();
    cy.wait(4000);
    cy.get("keys-info-cell").should("be.visible");
    cy.log("Successfully removed credentials");
  }
}
