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

export class ComponentListingPage {
  static deleteComponent(componentName: string) {
    cy.get("tr p").contains(componentName).should("be.visible").realHover();
    cy.get("button>span").contains("Delete").click();
    cy.get('[data-testid="confirm-name"]>div>input')
      .should("be.visible")
      .type(componentName);
    cy.get(".MuiDialogActions-spacing button")
      .eq(1)
      .should("be.enabled")
      .click();
    this.verifyDeletion();
  }

  private static verifyDeletion() {
    cy.intercept("POST", `${Cypress.env("newAppSvcURL")}/projects/1.0.0/graphql`).as("delete");
    cy.wait("@delete", { timeout: 180000 }).then((i) => {
      const { status, canDelete } = i.response.body.data["deleteComponentV2"];
      expect(status).equal("success");
      expect(canDelete).to.true;
    });
  }
}
