import { TimeoutError } from "cypress/types/bluebird";
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

  static designNewRestApi(apiName, apiVersion, apiBasePath, endpoint, fileID) {
    cy.get('[role="dialog"] ul>div:nth-child(1)').click();
    cy.get('[data-testid="api-name"] input').clear().type(apiName);
    cy.get('[data-testid="api-version"] input').clear().type(apiVersion);
    if (apiBasePath) {
      cy.get('[data-testid="api-basepath"] input').clear().type(apiBasePath);
    }
    cy.get('[data-testid="api-endpoint"] input').clear().type(endpoint);
    cy.get("button>span").contains("Create").click();
    
    Utils.saveProjectData(fileID);

    cy.get('[data-testid="delete-all-operations-btn"]', {
      timeout: 120000,
    }).should("be.visible");

    Utils.saveComponentURL(fileID);
  }

  static createOpenApi(filepath: string = "", url: string = "") {
    cy.get('[role="dialog"] ul>div:nth-child(2)').click();

    if (filepath) {
      cy.get('[data-testid="open-api-file"]').click();
      cy.get('input[type="file"]').attachFile(filepath);
    }

    if (url) {
      cy.get('[data-testid="open-api-url"]').click();
      cy.get('[data-testid="swagger-file-url"]>div>input').type(url);
    }

    cy.get('[id="next"]').click();
  }

  static enterAPIdetails(
    apiName: string,
    apiBasePath: string,
    endpoint: string,
    version: string = "",
    validateResourceName: string = "",
    key:string
  ) {
    cy.get('[data-testid="api-name"]>div>input').clear().type(apiName);

    if (version) {
      cy.get('[data-testid="api-version"]>div>input').clear().type(version);
    }

    cy.get('[data-testid="api-basepath"]>div>input').clear().type(apiBasePath);
    cy.get('[data-testid="api-endpoint"]').within(() => {
      cy.get("p").contains("Mui-error").should("not.exist");
    });
    if (endpoint) {
      cy.get('[data-testid="api-endpoint"]>div>input').clear().type(endpoint);
    }
    cy.get("button>span").contains("Create").click();
    Utils.saveProjectData(key);
    cy.get(`[data-testid="resource-/intensity"]`, { timeout: 120000 }).should(
      "be.visible"
    );
    Utils.saveComponentURL(key);
    let resourceIdentifier = "resource-/intensity";
    if (validateResourceName) {
      resourceIdentifier = "resource-/" + validateResourceName;
    }

    cy.get(`[data-testid="${resourceIdentifier}"]`, { timeout: 120000 }).should(
      "be.visible"
    );
  }

 
}
