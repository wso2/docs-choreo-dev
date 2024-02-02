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

import { SHORT_TIME } from "../../../commons/timeouts";
import { TestIds } from "../../constants/TestIds";

export class LeftMenu {
  private MENU_RENDERING_TIME = 300; // 0.3 seconds

  protected scrollToTopOfMenu() {
    cy.get('[data-cyid="left-navigation"]')
      .should("be.visible")
      .realHover({ position: "center" })
      .wait(this.MENU_RENDERING_TIME, { log: false })
      .realMouseWheel({
        deltaY: -100,
      });
  }

  protected navigateToMenuItem(menuItemSelector: string) {
    cy.get(menuItemSelector)
      .realHover({ position: "left" })
      .wait(this.MENU_RENDERING_TIME, { log: false })
      .click({ force: true })
      .wait(this.MENU_RENDERING_TIME, { log: false });
    cy.get(TestIds.backdropLoader, SHORT_TIME).should("not.exist");
    this.moveMouseAwayFromLeftMenu();
  }

  protected navigateToSubMenu(
    mainMenuSelector: string,
    subMenuSelectors: string[]
  ) {
    cy.get("body").then((bdy) => {
      let subMenuSelector: string = "";
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
          .wait(this.MENU_RENDERING_TIME, { log: false })
          .click()
          .wait(this.MENU_RENDERING_TIME, { log: false });

        // Click on anyone of the sub menus that are found first
        cy.get("body").then((bdy) => {
          for (const selector of subMenuSelectors) {
            if (bdy.find(selector).length > 0) {
              cy.get(selector)
                .should("be.visible")
                .realHover({ position: "left" })
                .wait(this.MENU_RENDERING_TIME, { log: false })
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
          .wait(this.MENU_RENDERING_TIME, { log: false })
          .click();
      }
    });
    cy.get(TestIds.backdropLoader).should("not.exist");
    this.moveMouseAwayFromLeftMenu();
  }

  private moveMouseAwayFromLeftMenu() {
    cy.get("body").realMouseMove(250, 250);
  }
}
