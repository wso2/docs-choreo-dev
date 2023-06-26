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


import { cyGet } from "../../../commons/cy";
import { Utils } from "../../../commons/utils";
import { ProxyAPI } from "../../../interfaces/proxy-api";


export class RestAPIProxyTemplate {
  static skipSource() {
    cy.get('[data-cyid="btn-skip-src"]').should("be.visible").click();
  }

  static createOpenApi(filepath: string = "", url: string = "") {
    if (filepath) {
      cy.get('[data-cyid="btn-upload-button"]').click();
      cy.get('input[type="file"]').attachFile(filepath);
    }

    if (url) {
      cy.get('[data-cyid="txt-oas-url"]').should("be.visible").type(url);
    }

    cy.get('[data-cyid="btn-next-button"]').should("be.visible").click();
  }




  static createProxyApi(api: ProxyAPI) {
    cy.get('[data-cyid="api-name"]').within(() =>
      cy.get("input").clear().type(api.apiName)
    );
    cy.get('[data-cyid="api-version"]').clear().type(api.version);
    cy.get('[data-cyid="api-basepath"]').within(() =>
      cy.get("input").clear().type(api.apiBasePath)
    );

    cy.get('[data-cyid="api-endpoint"]').within(() =>
      cy.get("input").clear().type(api.endpoint)
    );
    if(api.isInternal){
      cyGet('[aria-label="Access Modes"]>div').eq(1).click();
    }
    cy.get('[data-cyid="btn-create"]').should("be.enabled").click();

    cyGet('[data-testid="delete-all-operations-btn"]').should("be.visible");
  }





  static enterAPIdetails(
    apiName: string,
    apiBasePath: string,
    endpoint: string,
    version: string = "",
    validateResourceName: string = "",
    operation: string
  ) {
    cy.get('[data-cyid="api-name"]').within(() =>
      cy.get("input").clear().type(apiName)
    );

    if (version) {
      cy.get('[data-cyid="api-version"]').clear().type(version);
    }

    cy.get('[data-cyid="api-basepath"]').within(() =>
      cy.get("input").clear().type(apiBasePath)
    );

    if (endpoint) {
      cy.get('[data-cyid="api-endpoint"]').within(() =>
        cy.get("input").clear().type(endpoint)
      );
    }
    cy.get('[data-cyid="btn-create-button"]').should("be.enabled").click();

    let resourceIdentifier = "panel-/intensity/get-header";
    if (validateResourceName) {
      resourceIdentifier = "panel-/" + validateResourceName + "/get-header";
    }
    let isResourceFound = false;

    cy.get(`[data-testid="operation"]`)
      .each((item, index, list) => {
        let resourceId = Cypress.$(item).attr("id");
        if (resourceId === resourceIdentifier) {
          isResourceFound = true;
        }
      })
      .then(() => {
        expect(isResourceFound).to.be.true;
      });

    Utils.saveComponentURL();
  }
}
