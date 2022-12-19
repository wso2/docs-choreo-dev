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

import { GraphQL } from "../../apis/graphql";
import { Region } from "../enum/regions";



export class ProjectListingPage {

  static createNewProject(projectName: string, description: string, dataPlane: Region = Region.US) {
    cy.get('[data-testid="project-picker"]>div').click();
    cy.get('[data-cyid="btn-create-new"]').focus().click().wait(3000);
    cy.get('[name="Name"]').clear().type(projectName);
    cy.get('[name="Description"]').clear().type(description);
    cy.get('[aria-label="Without label"]').click()
    cy.get(`[data-value="${dataPlane}"]`).click()
    cy.get('[data-testid="create-version-create"]').click();
    cy.get('[data-cyid="create-component"]').should('be.visible')
  }

  static selectProject(projectName: string = "Default Project") {
    cy.get('[data-testid="project-picker"]').click();
    cy.get(`li>div`).contains(projectName).click();
  }
}
