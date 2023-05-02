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



import { Enums } from "../../../commons/enums";
import { SHORT_TIME } from "../../../commons/timeouts";
import { GRAPHQL_URL } from "../../../commons/urls";
import { Utils } from "../../../commons/utils";
import { ChoreoHomePage } from "../home/home-page";



export class ProjectListingPage {
  static createNewProject(
    projectName: string,
    description: string,
    dataPlane: Enums.Region = Enums.Region.US
  ) {
    ChoreoHomePage.navigateToHome();

    cy.url().then((url) => {
      if (url.includes("projects") && !url.includes("home")) {
        Utils.getRenderedElement('[data-testid="project-picker"]').click();
        Utils.getRenderedElement('[data-cyid="btn-create-new"]').click();
      } else {
        cy.intercept({
          method: "POST",
          url: GRAPHQL_URL,
          times: 10,
        }).as("queryComponents");

        cy.wait("@queryComponents", SHORT_TIME).then(() => {
          Utils.getRenderedElement('[data-cyid="create-project-card"]').click();
        });
      }
    });

    cy.get('[name="Name"]').clear().type(projectName);
    cy.get('[name="Description"]').clear().type(description);
    cy.get('[data-cyid="select-region"]').click();
    cy.contains(`Cloud Data Plane - ${dataPlane}`).click();
    Utils.getRenderedElement('[data-testid="create-version-create"]').click();
    cy.get('[data-testid="create-version-create"]').should("not.exist");
  }

  static selectProject(projectName: string = "Default Project") {
    cy.get("body").then((bdy) => {
      if (bdy.find('[data-cyid="create-project-card"]').length > 0) {
        cy.get('[data-cyid="search-icon"]').eq(0).click();
        cy.get('[data-testid="search-field"]').type(`${projectName}{enter}`);
        cy.contains(projectName).click();
      } else {
        Utils.getRenderedElement("#project-picker").click();
        cy.wait(3000);
        cy.get('ul>li [placeholder="Search"]').type(`${projectName}{enter}`);
        cy.get("ul>li>div>span>p").contains(projectName).click();
      }
    });
  }
}
