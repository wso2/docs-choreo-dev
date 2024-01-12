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

import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { ManualTrigger } from "../../entities/component/manual-trigger-component";
import { Types } from "../../../commons/types";
import { Enums } from "../../../commons/enums";
import { VERY_SHORT_TIME } from "../../../commons/timeouts";

export interface ExecuteFeature {
  _execute(component: ManualTrigger, env: Enums.Environment);
}

export function mixinExecute<T extends Types.Constructor>(
  base: T
): Types.Constructor<ExecuteFeature> & T {
  return class extends base {
    //export class _ManualTriggerExecute {
    private sideMenu = new ServiceLeftMenu();

    _execute(component: ManualTrigger, env: Enums.Environment) {
      this.sideMenu.navigateToExecute();
      this.selectEnvironment(env);
      this.executeManualTrigger(component);
    }

    private selectEnvironment(env: Enums.Environment) {
      cy.get('[data-cyid="environment-picker"]')
        .should("be.visible")
        .scrollIntoView()
        .click();
      cy.get(`[data-cyid="environment-picker-${env}"]`).click();
    }

    private executeManualTrigger(component: ManualTrigger) {
      cy.get(TestIds.runNow).should("be.enabled").click();
      cy.get(TestIds.runNow).should("be.enabled");

      cy.get(TestIds.refreshTasks).should("be.visible").click();

      cy.get(TestIds.executionCount, VERY_SHORT_TIME).then(($count) => {
        expect(Number($count.text())).gt(0);
      });
    }
  };
}
