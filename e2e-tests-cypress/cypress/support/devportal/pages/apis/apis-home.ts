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

import { Enums } from "../../../commons/enums";
import { MEDIUM_TIME, SHORT_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

export class Apis {
  static futureTime = 0;

  static navigateToApiOverview(apiName: string): void {
    cy.log("Navigating to Overview");
    cy.get('[data-testid="apis-appbar-btn"]', SHORT_TIME)
      .should("be.visible")
      .click();
    cy.log("Searching the API");
    cy.get("#outlined-search-bar-api-listing").clear();
    cy.get("#outlined-search-bar-api-listing", SHORT_TIME).type(
      apiName + "{enter}"
    );

    Utils.getRenderedElement(`[data-testid="apiCard-${apiName}"]`).click();
    cy.log("Successfully navigated to Overview");
  }

  static verifyAPIname() {
    return cy
      .get('[data-testid="txt-api-name"]', MEDIUM_TIME)
      .should("be.visible")
      .invoke("text");
  }

  static searchApiAndSelect(
    textApiName: string,
    versionCount: number = 1,
    version: string = ""
  ) {
    cy.get("[data-testid=apis-appbar-btn]").click({ force: true });

    cy.intercept(
      "GET",
      `${Cypress.env("apimSvcURL")}/api/am/devportal/v2/apis?organizationId=*`
    ).as("apis");

    cy.wait("@apis", SHORT_TIME).then((intercept) => {
      const splitArr = intercept.request.url.split("apis?");
      const url = `${splitArr[0]}apis?query=name:${textApiName}&${splitArr[1]}`;
      const header = intercept.request.headers.authorization;

      Cypress.env("devportal_auth", header);

      this.futureTime = Date.now() + 60000;
      this.verifyAPI(url, header, versionCount);
    });
    this.searchAPI(textApiName, version);
  }

  static confirmAPIUnavailability(textApiName) {
    cy.get("#outlined-search-bar-api-listing")
      .focus()
      .type(`${textApiName}{enter}`);
    cy.get("[data-testid=apiCard-" + textApiName + "]").should("not.exist");
    cy.log("Successfully verified the api unavailability");
  }

  private static searchAPI(textApiName: string, version: string = "") {
    cy.get("#outlined-search-bar-api-listing")
      .focus()
      .type(`${textApiName}{enter}`);
    if (version == "") {
      Utils.getRenderedElement(`[data-testid="apiCard-${textApiName}"`).click();
    } else {
      Utils.getRenderedElement(`[data-testid="apiCard-${textApiName}"`, 2000)
        .contains(`Version : ${version}`)
        .click();
    }

    // Ensure API Overview page is loaded
    cy.get('[data-testid="li-overview-item-link"]').should("be.visible");
    cy.get('[data-testid="txt-api-name"]')
      .contains(textApiName)
      .should("be.visible");
  }

  private static getInvokeUrl() {
    let urls = [];
    cy.get('[aria-haspopup="listbox"]').click();
    cy.get("ul>li").each(($l) => {
      urls.push($l.attr("data-value"));
    });

    return cy.wrap(urls);
  }

  static verifyInvokeUrl() {
    this.getInvokeUrl().then((urls) => {
      if (Cypress.env("isPrivateOrg")) {
        expect(urls).have.lengthOf(3);
        expect(urls).contains(
          Cypress.env(`${Enums.Environment.DEVELOPMENT}_test_url`)
        );
        expect(urls).contains(
          Cypress.env(`${Enums.Environment.STAGING}_test_url`)
        );
        expect(urls).contains(
          Cypress.env(`${Enums.Environment.PRODUCTION}_test_url`)
        );
      } else {
        expect(urls).have.lengthOf(2);
        expect(urls).contains(
          Cypress.env(`${Enums.Environment.DEVELOPMENT}_test_url`)
        );
        expect(urls).contains(
          Cypress.env(`${Enums.Environment.PRODUCTION}_test_url`)
        );
      }
    });
  }

  private static verifyAPI(url, header, versionCount) {
    const headers = {
      Authorization: `${header}`,
    };
    Utils.sendGetRequest(url, headers).then((res) => {
      if (res.body.list.length == versionCount) {
        return;
      } else {
        let k = this.futureTime - Date.now();
        if (k >= 0) {
          cy.wait(10000);
          this.verifyAPI(url, header, versionCount);
        }
      }
    });
  }
}
