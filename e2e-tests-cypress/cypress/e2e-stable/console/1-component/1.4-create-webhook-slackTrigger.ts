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
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { console } from "../../../support/console/console";
import { Project, WebhookInfo } from "../../../support/console/entities/project/project";
import { Webhook } from "../../../support/console/entities/component/webhook-component";

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


  it("Login to Console", () => {
    console.login();
  });
  
  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });


  it("Verify Webhook component creation", () => {
    project.createWebhookComponent(Enums.Accessibility.EXTERNAL, {
      url: "https://github.com/choreo-test-apps/slack-web-hook",
      branch: "main",
    },  webhookInfo,)
    .then((app: Webhook) => {
      webhook = app;
    });
  });

  it("Build Webhook", () => {
    webhook.build();
  });


  it("Deploy Webhook to dev", () => {
    webhook.deployToDev(CONFIG);
  });


  it("Verify component promotion to Prod", () => {
    webhook.promoteProd(CONFIG);
  });

  it("Stop deployments", () => {
    webhook.stopDeployment();
    webhook.stopPromotion();
  });
});
