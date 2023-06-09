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

import { cyGet, cyLog } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { MEDIUM_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

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
    this.selectDevelop();
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("body").then((body) => {
      if (
        body
          .find('[data-testid="operation"]>div>span>div>div>p')
          .first()
          .text()
          .trim() === "/*"
      ) {
        cy.log("trigger delete all");
        cy.get('[data-testid="delete-all-operations-btn"]').click();
        cy.contains("Undo Delete", MEDIUM_TIME).should("be.visible").wait(3000);
      }
    });
    this.addHTTPVerb(verbs);
    this.addResource(verbs, path);
  }

  static addPolicy(
    resourcePath: string,
    verb: string,
    policy: Enums.PolicyType,
    policyName: string,
    policyType: string,
    headerCount: number = 1
  ) {
    const header = this.getHeader(resourcePath, verb.toUpperCase());

    const buttons = `[id="/${resourcePath}/${verb.toUpperCase()}/out-flow"] div[data-key] button`;
    if (Utils.isUnifiedMenuEnabled()) {
      cyGet('[data-cyid="develop-policies"]').click();
    } else {
      cyGet('[data-testid="Policies"]').click();
    }
    cyGet(header).eq(0).click();
    cy.get(buttons).contains("Attach Policy").click();
    cy.get("button").contains(policy).click();
    cyGet('[name*="Name"]').should("be.visible").type(policyName);
    cyGet('[name*="Value"]').clear().type(policyType);
    cy.get("button").contains("Add").click();
    cyGet(`[title="${policy}"]`).should("have.length", headerCount);
    cy.get("button").contains("Save").click();
  }

  private static addResource(verbs: string[], path: string) {
    cy.get('[name="target"]').type(path);
    cy.get('[data-testid="add-btn"]').click();
    this.generateOperationId(verbs, path);
    cy.get("button")
      .should("be.enabled")
      .contains("Save")
      .click({ force: true });
    cy.intercept({
      method: "PUT",
      url: `${Cypress.env(
        "apimSvcURL"
      )}/api/am/publisher/v2/apis/*/swagger?organizationId=*`,
    }).as("swagger");
    cy.wait("@swagger", MEDIUM_TIME).then((res) => {
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
    cyLog(httpVerb);
    httpVerb.forEach((verb) => {
      const header = this.getHeader(resourcePath, verb.toLowerCase());
      const modifiedResourcePath = Cypress._.capitalize(
        resourcePath.replace(/\\/g, "")
      );
      const operationId = `${verb.toLowerCase()}${modifiedResourcePath}`;

      cy.get(header).click();
      cy.wait(5000);
      cy.get(header)
        .parent()
        .then((p) =>
          cy.wrap(p).within(() => {
            cy.get(`div>input[type="text"]`).eq(0).type(operationId);
          })
        );
    });
  }

  private static getHeader(resourcePath: string, verb: string) {
    return `[id="panel-/${resourcePath}/${verb}-header"]`;
  }

  static deletePolicy(resourcePath: string, verb: string) {
    const buttons = `[id="/${resourcePath}/${verb.toUpperCase()}/out-flow"] div[data-key] button`;
  }

  static editHeader(
    resourcePath: string,
    verb: string,
    headerValue: string,
    headerName: string = ""
  ) {
    const buttons = `[id="/${resourcePath}/${verb.toUpperCase()}/out-flow"] div[data-key] button`;
    const header = this.getHeader(resourcePath, verb.toUpperCase());
    cyGet('[data-cyid="develop-policies"]').click();
    cyGet(header).eq(0).click();
    cy.get(buttons).eq(0).click();
    if (headerName) {
      cyGet('[name*="Name"]').should("be.visible").type(headerName);
    }
    cyGet('[name*="Value"]').clear().type(headerValue);
    cy.get("button:not([disabled])").contains("Save").click();
    cy.wait(1000);
    cy.get("button").contains("Save").click();
  }

  private static selectDevelop() {
    let selector = '[data-cyid="develop-resources"]';
    if (Utils.isUnifiedMenuEnabled()) {
      cy.get("body").then((bdy) => {
        // Secondary menu is collapsed
        if (bdy.find(selector).length == 0) {
          // Expand secondary menu
          cy.get('[data-cyid="link-develop"]').click();
        }
      });
      cy.get(selector).click();
    } else {
      selector = '[data-testid="develop-resources-header"]';
      cy.get(selector).contains("Resources").should("be.visible");
    }
  }
}
