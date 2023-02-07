import { Utils } from "../../utils";

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
export class RestAPIProxyTemplate {
  static SelectHttpProxyAPITemplate() {
    cy.get('[data-testid="project-template-list-httpProxyApi"]').click();
  }

  static skipSource() {
    cy.get('[data-cyid="btn-skip-src"]').should("be.visible").click();
  }

  static createOpenApi(filepath: string = "", url: string = "") {
    if (filepath) {
      cy.get('[data-cyid="btn-upload"]').click();
      cy.get('input[type="file"]').attachFile(filepath);
    }

    if (url) {
      cy.get('[data-cyid="txt-oas-url"]').should("be.visible").type(url);
    }

    cy.get('[data-cyid="btn-next"]').should("be.visible").click();
  }

  static enterAPIdetails(
    apiName: string,
    apiBasePath: string,
    endpoint: string,
    version: string = "",
    validateResourceName: string = ""
  ) {
    cy.get('[data-cyid="api-name"]').clear().type(apiName);

    if (version) {
      cy.get('[data-cyid="api-version"]').clear().type(version);
    }

    cy.get('[data-cyid="api-basepath"]').clear().type(apiBasePath);

    if (endpoint) {
      cy.get('[data-cyid="api-endpoint"]').clear().type(endpoint);
    }
    cy.get('[data-cyid="btn-create"]').should("be.enabled").click();

    let resourceIdentifier = "resource-/intensity";
    if (validateResourceName) {
      resourceIdentifier = "resource-/" + validateResourceName;
    }

    cy.get(`[data-testid="${resourceIdentifier}"]`);
    Utils.saveComponentURL();
  }

  private static interceptValidate() {
    cy.intercept(
      `${Cypress.env(
        "apimSvcURL"
      )}/api/am/publisher/v2/apis/validate?organizationId=*&query=*`
    ).as("validate");

    cy.wait("@validate").then((r) => {
      if (r.response.statusCode == 404) {
        cy.get("button").then((buttons) => {
          if (buttons.length > 0) {
            buttons.each(function () {
              if (this.innerText === "Create") {
                this.click();
                return;
              }
            });
          }
        });
      }
    });
  }
}
