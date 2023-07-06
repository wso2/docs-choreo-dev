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

export class SwaggerUI {
  static SelectResource(path: string, method: string = "") {
    const pathVariable = method
      ? `[id="operations-default-${method}${this.capitalizeFirstLetter(path)}"]`
      : `[data-path="/${path}"]`;
    cy.get("body").then((b) => {
      if (
        b.find(
          `div[id*="${SwaggerUI.getModifiedResourceName(
            path
          )}"]>div>div>div>div>div>button`
        ).length == 0
      ) {
        if (b.find(`[class="try-out"]`).length == 0) {
          cy.get(pathVariable).scrollIntoView().click();
        }
      }
    });
  }

  private static capitalizeFirstLetter(str: string): string {
    if (str.length === 0) {
      return str;
    }
  
    const firstLetter = str.charAt(0).toUpperCase();
    const remainingLetters = str.slice(1);
    return firstLetter + remainingLetters;
  }
  

  static closeResource(path: string, parentComponentId: string) {
    if (parentComponentId == "") {
      cy.get(`[data-path="/${path}"]`).scrollIntoView().click();
    } else {
    cy.get(`[id="${parentComponentId}"]`)
      .within(() => {
        cy.get(`[data-path="/${path}"]`).scrollIntoView().click();
      });
    }
  }

  static TryoutAPI(resource: string = "-get") {
    cy.contains("Try it out").should("be.visible").click();
    cy.contains("Cancel").should("be.visible");
  }

  private static getModifiedResourceName(resource: string = "-get") {
    if (resource.includes("/")) {
      return resource.replace("/", "_");
    }
    return Cypress._.capitalize(resource);
  }

  static ExecuteResourceFunction(resource = "-get") {
    cy.contains("Execute").focus().click();
    cy.get(`[class="curl-command"]`).should("be.visible");
    cy.get('[class="loading-container"]').should("not.exist");
    cy.log("Execution is successful");
    cy.contains("Cancel").click();
  }

  static GetResponse() {
    cy.get(".curl-command").should("exist");
    cy.get(".request-url").should("exist");
    cy.log("Response is successfully returned");
    cy.log("API Tryout is successful!");
    return cy.get(".live-responses-table pre code").invoke("text");
  }

  static getResponseCode() {
    return cy.get("tbody>.response>.response-col_status").eq(0).invoke("text");
  }

  static enterValue(placeholder: string, value: string) {
    cy.wait(2000);
    cy.get(`[placeholder="${placeholder}"]`).clear().type(value);
  }

  static invokeResource(
    resource: string,
    key: string = "",
    value: string = "",
    method: string = ""
  ) {
    this.SelectResource(resource, method);
    this.TryoutAPI(resource);
    if (key) {
      this.enterValue(key, value);
    }
    this.ExecuteResourceFunction(resource);
  }
}
