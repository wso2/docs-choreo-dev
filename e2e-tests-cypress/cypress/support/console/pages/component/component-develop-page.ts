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

 



export class ComponentDevelopPage {

  static addResourcesToProxy(path: string, ...verbs) {
    cy.get('[id="backdrop-loader"').should("not.exist");
    cy.get('[data-testid="delete-all-operations-btn"]').click();
    this.checkHTTPVerb(verbs);
    cy.type("#operation-target").type(path);
    cy.get('[data-testid="add-btn"]').click();
  }

  private static checkHTTPVerb(verbs: string[]) {
    cy.get("#verb-selector").click();
    verbs.forEach((verb) => {
      cy.get(`[data-testid="checkbox-${verb.toUpperCase()}"]>span>input`).click();
    });
    cy.get("body").type("{esc}");
  }

  static addParameterToProxyResource(
    path: string,
    verb: string,
    type: string,
    name: string,
    dataType: string
  ) {
    cy.get(`[id="panel-/${path}/${verb.toLowerCase()}-header"`).click();
    cy.get("#in").eq(0).click();
    cy.contains(type).click()
    cy.get("#parameter-name").focus().type(name);
    cy.get("#type").eq(0).click();
    cy.contains(dataType).click()
    cy.get('[aria-label="add"]').click();
  }
  static saveResource() {
    cy.get("button").contains("Save").click({ force: true });
    cy.contains("API updated successfully").should("be.visible");
  }

  static getVersion() {
    return cy.get('[data-cyid="version-picker"]>div').then((v) => {
      return v.text().trim();
    });
  }

}
