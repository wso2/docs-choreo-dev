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
    cy.wait(5000);
    cy.get('[data-cyid="btn-deploy-proxy"]')
      .should("be.visible")
      .click({ force: true });
    cy.get('[data-cyid="btn-proxy-promote"]').should("be.visible");
  }

  static PromoteToProd() {
    cy.contains("Promote").click();
   cy.get('[data-cyid="proxy-env-card-header"]>div>span').contains('Production').should('be.visible')
  } 
  
  
  static verifyDevInvokeURL() {
    return cy
      .get('[data-cyid="text-field-invoke-url"] input')
      .eq(0)
      .invoke("attr", "value");
  }

  static verifyProdInvokeURL() {
    cy.wait(5000);
    return cy
      .get('[data-cyid="text-field-invoke-url"] input')
      .eq(1)
      .invoke("attr", "value");
  }
}
