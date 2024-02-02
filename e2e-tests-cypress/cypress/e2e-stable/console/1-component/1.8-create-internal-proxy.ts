/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import { Proxy } from "../../../support/console/entities/component/proxy-component";
import { Utils } from "../../../support/commons/utils";
import { Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";

describe(`Verify internal API Proxy functionality`, () => {
  const PROJECT_DESCRIPTION = "Internal API Proxy for REST Endpoint";
  const API_ENDPOINT =
    "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/users/endpoint-9090-803/v1.0";

  const OPERATION_USERS = "users";

  const DEV_INVOKE_URL_TEXT = "dev-internal";
  const PROD_INVOKE_URL_TEXT = "prod-internal";
  const endpointMatcher = new Map<string, Enums.Environment>([
    [DEV_INVOKE_URL_TEXT, Enums.Environment.DEVELOPMENT],
    [PROD_INVOKE_URL_TEXT, Enums.Environment.PRODUCTION],
  ]);

  let project: Project;
  let internalProxy: Proxy;
  let firstPublicProxy: Proxy;
  let secondPublicProxy: Proxy;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating an Internal Proxy for an existing API", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: API_ENDPOINT,
        isInternal: true,
      })
      .then((comp) => {
        internalProxy = comp;
      });
  });

  it("Add resource to Internal Proxy", () => {
    internalProxy.removeDefaultResources();
    internalProxy.addResources([{ path: OPERATION_USERS, verbs: ["GET"] }]);
  });

  it("Deploy Internal Proxy", () => {
    internalProxy.deploy();
  });

  it("Promote Internal Proxy", () => {
    internalProxy.promote();
  });

  it("Disable security of Internal Proxy in Dev", () => {
    internalProxy.disableSecurityInDev(OPERATION_USERS);
  });

  it("Disable security of Internal Proxy in Prod", () => {
    internalProxy.disableSecurityInProd(OPERATION_USERS);
  });

  it("Copy Internal Proxy endpoints and return to Project", () => {
    internalProxy.saveEndpointUrls(endpointMatcher).then(() => {
      // Verify that the internal API is not accessible
      expect(Utils.isHostResolvable(internalProxy.getDevEndpointUrl()) != true);
      expect(
        Utils.isHostResolvable(internalProxy.getProdEndpointUrl()) != true
      );
    });
  });

  it("Return to Project", () => {
    internalProxy.goBackToProject();
  });

  it("Create 1st Public Proxy using Dev endpoint of Internal Proxy", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: internalProxy.getDevEndpointUrl(),
      })
      .then((comp) => {
        firstPublicProxy = comp;
      });
  });

  it("Add resource to 1st Public Proxy", () => {
    firstPublicProxy.removeDefaultResources();
    firstPublicProxy.addResources([{ path: OPERATION_USERS, verbs: ["GET"] }]);
  });

  it("Deploy 1st Public Proxy", () => {
    firstPublicProxy.deploy();
  });

  it("Promote 1st Public Proxy", () => {
    firstPublicProxy.promote();
  });

  it("Verify 1st Public Proxy test functionality using Swagger UI in Dev", () => {
    firstPublicProxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify 1st Public Proxy test functionality using Swagger UI in Prod", () => {
    firstPublicProxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Return to Project", () => {
    firstPublicProxy.goBackToProject();
  });

  it("Create 2nd Public Proxy using Prod endpoint of Internal Proxy", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: internalProxy.getProdEndpointUrl(),
      })
      .then((comp) => {
        secondPublicProxy = comp;
      });
  });

  it("Add resource to 2nd Public Proxy", () => {
    secondPublicProxy.removeDefaultResources();
    secondPublicProxy.addResources([{ path: OPERATION_USERS, verbs: ["GET"] }]);
  });

  it("Deploy 2nd Public Proxy", () => {
    secondPublicProxy.deploy();
  });

  it("Promote 2nd Public Proxy", () => {
    secondPublicProxy.promote();
  });

  it("Verify 2nd Public Proxy test functionality using Swagger UI in Dev", () => {
    secondPublicProxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify 2nd Public Proxy test functionality using Swagger UI in Prod", () => {
    secondPublicProxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Return to Project", () => {
    secondPublicProxy.goBackToProject();
  });

  it("Visit Internal Proxy", () => {
    project.visitComponent(internalProxy.getName());
  });

  it("Verify change Internal Proxy to External Proxy", () => {
    internalProxy.updateAccessMode(Enums.Accessibility.EXTERNAL);
  });

  it("Verify converted External Proxy test functionality using Swagger UI in Dev", () => {
    internalProxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify converted External Proxy test functionality using Swagger UI in Prod", () => {
    internalProxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Publish converted External Proxy to Dev portal", () => {
    internalProxy.publish();
  });

  it("Navigate to Dev portal", () => {
    internalProxy.navigateToDevPortal();
  });

  it("Generate Production credentials for converted External Proxy in Dev portal", () => {
    internalProxy.generateCredentials_DevPortal(Enums.Environment.PRODUCTION);
  });

  it("Tryout converted External Proxy in Dev portal", () => {
    internalProxy.testSwaggerConsole_DevPortal(OPERATION_USERS);
  });
});
