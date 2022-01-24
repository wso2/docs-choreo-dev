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

export class TriggersTemplate {
  static SelectWebhookTemplate() {
    cy.get('[data-testid="project-template-list-webhook"]').click();
  }

  static createSlackTriggerFromTemplate(webhookName: string, fileID: string) {
    cy.get('input[name="webhookName"]').clear().type(webhookName);
    cy.get('#mui-component-select-triggerType').click();
    cy.get('li').contains('Slack');
    cy.get('#mui-component-select-triggerChannel').click();
    // cy.get('li').contains('SlackEventsAppService').click();
    cy.contains('SlackEventsAppService').click({ force: true });
    cy.get('button').contains('Create').click();
    cy.intercept(Cypress.env('gqlServerUrl')).as('proj_create');
    cy.get("#mui-component-select-triggerType").click();
    cy.contains("Slack").click();
    cy.get("#mui-component-select-triggerChannel").click();
    cy.contains("SlackEventsAppService").click();
    cy.get("button").contains("Create").click();
    cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("proj_create");
    this.interceptProjectDetails(fileID);
  }

  private static interceptProjectDetails(fileID: string) {
    cy.wait("@proj_create", { timeout: 80000 }).then((e) => {
      const authdata = {
        header: {
          authorization: e.request.headers.authorization,
          "content-type": "application/json",
        },
        id: e.response.body.data.createProjectComponent.id,
        projectId: e.response.body.data.createProjectComponent.projectId,
        handler: e.response.body.data.createProjectComponent.handler,
      };
      cy.task("writeTestData", {
        fileName: fileID,
        key: "authData",
        value: authdata,
      });
    });
  }
}
