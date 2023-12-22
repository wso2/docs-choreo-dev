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

import { TestIds } from "./constants/TestIds";
import { VERY_SHORT_TIME } from "../commons/timeouts";

export class DevPortal {
  searchApi(apiName: string, version?: string) {
    cy.get(TestIds.apiBar).click();
    cy.contains("All").should("be.visible");

    cy.get(TestIds.apiSearch)
      .should("be.visible")
      .focus()
      .type(`${apiName}{enter}`);

    if (version) {
      cy.get(TestIds.apiCard(apiName), VERY_SHORT_TIME)
        .should("be.visible")
        .contains(`Version : ${version}`)
        .click();
    } else {
      cy.get(TestIds.apiCard(apiName), VERY_SHORT_TIME)
        .should("be.visible")
        .click();
    }

    // Ensure API Overview page is loaded
    cy.get(TestIds.apiOverviewDevPortal).should("be.visible");
    cy.get(TestIds.apiNameDevPortal).contains(apiName).should("be.visible");
  }
}

// Singleton instance of Choreo Dev Portal
export const devPortal = new DevPortal();
