import { is } from "cypress/types/bluebird";
import { cyGet, cyLog } from "../../../commons/cy";
import { MEDIUM_TIME, MENU_RENDERING_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

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
export class ComponentOverviewPage {
  static goBackToProject() {
    cy.get('[data-cyid="project-picker-button"]').should("be.visible").click();
  }

  static copyURL(env: string) {
    cy.get("body").then((body) => {
      const endpointCount = body.find(
        '[data-cyid="text-field-endpoint"]'
      ).length;

      for (let i = 0; i < endpointCount; i++) {
        cy.get('[data-cyid="text-field-endpoint"]')
          .eq(i)
          .within(() => {
            cy.get("input")
              .invoke("val")
              .then((text) => {
                if (text.toString().includes(env)) {
                  cy.log(`url of ${env}: `, text.toString());
                  cy.wrap(text).as(env);
                }
              });
          });
      }
    });
  }

  static navigateToBuild() {
    this.scrollToTopOfMenu();
    cy.get("[data-cyid=link-build]").should("be.visible").realHover({ position: "left" })
      .wait(MENU_RENDERING_TIME).click({ force: true })
      .wait(MENU_RENDERING_TIME);
    cy.get('[id="backdrop-loader"]').should("not.exist");
    Utils.moveMouseAwayFromLeftMenu();
  }

  static navigateToDeploy() {
    this.scrollToTopOfMenu();
    cy.get("[data-cyid=link-deploy]")
      .should("be.visible")
      .realHover({ position: "left" })
      .wait(MENU_RENDERING_TIME)
      .click({ force: true })
      .wait(MENU_RENDERING_TIME);
    cy.get('[id="backdrop-loader"]').should("not.exist");
    Utils.moveMouseAwayFromLeftMenu();
  }

  static navigateToExecute() {
    this.scrollToTopOfMenu();
    cy.get("[data-cyid=link-execute]").should("be.visible")
      .realHover({ position: "left" })
      .wait(MENU_RENDERING_TIME)
      .click({ force: true })
      .wait(MENU_RENDERING_TIME);
    cy.get('[id="backdrop-loader"]').should("not.exist");
    Utils.moveMouseAwayFromLeftMenu();
  }

  static navigateToTest() {
    this.navigateToSubMenu(
      '[data-cyid="link-test"]',
      new Array(
        '[data-cyid="graphql"]',
        '[data-cyid="testConsole"]',
        '[data-cyid="openapi"]',
        '[data-cyid="curl"]'
      )
    );
    Utils.moveMouseAwayFromLeftMenu();

    cy.get('[id="backdrop-loader"]').should("not.exist");
  }

  static navigateToOverview() {
    cy.get('[data-cyid="home"]')
      .realHover({ position: "left" })
      .wait(MENU_RENDERING_TIME)
      .click({ force: true })
      .wait(MENU_RENDERING_TIME);
    cy.get('[id="backdrop-loader"]').should("not.exist");
    Utils.moveMouseAwayFromLeftMenu();

    cy.get("[data-cyid=create-time]").should("be.visible");
  }

  static navigateToManage() {
    this.navigateToSubMenu(
      '[data-cyid="link-manage"]',
      new Array('[data-cyid="manage-overview"]')
    );
    Utils.moveMouseAwayFromLeftMenu();
  }

  static navigateToObserve(timeToWait = 0) {
    cy.wait(timeToWait);
    cy.get('[data-cyid="observability"]').should("be.visible").click();
  }

  static navigateProxyResources() {
    cy.xpath('//div[@id="root"]/div/div/div/div[2]/div[1]')
      .realHover()
      .wait(2000);
    cyGet('[data-cyid="link-develop"]').should("be.visible").click();
    cyGet('[data-cyid="develop-resources"]');
    cy.xpath('//div[@id="root"]/div/div/div/div[2]').realHover({
      position: "right",
    });
  }

  static navigateToDevelop() {
    this.navigateToSubMenu(
      '[data-cyid="link-develop"]',
      new Array('[data-cyid="develop-resources"]')
    );
    Utils.moveMouseAwayFromLeftMenu();
  }

  private static scrollToTopOfMenu() {
    cy.get('[data-cyid="left-navigation"]')
      .should("be.visible")
      .realHover({ position: "center" })
      .wait(MENU_RENDERING_TIME)
      .realMouseWheel({
        deltaY: -100,
      });
  }

  static navigateToDevPortal() {
    cy.get("[data-cyid='developer-portal-link']")
      .invoke("attr", "href")
      .then((href) => cy.visit(href));
    return cy.get("header>div>div>p").invoke("text");
  }

  static createNewVersion(version: string, newBranch: string) {
    cy.get('[data-cyid="version-picker"]').click();
    cy.get("[data-cyid=btn-create-version-button]").click();

    if (newBranch) {
      this.createNewVersionRestApi(version, newBranch);
    } else {
      this.createNewVersionApiProxy(version);
    }
    cy.wait(3000);
  }

  private static createNewVersionApiProxy(version: string) {
    cy.get("[data-testid=create-version-create]", MEDIUM_TIME).should(
      "be.visible"
    );

    cy.get('[data-cyid="text-field-new-version"]').within(() => {
      cy.get("input").clear().type(version);
    });
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }

  private static createNewVersionRestApi(version: string, branch: string) {
    cy.get('[data-testid="dialog-close-icon"]').should("exist");
    cy.get('div>[aria-label="Without label"]').click();
    cy.get(`[data-value=${branch}]`).click();
    cy.get('[data-cyid="text-field-new-version"]>div>input')
      .clear()
      .type(version);
    cy.get("[data-testid=create-version-create]").click();
    cy.get('[data-testid="dialog-close-icon"]').should("not.exist");
  }

  private static navigateToSubMenu(
    mainMenuSelector: string,
    subMenuSelectors: string[]
  ) {
    cy.get("body").then((bdy) => {
      let subMenuSelector: string;
      let isSubmenuExpanded = false;
      // Check if at least one of the sub menus are visible
      for (subMenuSelector of subMenuSelectors) {
        if (bdy.find(subMenuSelector).length > 0) {
          isSubmenuExpanded = true;
          break;
        }
      }
      // Sub menu is collapsed
      if (!isSubmenuExpanded) {
        // Expand sub menu
        cy.get(mainMenuSelector)
          .should("be.visible")
          .realHover({ position: "left" })
          .wait(MENU_RENDERING_TIME)
          .click()
          .wait(MENU_RENDERING_TIME);

        // Click on anyone of the sub menus that are found first
        cy.get("body").then((bdy) => {
          for (const selector of subMenuSelectors) {
            if (bdy.find(selector).length > 0) {
              cy.get(selector)
                .should("be.visible")
                .realHover({ position: "left" })
                .wait(MENU_RENDERING_TIME)
                .click({ force: true });
              break;
            }
          }
        });
      } else {
        // Sub menu is expanded but click to ensure that relevant page is loaded
        // in case we are navigating from a different page
        cy.get(subMenuSelector)
          .should("be.visible")
          .realHover({ position: "left" })
          .wait(MENU_RENDERING_TIME)
          .click();
      }
    });
  }

  static getServiceInvokeUrl(visibility: string) {
    cyGet("td>div>div>div")
      .eq(1)
      .invoke("text")
      .then((url) => {
        cy.log(url);
        Cypress.env(`${visibility}_URL`, url);
      });
  }

  static navigateToDevops() {
    cyGet('[data-cyid="advanced-devops"]').click();
  }

  static navigateToRuntime() {
    cy.contains("Runtime").click();
  }

  static generateProxyUrl(visibility: string) {
    cyGet('[data-cyid="release-information"]>input')
      .eq(2)
      .invoke("attr", "value")
      .then((namespace) => {
        const url: string = Cypress.env(`${visibility}_URL`);
        const _prts: string[] = url.split(":");
        const _proxyUrl: string = `${_prts[0]}:${_prts[1]}.${namespace}:${_prts[2]}`;
        Cypress.env("PROXY_URL", _proxyUrl);
        cyLog(_proxyUrl);
      });
  }
}
