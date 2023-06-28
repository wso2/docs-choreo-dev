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

import { version } from "chai";
import { Enums } from "../../../commons/enums";
import { Utils } from "../../../commons/utils";
import { AbsComponent } from "../../../interfaces/abs-component";
import { GraphQLQueryBuilder } from "../../apis/gql-query-builder";
import { GraphQL } from "../../apis/graphql";
import { APIDevelop } from "../apis/api-develop";
import { RestAPIProxyTemplate } from "../templates/rest-api-proxy-temp";
import { cyGet, cyLog } from "../../../commons/cy";

export class ProjectOverviewPage {
  static searchReuseComponent(
    componentData: AbsComponent,
    projectName: string = "Default Project",
    isComponentBYOC: boolean = false
  ) {
    const REPO_NAME = Utils.generateComponentName("repo");
    GraphQL._getComponentByName(projectName, componentData.componentName).then((comps) => {
      if (comps.displayName != componentData.componentName) {
        if (isComponentBYOC) {
          GraphQL.createComponent(
            projectName,
            REPO_NAME,
            componentData,
            GraphQLQueryBuilder.getBYOCComponentCreationQuery
          );
        } else {
          GraphQL.createComponent(
            projectName,
            REPO_NAME,
            componentData,
            GraphQLQueryBuilder.getRestComponentCreationQuery
          );
        }
      }
    })
  }


  static createHttpProxyAPI() {
    this.waitForTemplateCardsToLoad();
    cy.get('[data-testid="project-template-list-httpProxyApi"]')
      .should("be.visible")
      .click();
  }

  static navigateToComponents() {
    cy.contains("← Components").click();
  }

  static waitForTemplateCardsToLoad() {
    cy.get('[data-cyid="scheduleTask"]')
      .get('[data-testid="project-template-list-scheduleTask"]')
      .should("be.enabled")
      .get('[data-cyid="manualTrigger"]')
      .get('[data-testid="project-template-list-manualTrigger"]')
      .should("be.enabled")
      .get('[data-cyid="httpProxyApi"]')
      .get('[data-testid="project-template-list-httpProxyApi"]')
      .should("be.enabled")
  }

  static addComponent() {
    cy.get('[data-cyid="create-component-button"]').click();
  }
}
