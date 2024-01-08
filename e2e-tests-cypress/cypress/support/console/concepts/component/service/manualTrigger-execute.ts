/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { ManualTrigger } from "./manualTrigger-component";

export class _ManualTriggerExecute {
  private sideMenu = new ServiceLeftMenu();

  execute(component: ManualTrigger ) {
    this.sideMenu.navigateToExecute();
    this.executeManualTrigger(component);
  }

  private executeManualTrigger(component: ManualTrigger) {
    cy.get(TestIds.runNow).should("be.enabled").click();
    cy.get(TestIds.runNowNotification).should("be.visible").contains('Task triggered successfully');
    cy.log("Task executed Successfully");
  }


}
