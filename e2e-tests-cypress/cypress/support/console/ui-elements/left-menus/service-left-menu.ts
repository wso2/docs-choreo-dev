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

export class ServiceLeftMenu extends LeftMenu {
  navigateToOverview() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=home]");
  }

  navigateToBuild() {
    this.scrollToTopOfMenu();
    // A workaround for when the build page is waiting for the build list but the scheduling 
    // of the build is delayed in the backend. In this case the page does not resppond to any actions.
    // Navigating to the deploy page and then the build page will cause the build page to reload
    // & make the page actionable.
    this.navigateToMenuItem("[data-cyid=link-deploy]");
    this.navigateToMenuItem("[data-cyid=link-build]");
  }

  navigateToDeploy() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=link-deploy]");
  }

  navigateToTest() {
    this.scrollToTopOfMenu();
    // A workaround when the test console does not load properly. 
    // If an error is encountered after navigating to the test console and a retry is triggered, 
    // navigating to the deploy page and then the test page will cause the test page to reload.
    this.navigateToMenuItem("[data-cyid=link-deploy]");
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

  navigateToPostman() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-test]",
      new Array('[data-cyid="postman"]')
    );
  }

  navigateToApiChat() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-test]",
      new Array('[data-cyid="apiChat"]')
    );
  }

  navigateToManage() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=link-manage]",
      new Array('[data-cyid="manage-overview"]')
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

  navigateToUsageInsights() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem('[data-cyid="usage-insights"]');
  }

  navigateToExecute() {
    this.scrollToTopOfMenu();
    this.navigateToMenuItem("[data-cyid=link-execute]");
  }

  navigateToMetrics() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=observability]",
      new Array('[data-cyid="metrics"]')
    );
  }

  navigateToRuntimeLogs() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=observability]",
      new Array('[data-cyid="logs-runtime"]')
    );
  }

  navigateToDependencies() {
    this.scrollToTopOfMenu();
    this.navigateToSubMenu(
      "[data-cyid=dependencies]",
      new Array('[data-cyid="connections"]')
    );
  }
}
