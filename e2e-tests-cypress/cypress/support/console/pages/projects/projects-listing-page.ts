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

export class ProjectListingPage {
  static createNewProject(
    projectName: string,
    description: string,
    fileID: string
  ) {
    cy.get('[data-testid="version-picker"]',{timeout:120000}).click();
    cy.get('[aria-labelledby="version-picker"]>button').click();

    cy.get('[name="Name"]').should("be.visible").clear().type(projectName);
    cy.get('[name="Description"]')
      .should("be.visible")
      .clear()
      .type(description);
    cy.get('[data-testid="create-version-create"]').click();
  }

  static selectProject(projectName: string) {
    cy.get('[data-testid="version-picker"]').click();
    cy.get(`li>div`).contains(projectName).click()
  }
}
