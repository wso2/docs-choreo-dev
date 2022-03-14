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
import { Utils } from "../../../console/utils";
import { STANDARD_TIME_OUT } from "../../constants";

export class Apis {
  static afterTwoMinutes = 0;

  static navigateToApiOverview(apiName: string): void {
    cy.log("Navigating to Overview");
    cy.get('[data-testid="apis-appbar-btn"]', { timeout: STANDARD_TIME_OUT })
      .should("be.visible")
      .click();
    cy.log("Searching the API");
    cy.get("#outlined-search-bar-api-listing").clear();
    cy.get("#outlined-search-bar-api-listing", {
      timeout: STANDARD_TIME_OUT,
    }).type(apiName + "{enter}");
    cy.get('[data-testid="apiCard-' + apiName + '"]')
      .first()
      .should("be.visible")
      .click();
    cy.log("Successfully navigated to Overview");
  }

  static verifyAPIname() {
    return cy
      .get('[data-testid="txt-api-name"]', { timeout: 120000 })
      .should("be.visible")
      .invoke("text");
  }

  static searchApiAndSelect(textApiName, versionCount = 1) {
    cy.get("[data-testid=apis-appbar-btn]").click();

    cy.intercept(
      "GET",
      `${Cypress.env("apimSvcURL")}/api/am/devportal/v2/apis?organizationId=*`
    ).as("apis");

    cy.wait("@apis", { timeout: 40000 }).then((intercept) => {
      const splitArr = intercept.request.url.split("apis?");
      const url = `${splitArr[0]}apis?query=name:${textApiName}&${splitArr[1]}`;
      const header = intercept.request.headers.authorization;

      Cypress.env("devportal_auth", header);

      this.afterTwoMinutes= Date.now() + 180000
      this.verifyAPI(url, header, versionCount);
    });
    this.searchAPI(textApiName);
  }

  private static searchAPI(textApiName) {
    cy.get("#outlined-search-bar-api-listing")
      .focus()
      .type(`${textApiName}{enter}`);
    cy.get("[data-testid=apiCard-" + textApiName + "]")
      .last()
      .click();
  }

  private static verifyAPI(url, header, versionCount) {
    const headers = {
      Authorization: `${header}`,
    };
    Utils.sendGetRequest(url, headers).then((res) => {
      if (res.body.list.length == versionCount) {
        return;
      } else {
        let k = this.afterTwoMinutes - Date.now();
        if (k >= 0) {
          cy.wait(60000);
          this.verifyAPI(url, header, versionCount);
        }
      }
    });
  }
}
