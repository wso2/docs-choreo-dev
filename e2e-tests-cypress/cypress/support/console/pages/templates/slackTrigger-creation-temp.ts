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
    cy.get("#mui-component-select-triggerType").click();
    cy.get('[data-cyid="Slack"]').click();
    cy.get("#mui-component-select-triggerChannel").click();
    cy.get('[data-cyid="SlackEventsAppService"]').click();
    cy.get('[data-cyid="btn-create"]').click();
    cy.intercept({
      method: "POST",
      url: Cypress.env("appSvcURL") + "/graphql",
      times: 1,
    }).as("proj_create");
    this.interceptProjectDetails(fileID);
  }

  private static interceptProjectDetails(fileID: string) {
    // eslint-disable-next-line arrow-body-style

    cy.wait("@proj_create", { timeout: 180000 }).then((e) => {
      expect(e.response.statusCode).to.eq(200);
      const { id, projectId, handler } = e.response.body.data.createComponent;
      const authdata = {
        header: {
          authorization: e.request.headers.authorization,
          "content-type": "application/json",
        },
        id,
        projectId,
        handler,
      };
      cy.task("writeTestData", {
        fileName: fileID,
        key: "authData",
        value: authdata,
      });
    });
  }
}
