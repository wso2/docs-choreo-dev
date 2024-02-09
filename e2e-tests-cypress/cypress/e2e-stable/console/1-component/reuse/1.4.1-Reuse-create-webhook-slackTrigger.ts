/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums } from "../../../../support/commons/enums";
import { MEDIUM_TIME } from "../../../../support/commons/timeouts";
import { ConfigEntryStep } from "../../../../support/commons/types";
import { console } from "../../../../support/console/console";
import { Webhook } from "../../../../support/console/entities/component/webhook-component";
import {
  Project,
  WebhookInfo,
} from "../../../../support/console/entities/project/project";

describe("Create Reusable Webhook functionality", () => {
  const CONFIG = "pkKgDNr5vGND364IsHzwGM7O";
  const WEBHOOK_NAME = "create-Reuse-slackTrigger-1.4.1";
  const PROJECT_NAME = "Default Project";

  const webhookInfo: WebhookInfo = {
    triggerChannels: "AppService",
    triggerId: "126",
  };

  let project: Project;
  let webhook: Webhook;

  function addConfiguration() {
    cy.get(".ConfigForm", MEDIUM_TIME).should("be.visible");
    cy.get(".ConfigForm div input").clear().type(CONFIG);
    cy.get('.ConfigForm button[type="submit"]').click();
  }

  it("Login to Console", () => {
    console.login();
  });

  it("Search reuse component project", () => {
    project = console.searchProject(PROJECT_NAME);
  });

  it("Navigate to existing Webhook component", () => {
    project.isComponentExists(WEBHOOK_NAME).then((isExists) => {
      if (!isExists) {
        project
          .createWebhookComponent(
            Enums.Accessibility.EXTERNAL,
            {
              url: "https://github.com/choreo-test-apps/slack-web-hook",
              branch: "main",
            },
            webhookInfo,
            WEBHOOK_NAME
          )
          .then((app: Webhook) => {
            project.visitComponent(WEBHOOK_NAME);
            webhook = app;
          });
      } else {
        project.visitComponent(WEBHOOK_NAME);
        webhook = new Webhook(WEBHOOK_NAME);
      }
    });
  });

  it("Build Webhook", () => {
    webhook.build();
  });

  it("Deploy the component", () => {
    webhook.deployToDev([new ConfigEntryStep(addConfiguration)]);
  });

  it("Component promotion to prod", () => {
    webhook.promoteProd();
  });

  it("Verify suspending Dev deployed component", () => {
    webhook.stopDeployment();
  });

  it("Verify suspending Prod deployed component", () => {
    webhook.stopPromotion();
  });
});
