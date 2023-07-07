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

import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
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

  it("Verify Choreo filter functionality", () => {
    ChoreoHomePage.goToMarketplacePage();
    Marketplace.filterByChoreo();
    Marketplace.clearSelectedFilters();
  });

  it("Verify my organization filter functionality", () => {
    Marketplace.filterByMyOrganization();
    Marketplace.clearSelectedFilters();
  });

  it("Verify price by Free filter functionality", () => {
    Marketplace.filterByFree();
    Marketplace.validateConnectorPopulation([FREE, FREEMIUM]);
    Marketplace.clearSelectedFilters();
  });

  it("Verify price by Freemium filter functionality", () => {
    Marketplace.filterByFreemium();
    Marketplace.validateConnectorPopulation([FREEMIUM]);
    Marketplace.clearSelectedFilters();
  });

  it("Verify price by Paid filter functionality", () => {
    Marketplace.filterByPaid();
    Marketplace.validateConnectorPopulation([PAID]);
    Marketplace.clearSelectedFilters();
  });

  it("Verify filter functionality by main category", () => {
    Marketplace.filterByCategory(MAIN_CATEGORY1, "");
    Marketplace.validateConnectorPopulation([MAIN_CATEGORY1]);
    Marketplace.clearSelectedFilters();
  });

  it("Verify filter functionality by main category and subcategory", () => {
    Marketplace.filterByCategory(MAIN_CATEGORY2, SUB_CATEGORY);
    Marketplace.validateConnectorPopulation([SUB_CATEGORY]);
    Marketplace.clearSelectedFilters();
  });

  it("Verify Connector search functionality", () => {
    Marketplace.searchConnector(CONNECTOR).should("have.length", 1);
    Marketplace.getConnectorName().should("eq", CONNECTOR);
    Marketplace.getConnectorTags().should("have.length", 2);
  });

  it("Verify Trigger search functionality", () => {
    Marketplace.navigateToTriggersTab();
    Marketplace.searchTrigger(TRIGGER).should("have.length", 1);
    Marketplace.getTriggerName().should("eq", TRIGGER);
    Marketplace.getTriggerTags().should("have.length", 3);
  });
});