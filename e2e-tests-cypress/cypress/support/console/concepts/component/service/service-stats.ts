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

import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";

export class _ServiceStats {
  private sideMenu = new ServiceLeftMenu();

  viewUsageInsights() {
    this.sideMenu.navigateToUsageInsights();
    this.verifyUsageInsights();
  }

  navigateFromComponentToProjectInsights() {
    this.sideMenu.navigateToUsageInsights();
    this.navigateToProjectInsights();
  }

  private verifyUsageInsights() {
    cy.contains("Coming Soon").should("be.visible");
  }

  private navigateToProjectInsights() {
    cy.get(TestIds.projectInsights).should("be.visible").click();
    cy.get(TestIds.backdropLoader).should("not.exist");
  }
}
