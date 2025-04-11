/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { LeftMenu } from "./left-menu";

export class ProxyLeftMenu extends LeftMenu {
  navigateToOverview() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=home]");
  }

  navigateToDevelop() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-develop]",
      new Array('[data-cyid="develop-resources"]')
    );
  }

  navigateToPolicies() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-develop]",
      new Array('[data-cyid="develop-policies"]')
    );
  }

  navigateToDeploy() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=link-deploy]");
  }

  navigateToTest() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-test]",
      new Array(
        '[data-cyid="graphql"]',
        '[data-cyid="testConsole"]',
        '[data-cyid="openapi"]',
        '[data-cyid="curl"]'
      )
    );
  }

  navigateToManage() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-overview"]')
    );
  }

  navigateToBuild() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=link-deploy]");
    this.navigateToMenuItem("[data-cyid=link-build]");
  }

  navigateToLifecycle() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-lifecycle"]')
    );
  }

  navigateToUsagePlan() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-usage"]')
    );
  }

  navigateToSettings() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-settings"]')
    );
  }

  navigateToPermissions() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-permissions"]')
    );
  }

  navigateToConsumers() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-consumers"]')
    );
  }

  navigateToUsageInsights() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem('[data-cyid="usage-insights"]');
  }
}
