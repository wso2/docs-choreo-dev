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

export class RestAPITemplate {
  static selectHttpAPITemplate() {
    cy.get('[data-testid="project-template-list-httpApi"]').click();
  }

  static createApiFromScratch(
    componentName: string,
    description: string
  ) {
    cy.get('[role="dialog"] ul>div:nth-child(1)').click();
    cy.get('[name="name"]').clear().type(componentName);
    cy.get('input[name="description"]').clear().type(description);
    cy.get('[data-cyid="create-api-from-scratch-next"]').click();
    cy.get('[data-cyid="choreo-managed-repo-radio-btn"]').click();
    cy.get('[data-testid="create-api-from-scratch-submit"]').click();
  }
}
