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
import { RestAPIProxyTemplate } from "../templates/rest-api-proxy-temp";
import { RestAPITemplate } from "../templates/rest-api-temp";
import { TriggersTemplate } from "../templates/slackTrigger-creation-temp";

export class ProjectOverviewPage {
  static selectComponent(fileID) {
    cy.get("td>div>p").contains(fileID).click();
  }

  static addNewComponent() {
    cy.contains("Time to create your first component", {
      timeout: 120000,
    }).should("be.visible");
    cy.get(".MuiContainer-root button").click(); // Need to add a id for the Create button
  }

  static addComponent() {
    cy.get('[data-cyid="create-component"]').click();
  }

  //Only used for Enterprise login TC
  static addNewComponentEL() {
    cy.get(".MuiContainer-root button").click({ multiple: true }); // Need to add a id for the Create button
  }
}
