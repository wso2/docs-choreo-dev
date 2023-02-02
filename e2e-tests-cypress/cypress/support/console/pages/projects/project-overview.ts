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
import { Enums } from "../../enums";
import { Utils } from "../../utils";

export class ProjectOverviewPage {
  static selectComponent(fileID) {
    cy.get("td>div>p").contains(fileID).click();
  }

  static addNewComponent() {
    cy.get(".MuiContainer-root button").click(); // Need to add a id for the Create button
  }


  static searchReuseComponent(reuseComponentName) {
    
    const PROJECT_NAME = "Default Project";
    const SCHEDULE_NAME = "create-ReuseScheduleTrigger-1.7.1";
    const REPO_NAME = Utils.generateComponentName("repo");

    cy.get('tbody').then(bdy => {
      if(bdy.find(`[title="${reuseComponentName}"]`).length>0) {
    cy.log("Reuse component exists")
          }else{
  
        let componentData: ComponentData = {
        componentName: SCHEDULE_NAME,
        displayType: Enums.DisplayType.scheduledTask,
        accessibility: Enums.Accessibility.EXTERNAL,
        projectName: PROJECT_NAME,
        triggerChannels: "",
        triggerId: null,
        srcGitRepoUrl: "https://github.com/choreo-test-apps/schedule-trigger",
        initializeAsBallerinaProject: false,
        repositoryType: Enums.RepoType.UserManagedNonEmpty,
        repositorySubPath: "",
        sampleTemplate: "",
  };
  
   GraphQL.createComponentWithRepo(componentData, REPO_NAME);
  }
  })}
  

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

  //Only used for Enterprise login TC
  static addNewComponentEL() {
    cy.get(".MuiContainer-root button").click({ multiple: true }); // Need to add a id for the Create button
  }
}
