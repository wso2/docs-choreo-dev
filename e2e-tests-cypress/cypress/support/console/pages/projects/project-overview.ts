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

import { ComponentData } from "../../../interfaces/component-data";
import { GraphQL } from "../../apis/graphql";
import { Utils } from "../../utils";

export class ProjectOverviewPage {

  static searchReuseComponent(componentData: ComponentData) {

    const REPO_NAME = Utils.generateComponentName("repo");
    cy.wait(5000)
    cy.get('body').then(bdy => {
      if (bdy.find('tbody').length > 0) {
        let isFound: boolean = false;
        const kk = bdy.find('p')
        for (let i = 0; i < kk.length; i++) {
          if (kk[i].innerText === componentData.componentName) {
            isFound = true;
            break;
          }
        }
        if(!isFound){
          GraphQL.createComponentWithRepo(componentData, REPO_NAME);
        }
      }else{
        GraphQL.createComponentWithRepo(componentData, REPO_NAME);
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
    cy.contains('← Components').click()
  }

  private static waitForTemplateCardsToLoad() {
    cy.get('[data-cyid="scheduleTask"]')
      .get('[data-testid="project-template-list-scheduleTask"]')
      .should("be.enabled")
      .get('[data-cyid="manualTrigger"]')
      .get('[data-testid="project-template-list-manualTrigger"]')
      .should("be.enabled")
      .get('[data-cyid="httpProxyApi"]')
      .get('[data-testid="project-template-list-httpProxyApi"]')
      .should("be.enabled")
      .get('[data-cyid="httpApi"]')
      .get('[data-testid="project-template-list-httpApi"]')
      .should("be.enabled")
      .get('[data-cyid="httpApi"]');
  }

  static addComponent() {
    cy.get('[data-cyid="create-component"]').click();
  }
}
