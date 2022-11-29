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


export class APIDeployment {
  static navigateToDeployment() {
    cy.contains("Deploy").should("be.visible").click();
    cy.get('[id="backdrop-loader"').should("not.exist");
  }

  static DeployToDev() {
    cy.get('[data-cyid="btn-deploy-proxy"]').should("not.be.disabled").click();
    cy.get('[data-cyid="btn-next"]')
      .contains("Deploy")
      .should("be.visible")
      .click();
    cy.get('[data-cyid="deployment-status"]')
      .contains("Active")
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static PromoteToProd() {
    cy.get('[data-cyid*="promote"]').click();
    cy.get('[data-cyid="btn-next"]').click();
    cy.get('[data-cyid="proxy-env-card-header"]>div>span')
      .contains("Production")
      .should("be.visible");
    cy.get('[data-cyid="deployment-status"]')
      .should("have.length", 2)
      .eq(1)
      .contains("Active")
      .should("be.visible");
    cy.get('[data-cyid*="promote"]').should("not.be.disabled");
  }

  static verifyDevInvokeURL() {
    // return Utils.getInvokeUrl(0);
  }

  static verifyStgeInvokeURL() {
    if (Cypress.env("isPrivateOrg")) {
      cy.wait(5000);
      // return Utils.getInvokeUrl(1);
    }
    return cy.wrap("skip");
  }

  static verifyProdInvokeURL() {
    if (Cypress.env("isPrivateOrg")) {
      // return Utils.getInvokeUrl(2);
    }
    // return Utils.getInvokeUrl(1);
  }
}
