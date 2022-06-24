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


  static createTrigger(triggerType: string, triggerName: string, triggerChannel: string) {
    cy.get('[data-testid="search-field"]>input').clear().type(triggerType);
    cy.get('[data-testid="search-button"]>button').click();
    cy.get(`.package-card-class>div:not([data-testid])`).realHover().wait(2000)
    cy.get('[data-testid="Slack"]').realClick();
    cy.get('[data-testid="webhook-name"]>div>input').clear().type(triggerName);
    cy.get(".MuiAutocomplete-endAdornment").click();
    cy.get("#trigger-channel-select-popup");
    cy.get("li>div>h5").contains(triggerChannel).click();
    cy.get('[data-cyid="create-webhook-next"]').click();
    cy.get('[data-testid="create-btn"]').click();
  }
}
