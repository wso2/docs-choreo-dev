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

import { MEDIUM_TIME } from "../../../commons/timeouts";

export class ApiOverview {
  static addCommentToApi(apiComment: string): void {
    cy.get("[data-testid=btn-add-comment-open-close]").click();
    cy.log("Opened the comment box");
    cy.wait(500);
    cy.get('[name="newComment"]').type(apiComment).wait(200);
    cy.get("[data-testid=btn-add-comment").click();
    cy.get('[data-testid="txt-comments-count"]').should(
      "have.text",
      "Comments (1)",
      MEDIUM_TIME
    );
    cy.get("[data-testid=txt-no-comments]").should("not.exist");
    cy.log("Comment added successfully");
  }

  static deleteComment(): void {
    cy.log("Deleting the comment");
    cy.get("table > tbody > tr:first").within(() => {
      cy.get('[data-testid="btn-delete-comment"]', MEDIUM_TIME)
        .should("be.visible")
        .click({ force: true });
    });
    cy.get('[class="MuiPopover-root"]')
      .get("button")
      .contains("Yes")
      .click({ force: true });
    cy.get('[data-testid="txt-comments-count"]').should(
      "have.text",
      "Comments (0)"
    );
    cy.get("[data-testid=txt-no-comments]").should("exist");
    cy.log("Successfully deleted the comment");
  }

  static confirmPublicAPIOverview(): void {
    cy.log("Checking for public API Overview elements");
    cy.get('[data-testid="credentials-item-link"]').should("not.exist");
    cy.get('[data-testid="tryout-item-link"]').should("not.exist");
    cy.get('[data-testid="overview-item-link"]').should("exist");
    cy.get('[data-testid="documents-item-link"]').should("exist");
    cy.get('[data-testid="tryout-item-link"]').should("not.exist");
    cy.get('[type="button"]').contains("Try Out").should("not.exist");
    cy.log("Public api view properly rendered");
  }

  static confirmPublicAPIResourcePage(): void {
    cy.log("Checking for public API pages");
    cy.wait(5000);
    cy.get('[data-testid="resources-item-link"]').should("exist").click();
    cy.get('[type="button"]')
      .contains(/swagger \(\/swagger\.json\)/i)
      .should("exist");
    cy.get('[data-testid="get-test-key-btn"]').should("not.exist");
    cy.get('[id="operations-tag-default"]').should("exist");
    cy.log("Public api resource page properly rendered");
  }
}
