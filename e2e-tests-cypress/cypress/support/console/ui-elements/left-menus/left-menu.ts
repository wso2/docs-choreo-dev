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
    cy.get(TestIds.backdropLoader).should("not.exist");
    this.moveMouseAwayFromLeftMenu();
  }

  protected navigateToSubMenu(
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
