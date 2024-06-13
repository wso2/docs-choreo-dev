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

import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { Marketplace } from "../../../support/console/pages/marketplace/marketplace-page";

describe("Verify the functionality in Choreo Marketplace", () => {
  const MAIN_CATEGORY1 = "Business Management";
  const MAIN_CATEGORY2 = "Marketing";
  const SUB_CATEGORY = "Social Media Accounts";
  const CONNECTOR = "Slack";
  const TRIGGER = "GitHub";
  const FREE = "Cost/Free";
  const FREEMIUM = "Cost/Freemium";
  const PAID = "Cost/Paid";

  before(() => {
    cy.clearAllSessionStorage();
    cy.clearAllCookies();
    cy.clearAllLocalStorage()
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });


  it("verify Type filter functionality by internal", () => {
    ChoreoHomePage.goToMarketplacePage();
    Marketplace.filterByInternal();
  });

  it("verify Type filter functionality by third party", () => {
    Marketplace.filterByThirdParty();
  });

  it("verify Network filter functionality by organization", () => {
    Marketplace.filterByOrganization();
  });


  it("verify Network filter functionality by public", () => {
    Marketplace.filterByPublic();
  });

});