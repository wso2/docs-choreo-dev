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

export class TriggersTemplate {
  static SelectWebhookTemplate() {
    cy.get('[data-testid="project-template-list-webhook"]').click();
  }

  static createSlackTriggerFromTemplate(webhookName: string) {
    cy.get('input[name="webhookName"]').clear().type(webhookName);
    cy.get("#mui-component-select-triggerType").click();
    cy.get('[data-cyid="Slack"]').click();
    cy.get("#mui-component-select-triggerChannel").click();
    cy.get('[data-cyid="SlackEventsAppService"]').click();
    cy.get('[data-cyid="create-webhook-next"]').click();
    cy.get('[data-cyid="choreo-managed-repo-radio-btn"]').click();
    cy.get('[data-cyid="btn-create"]').click();
    Utils.saveProjectData()
  }

}
