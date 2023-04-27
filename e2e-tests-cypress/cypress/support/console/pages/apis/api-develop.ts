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
  static httpVerbs: string[] = [
    "GET",
    "POST",
    "PUT",
    "PATCH",
    "DELETE",
    "HEAD",
    "OPTIONS",
  ];

  static addResources(path: string, ...verbs) {
    cy.get('[data-testid="develop-resources-header"]')
      .contains("Resources")
      .should("be.visible");
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("body").then((body) => {
      if (body.find("#panel1a-header>div>h4").text().trim() === "/*") {
        cy.log("trigger delete all");
        cy.get('[data-testid="delete-all-operations-btn"]').click();
        cy.contains("Undo Delete", { timeout: 120000 })
          .should("be.visible")
          .wait(3000);
      }
    });
    this.addHTTPVerb(verbs);
    this.addResource(verbs, path);
  }

  private static addResource(verbs: string[], path: string) {
    cy.get('[name="target"]').type(path);
    cy.get('[data-testid="add-btn"]').click();
    this.generateOperationId(verbs, path);
    cy.get("button").should('be.enabled').contains("Save").click({ force: true })
    cy.intercept({
      method: "PUT",
      url: `${Cypress.env(
        "apimSvcURL"
      )}/api/am/publisher/v2/apis/*/swagger?organizationId=*`,
    }).as("swagger");
    cy.wait("@swagger", { timeout: 120000 }).then((res) => {
      const reqUrl = res.request.url;
      const geturl = reqUrl.replace("/swagger", "");
      cy.log(geturl);
      expect(res.response.body.paths).to.have.property(`/${path}`);
    });
    cy.get(`[id="panel-/${path}/${verbs[0].toLowerCase()}-header"]`).should(
      "exist"
    );
  }

  private static addHTTPVerb(verbs: string[]) {
    cy.get("#verb-selector").click();
    verbs.forEach((verb) => {
      let id = `verb-selector-option-${this.httpVerbs.indexOf(verb)}`;
      cy.get(`#${id}`).click().wait(1000);
    });
    cy.get("body").type("{esc}");
  }

  private static generateOperationId(httpVerb: string[], resourcePath: string) {
    httpVerb.forEach((verb) => {
      const header = `[id="panel-/${resourcePath}/${verb.toLowerCase()}-header"]`;
      const input = `[id="panel-/${resourcePath}/${verb.toLowerCase()}-header"]  div>input[type="text"]`;
      const modifiedResourcePath = Cypress._.capitalize(resourcePath.replace(/\\/g, ""))
      const operationId = `${verb.toLowerCase()}${modifiedResourcePath}`;

      cy.get(header).click();
    cy.wait(5000)
      cy.get(header).parent().then(p=>cy.wrap(p).within(()=>{
        cy.get(`div>input[type="text"]`).eq(0).type(operationId)
      }))
   //   cy.get(input).eq(0).type(operationId);
    });
  }
}
