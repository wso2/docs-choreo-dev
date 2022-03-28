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



export class APIDevelop {
  static addResources(path: string, ...verbs) {
    cy.get('[data-testid="develop-resources-header"]', { timeout: 120000 })
      .contains("Resources")
      .should("be.visible");
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("body").then((body) => {
      if (body.find("#panel1a-header>div>h4").text().trim() === "/*") {
        cy.get('[data-testid="delete-all-operations-btn"]').click();
      }
    });
    this.addHTTPVerb(verbs);
    this.addResource(verbs,path);
  }

  private static addResource(verbs: string[],path: string) {
    cy.get("#operation-target").type(path);
    cy.get('[data-testid="add-btn"]').click();
    this.generateOperationId(verbs,path)
    cy.get("button").then((buttons) => {
      if (buttons.length > 0) {
        buttons.each(function () {
          if (this.innerText === "Save") {
            this.click();
            return;
          }
        });
      }
    });
    cy.intercept({
      method: "PUT",
      url: `${Cypress.env(
        "apimSvcURL"
      )}/api/am/publisher/v2/apis/*/swagger?organizationId=*`,
    }).as("swagger");
    cy.wait("@swagger", { timeout: 120000 }).then((res) => {
      expect(res.response.body.paths).to.have.property(`/${path}`);
    });
    cy.get(`[data-testid="resource-/${path}"]`, { timeout: 120000 }).should(
      "be.visible"
    );
  }

  private static addHTTPVerb(verbs: string[]) {
    cy.get('[data-testid="verb-selector"]').click();
    verbs.forEach((verb) => {
      cy.get(`[data-testid="checkbox-${verb.toUpperCase()}"]`).click();
      cy.wait(1000);
    });
    cy.get("body").type("{esc}");
  }

  static addEndpoints() {
    cy.get('[data-testid="Endpoints"]').click();
    cy.contains("Save").click();
  }

  static updateEndpointConfiguration(newEndpoint: string) {
    cy.get('[data-testid="Endpoints"]').click();
    cy.get('[data-testid="api-endpoint"]').within(() => {
      cy.get("input").clear().type(newEndpoint);
    });
    cy.contains("Save").click();

    cy.get('[id="circular-loader"]').should("not.exist");
    cy.get('[data-testid="api-endpoint"] > div > input').should(
      "have.value",
      newEndpoint
    );
    cy.wait(1000);
    cy.log("Endpoint configuration updated successfully");
  }

  private static generateOperationId(httpVerb: string[], resourcePath: string) {
    httpVerb.forEach((verb) => {
      let header =`[id="panel-/${resourcePath}/${verb.toLocaleLowerCase()}-header"]`
      let input = `[id="panel-/${resourcePath}/${verb.toLocaleLowerCase()}-content"]  div>input[type="text"]`;
      let operationId =`${verb}${resourcePath.replace(/\\/g,'')}`
      cy.get(header).click()
      cy.get(input).eq(0).type(operationId)
    });
  }
}
