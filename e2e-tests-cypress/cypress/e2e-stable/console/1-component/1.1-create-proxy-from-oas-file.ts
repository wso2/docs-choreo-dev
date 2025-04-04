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

import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import {
  Proxy,
  ProxyMetaData,
} from "../../../support/console/entities/component/proxy-component";
import { Enums, SecurityScheme, UsagePlan } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";
import { Application } from "../../../support/console/entities/application/application";
import { devPortal } from "../../../support/console/devportal";
import { Utils } from "../../../support/commons/utils";

describe("Create Proxy from OAS file", () => {
  const PROJECT_DESCRIPTION = "Project with proxy from OAS file";
  const Filepath = "apis/generation_oas.yaml";
  const RESOURCE = "intensity";
  const permissions = [`emp-read-${Date.now()}`, `emp-write-${Date.now()}`];

  let project: Project;
  let proxy: Proxy;
  let application: Application;

  it("Login to Console", () => {
    console.login().then(() => {
      console.cleanUpData();
    });
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a proxy from oas file", () => {
    project
      .createProxyComponent({
        version: "1.0",
        oasFilePath: Filepath,
        endpointUrl: "",
      })
      .then((comp) => {
        proxy = comp;
        // Since we are switching domains when navigating to devportal url we will no longer have access to the proxy object
        // So we need to save the proxy metadata in nodejs global state using below cy.task() to access it later
        cy.task("setData", {
          key: Cypress.spec.name, // Unique key to store the data, in this case spec name is sufficient
          value: proxy.getMetaData(),
        });
      });
  });

  it("Enable OAuth2 security", () => {
    proxy.enableSecuritySchemesAndDeploy([SecurityScheme.OAuth2]);
  });

  it("Promote proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    proxy
      .testCurl(Enums.Environment.DEVELOPMENT, Enums.HTTPMethod.GET, RESOURCE)
      .then((res) => {
        expect(res.status).equal(OK);
      });
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    proxy
      .testCurl(Enums.Environment.PRODUCTION, Enums.HTTPMethod.GET, RESOURCE)
      .then((res) => {
        expect(res.status).equal(OK);
      });
  });

  it("Updating the usage plans", () => {
    proxy.updateUsagePlans([UsagePlan.Gold, UsagePlan.Bronze]);
  });

  it("Enable CORS", () => {
    proxy.enableCors(Enums.Environment.DEVELOPMENT);
  });

  it("Manage permissions", () => {
    proxy.managePermissions(permissions);
  });

  it("Publish proxy to Dev portal", () => {
    proxy.publish();
  });

  it("Navigate to Dev portal", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        devPortal.loginToDevPortal();
        // Recreate Proxy object using previously saved metadata
        cy.task("getData", Cypress.spec.name).then((metaData) => {
          proxy = Proxy.fromMetaData(metaData as ProxyMetaData);
        });
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Find API in devportal custom domain", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        devPortal.searchApi(proxy.getName());
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Create application in Dev portal", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        application = proxy.createApplication_DevPortal();
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Generate credentials for application", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        application.generateCredentials(Enums.Environment.SANDBOX);
        application.generateCredentials(Enums.Environment.PRODUCTION);
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Add api subscription to application", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        application.addSubscription(proxy.getName(), UsagePlan.Bronze);
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Navigate back to Proxy in Dev Portal", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        devPortal.searchApi(proxy.getName());
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Tryout application", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        proxy.testSwaggerConsole_DevPortal({
          resource: RESOURCE,
          keyType: Enums.ApiTryoutKeyType.APPLICATION_KEY,
        });
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Verify Proxy Consumer in Console", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        cy.visit(proxy.getComponentUrl()).then(() => {
          proxy.verifyConsumer(application.getName());
        });
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Stop component", () => {
    proxy.stopDeployment();
    proxy.stopPromotion();
  });

  it("Verifying component insights", () => {
    proxy.verifyUsageInsights();
  });

  it("Verifying project insights in Dev", () => {
    project.verifyUsageInsights(Enums.Environment.DEVELOPMENT);
  });

  it("Verifying project insights in Prod", () => {
    project.verifyUsageInsights(Enums.Environment.PRODUCTION);
  });

  it('Clean up created data', () => {
    console.cleanUpData();
  });
});
