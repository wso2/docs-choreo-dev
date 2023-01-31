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

import { GraphQL } from "../../apis/graphql";
import { Utils } from "../../utils";

export class ComponentListingPage {
  
  static deleteComponent(componentName: string) {
    cy.get(`tbody>tr`).should("be.visible").realHover();
    cy.get("button>span").contains("Delete").click();
    cy.get('[data-testid="confirm-name"]').within(()=>{
      cy.get('input').type(componentName)
    })
    cy.get(".MuiDialogActions-spacing button")
      .should("be.enabled")
      .eq(1)
      .click();
    cy.get(".MuiDialog-container").should("not.exist");
    this.verifyDeletion();
  }

  static visitComponentWithoutName() {
    const componentURL = Cypress.env(`componentURL`);
    cy.visit(componentURL);
  }

  static visitToAComponent(componentName: string) {
    cy.get('[data-testid="main-left-nav-item-Project"]')
      .should("be.visible")
      .click();
    cy.get("tr p").contains(componentName).should("be.visible").click();
    cy.get("[data-cyid=link-overview]").should("be.visible");
    Utils.saveComponentURL();
    cy.log("Successfully visited to the component");
  }

  private static verifyDeletion() {
    cy.url().then((url) => {
      const projectID = url.split("projects/")[1].split("?")[0];
      const { handle } = Cypress.env("userData");
      const token = Cypress.env("apim_token");
      GraphQL.getComponents(projectID, handle, token).then((res) => {
        expect(res.status).to.be.equal(200);
        expect(res.body.data.components).to.be.empty;
      });
    });
  }
}
