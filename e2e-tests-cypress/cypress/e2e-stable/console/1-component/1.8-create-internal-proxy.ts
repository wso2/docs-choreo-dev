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

describe(`Verify internal API Proxy functionality`, () => {
  const PROJECT_DESCRIPTION = "Internal API Proxy for REST Endpoint";
  const API_ENDPOINT =
    "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/users/endpoint-9090-803/v1.0";

  const OPERATION_USERS = "users";

  const DEV_INVOKE_URL_TEXT = "dev-internal";
  const PROD_INVOKE_URL_TEXT = "prod-internal";

  let DEV_INVOKE_URL = "";
  let PROD_INVOKE_URL = "";
  let project: Project;
  let internalProxy: Proxy;

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

  it("Verify Internal Proxy endpoints are not accessible", () => {
    internalProxy.copyEndpointUrl(DEV_INVOKE_URL_TEXT);
    internalProxy.copyEndpointUrl(PROD_INVOKE_URL_TEXT);

    cy.get<string>(`@${DEV_INVOKE_URL_TEXT}`).then((devUrl) => {
      expect(Utils.isHostResolvable(devUrl) != true); // Verify that the internal API is not accessible
      DEV_INVOKE_URL = devUrl;
    });

    cy.get<string>(`@${PROD_INVOKE_URL_TEXT}`).then((prodUrl) => {
      expect(Utils.isHostResolvable(prodUrl) != true); // Verify that the internal API is not accessible
      PROD_INVOKE_URL = prodUrl;
    });
  });
});
