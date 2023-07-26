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

import { cyGet } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { SHORT_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

export class ComponentTestPage {
  static selectCurl() {
    if (Utils.isUnifiedMenuEnabled()) {
      cy.get('[data-testid="cURL"]').click();
    } else {
      cy.get('[data-cyid="curl"]').click();
    }
  }

  static getTestKey() {
    cy.contains("Get Test Key", SHORT_TIME).should("be.visible").click();
  }

  static selectEnvironment(env: Enums.Environment) {
    cy.get('[data-testid="env"]>div[role="button"]').click();
    cy.get("ul>li").contains(env).click();
    cy.wait(1000); // Wait for the environment to be selected
  }

  static selectEndpoint(endpoint: string) {
    Utils.getRenderedElement('[data-cyid="select-endpoint-select"]').click();
    Utils.getRenderedElement('ul>li[role="option"]').contains(endpoint).click();
  }
}
