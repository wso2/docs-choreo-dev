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
import { MEDIUM_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

export class DevPortalHomePage {
  static logout(): void {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => {
        cy.request(url);
      });
  }

  static verifyDevportalHomePagePublicView(): void {
    cyGet('[data-testid="login-button"]').should("exist");
    cyGet('[data-testid="home-appbar-btn"]').should("exist");
    cy.log("Successfully navigated to public devportal home page");
  }

  static navigateToApisPage(): void {
    cy.wait(5000);
    cy.get("[data-testid=apis-appbar-btn]").click();
  }

  static navigateSelectAPI(apiName: string): void {
    cy.get(`[data-testid="apiCard-${apiName}"`).click();
  }

  static navigateToPerApiView(apiName: string): void {
    cy.wait(60000);
    cy.reload();
    cy.get('[data-testid="txt-api-name"]')
      .should("be.visible")
      .invoke("text")
      .then((t) => {
        expect(t).to.be.equal(apiName);
      });
  }

  static navigateToAppsPage() {
    cy.get('[data-testid="applications-appbar-btn"]')
      .should("be.visible")
      .click();
    this.interceptApplications();
  }

  private static interceptApplications() {
    cy.intercept(
      `${Cypress.env(
        "apimSvcURL"
      )}/api/am/devportal/v2/applications/?organizationId=*`
    ).as("apps");
    cy.wait("@apps", MEDIUM_TIME).then((intercept) => {
      const orgId = intercept.request.url.split("organizationId=")[1];

      const header = intercept.request.headers.authorization;
      const apps = intercept.response.body.list as [];
      const headers = {
        Authorization: `${header}`,
      };
      apps.forEach((app) => {
        let appId = app["applicationId"];
        Utils.sendDeleteRequest(
          `${Cypress.env(
            "apimSvcURL"
          )}/api/am/devportal/v2/applications/${appId}?organizationId=${orgId}`,
          headers
        );
      });
    });
  }
}
