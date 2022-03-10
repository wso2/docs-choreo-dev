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

export class DevPortalHomePage {
  static username = "[data-testid=signedin-user-menu-btn]";

  static navigateToHome(): void {
    cy.get('[data-testid="main-left-nav-item-Home"]')
      .should("be.visible")
      .click();
  }

  static clickOnLoggedInUser(): void {
    cy.get(this.username).click();
  }

  static logout(): void {
    cy.window()
      .its("sessionStorage")
      .invoke("getItem", "sign_out_url")
      .then((url) => {
        cy.request(url);
      });
  }

  static navigateToApisPage(): void {
    cy.get("[data-testid=apis-appbar-btn]").should("be.visible").click();
  }

  static navigateToAppsPage() {
    cy.get('[data-testid="applications-appbar-btn"]')
      .should("be.visible")
      .click();
    this.interceptApplications();
  }

  private static interceptApplications() {
    cy.intercept(
      "https://sts.preview-dv.choreo.dev/api/am/devportal/v2/applications/?organizationId=*"
    ).as("apps");
    cy.wait("@apps", { timeout: 180000 }).then((intercept) => {
      const orgId = intercept.request.url.split("organizationId=")[1];

      const header = intercept.request.headers.authorization;
      const apps = intercept.response.body.list as [];
      const headers = {
        Authorization: `${header}`,
      };
      apps.forEach((app) => {
        let appId = app["applicationId"];
        Utils.sendDeleteRequest(
          `https://sts.preview-dv.choreo.dev/api/am/devportal/v2/applications/${appId}?organizationId=${orgId}`,
          headers
        );
      });
    });
  }
}
