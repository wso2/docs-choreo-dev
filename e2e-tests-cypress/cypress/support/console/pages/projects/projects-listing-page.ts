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
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { GRAPHQL_URL } from "../../../commons/urls";
import { Utils } from "../../../commons/utils";
import { ChoreoHomePage } from "../home/home-page";
import { ProjectOverviewPage } from "./project-overview";

export class ProjectListingPage {
  static createNewProject(
    projectName: string,
    description: string,
    dataPlane: Enums.Region = Enums.Region.US
  ) {
    ChoreoHomePage.navigateToHome();
    this.checkProjectCardCreation();
    cy.get('[data-cyid="project-name"]').clear().type(projectName);
    cy.get('[data-cyid="project-description"]').clear().type(description);
    cy.get('[data-testid="Multi Repository-radio-card"]').click();
    Utils.getRenderedElement(
      '[data-cyid="create-project-stepper-submit-button"]'
    ).click();
    cy.get('[data-cyid="create-project-stepper-submit-button"]').should(
      "not.exist"
    );
    ProjectOverviewPage.waitForTemplateCardsToLoad();
  }

  static checkProjectCardCreation() {
    cy.url().then((url) => {
      if (url.includes("projects")) {
        Utils.getRenderedElement('[data-testid="project-picker"]').click();
        Utils.getRenderedElement('[data-cyid="btn-create-new"]').click();
      } else {
        this.getCreateNewProjectPopUp();
      }
    });
  }

  static getCreateNewProjectPopUp(retryCount: number = 0) {
    retryCount++;
    if (retryCount > 10) {
      return;
    }

    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="create-project-card"]').length > 0) {
        cy.get('[data-cyid="create-project-card"]').click();
        cy.wait(VERY_SHORT_TIME.timeout);
      } else {
        cy.log("Retry count: " + retryCount);
        this.getCreateNewProjectPopUp(retryCount);
      }
    });

    cy.log("Verify the PopUP is displayed");
    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="project-name"]').length > 0) {
        cy.get('[data-cyid="project-name"]').should("be.visible");
        return;
      } else {
        this.getCreateNewProjectPopUp(retryCount);
      }
    });
  }

  static selectProject(projectName: string = "Default Project") {
    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="create-project-card"]').length > 0) {
        cyGet('[data-cyid="search-icon"]').eq(0).click();
        cyGet('[data-testid="search-field"]').type(`${projectName}{enter}`);
        cy.contains(projectName).click();
      } else {
        Utils.getRenderedElement("#project-picker").click();
        cy.wait(3000);
        cyGet('ul>li [placeholder="Search"]').type(`${projectName}{enter}`);
        cyGet("ul>li>div>span>p").contains(projectName).click();
      }
    });
  }
}
