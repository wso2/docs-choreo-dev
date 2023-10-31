/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { BUILD_SUCCESS } from "../../../../commons/constants";
import { MEDIUM_TIME, VERY_SHORT_TIME } from "../../../../commons/timeouts";

export class ComponentBuild {
  static buildComponent() {
    cy.get('[data-cyid="build-button"]').should("be.enabled").click();
    cy.get('[data-cyid="btn-next-button"]').should("be.visible").click();
    cy.get('[data-cyid="btn-next-button"]').should(
      "not.be.visible",
      VERY_SHORT_TIME
    );
    cy.get('[data-cyid="table-title"]').within(() => {
      cy.contains(BUILD_SUCCESS, MEDIUM_TIME);
    });
  }
}
