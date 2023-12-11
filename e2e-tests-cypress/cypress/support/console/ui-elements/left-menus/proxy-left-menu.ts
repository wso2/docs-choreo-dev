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
}
