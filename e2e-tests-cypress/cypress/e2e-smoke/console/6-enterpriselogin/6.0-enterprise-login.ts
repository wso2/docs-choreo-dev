/*

Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
This software is the property of WSO2 Inc. and its suppliers, if any.
Dissemination of any information or reproduction of any material contained
herein is strictly forbidden, unless permitted by WSO2 in accordance with
the WSO2 Commercial License available at http://wso2.com/licenses.
For specific language governing the permissions and limitations under
this license, please see the license as well as any agreement you’ve
entered into with WSO2 governing the purchase of this software and any
associated services.
*/


import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";


describe("Enterprise Login using auth0Idp", () => {


  before(() => {
    cy.request(Cypress.env("auth0LogoutUrl"), {
      client_id: Cypress.env("auth0ClientID"),
      returnTo: Cypress.env("enterpriseLoginUrl"),
    });
  });

  after(() => {
    ChoreoHomePage.logout();
  });

  it("Enterprise login to console", () => {
    LoginPage.enterpriseLogin();
  });


  it("Verify devportal sso login", () => {
    ComponentOverviewPage.navigateToDevPortal().should(
      "eq",
      "API Developer Portal"
    );
  });
})
