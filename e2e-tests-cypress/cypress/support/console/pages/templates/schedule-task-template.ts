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


import { Utils } from "../../utils";

export class ScheduleTask {
  static selectTask() {
    cy.get('[data-testid="project-template-list-scheduleTask"]').click();
  }

  static createTask(name: string, description: string) {
    cy.get('.MuiDialog-paperScrollPaper ul>div>div').eq(0).should('be.visible').click()
    cy.get('[name="name"]').clear().type(name);
    cy.get('[data-cyid="create-scheduled-task-next"]').click();
    cy.get('[data-cyid="choreo-managed-repo-radio-btn"]').click();
    cy.get('[data-cyid="btn-create-"]').click();
    cy.setCookie("fidpId", "choreoe2etest");
    Utils.saveComponentURL();

  }
}
