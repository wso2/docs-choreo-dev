import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { TestIds } from "../../../constants/TestIds";
import { Proxy } from "./proxy-component";
import { VERY_SHORT_TIME } from "../../../../commons/timeouts";

export class _ProxyOverview {
  private menu = new ProxyLeftMenu();

  navigateToDevPortal(component: Proxy, idp: string) {
    this.menu.navigateToOverview();
    cy.get(TestIds.createTime).should("be.visible");
    cy.get(TestIds.devPortalLink)
      .invoke("attr", "href")
      .then((href) => {
        const linkParts = href.split("?");
        const url = linkParts[0];
        const queryParams = linkParts[1].split("&amp;");

        let updatedQueryParams = "";

        for (let i = 0; i < queryParams.length; i++) {
          const keyValues = queryParams[i].split("=");

          if (keyValues[0] === "idp") {
            updatedQueryParams += `${keyValues[0]}=${idp}`;
          } else {
            updatedQueryParams += queryParams[i];
          }
        }

        cy.visit(`${url}?${updatedQueryParams}`).then(() => {
          cy.get(TestIds.backdropLoader).should("not.exist");
          cy.get(TestIds.apiNameDevPortal)
            .should("be.visible")
            .contains(component.getName(), VERY_SHORT_TIME);
        });
      });
  }
}
