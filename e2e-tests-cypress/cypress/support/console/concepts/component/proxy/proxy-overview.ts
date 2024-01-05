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

import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { TestIds } from "../../../constants/TestIds";
import { Proxy } from "./proxy-component";
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../../commons/timeouts";

export class _ProxyOverview {
  private menu = new ProxyLeftMenu();

  navigateToDevPortal(component: Proxy, idp: string) {
    this.menu.navigateToOverview();

    cy.get(TestIds.createTime).should("be.visible");
    cy.get(TestIds.progressBar).should("not.exist");
    cy.get(TestIds.deploymentStatusChip).should("be.visible");

    cy.contains("Requests", SHORT_TIME).should("be.visible");
    cy.contains("Errors", SHORT_TIME).should("be.visible");
    cy.contains("Average TPS", SHORT_TIME).should("be.visible");
    cy.contains("Latency", SHORT_TIME)
      .should("be.visible")
      .wait(VERY_SHORT_TIME.timeout); // Wait for the latency stats to load

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

        component.setDevPortalUrl(`${url}?${updatedQueryParams}`);

        cy.visit(component.getDevPortalUrl()).then(() => {
          cy.get(TestIds.backdropLoader).should("not.exist");
          cy.get(TestIds.apiNameDevPortal)
            .should("be.visible")
            .contains(component.getName(), VERY_SHORT_TIME);
        });
      });
  }
}
