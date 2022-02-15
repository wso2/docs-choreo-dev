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

import { deprecate } from "util";
import { Utils } from "../../utils";

export class ComponentDeployPage {

  static deploy() {
    cy.get('[data-cyid="btn-deploy-api"]').should("be.visible").click();
  }

  static configureAndDeploy(configValue: string) {
    cy.wait(8000);
    cy.get('[data-cyid="btn-deploy-api"]').click();
    cy.contains("Deploy").should("be.visible").click();
    this.addConfiguration(configValue);
  }

  static addConfiguration(value: string) {
    cy.contains("Configure & Deploy").should("be.visible").click();
    cy.get(
      '[class="MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputMarginDense MuiOutlinedInput-inputMarginDense"]'
    ).type(value);
    cy.get('button[type="submit"]').click();

    cy.get('[title="Build Success"]', { timeout: 900000 });
    cy.get('[data-cyid="btn-promote"]').should("be.visible");
  }

  static isDeploymentSuccessful() {
    return cy.get('[title="Build Success"]',{timeout:90000});
  }

  static ismanualDeploymentSuccessful() {
    return cy.get('[title="Deployed successfully"]',{timeout:90000});
  }


  static promoteToProd() {
    cy.get('[data-cyid="btn-promote"]',{timeout:120000}).should('be.visible').click();
  }

  private static selectButton(buttonName) {
    return cy.get('[type="button"]>span').contains(buttonName);
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

  static stopAllDeployment() {
    cy.get('[data-cyid="btn-stop-redeploy"]').click({ multiple: true });
  }
}
