/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums } from "../../../support/commons/enums";
import { console } from "../../../support/console/console";
import {
  Project,
  WebhookInfo,
} from "../../../support/console/entities/project/project";
import { Webhook } from "../../../support/console/entities/component/webhook-component";
import { MEDIUM_TIME } from "../../../support/commons/timeouts";
import { ConfigEntryStep } from "../../../support/commons/types";
import { TestIds } from "../../../support/console/constants/TestIds";

after(() => {
  console.logout();
});

describe("Verify webhook creation functionality", () => {
  const CONFIG = "pkKgDNr5vGND364IsHzwGM7O";
  const PROJECT_DESCRIPTION = "Slack Webhook";

  const webhookInfo: WebhookInfo = {
    triggerChannels: "AppService",
    triggerId: "126",
  };

  let project: Project;
  let webhook: Webhook;

  // This step is only encountered the first time a service component with a config is promoted.
  // However if due to an error the step is retried by Cypress this step will not be encountered.
  // Therefore this is handled as an optional step.
  function useDeployConfigsIfPrompted() {
    cy.contains(/^Step/).should("be.visible");
    cy.get("body").then((body) => {
      if (body.find(TestIds.nextButton).length > 0) {
        cy.get(TestIds.nextButton).click();
      }
    });
  }

  function addConfiguration() {
    cy.get(".ConfigForm", MEDIUM_TIME).should("be.visible");
    cy.get(".ConfigForm div input").clear().type(CONFIG);
    cy.get('.ConfigForm button[type="submit"]').click();
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Verify Webhook component creation", () => {
    project
      .createWebhookComponent(
        Enums.Accessibility.EXTERNAL,
        {
          url: "https://github.com/choreo-test-apps/slack-web-hook",
          branch: "main",
        },
        webhookInfo
      )
      .then((app: Webhook) => {
        project.visitComponent(app.getName());
        webhook = app;
      });
  });

  it("Build Webhook", () => {
    webhook.build();
  });

  it("Deploy Webhook to dev", () => {
    webhook.deployToDev([new ConfigEntryStep(addConfiguration)]);
  });

  it("Verify component promotion to Prod", () => {
    webhook.promoteProd([
      new ConfigEntryStep(useDeployConfigsIfPrompted),
      new ConfigEntryStep(addConfiguration),
    ]);
  });

  it("Stop deployments", () => {
    webhook.stopDeployment();
    webhook.stopPromotion();
  });
});
