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

import "@testing-library/cypress/add-commands";
import { APITest } from "./console/pages/apis/api-test";
import { SwaggerUI } from "./console/pages/component/UI-components/swagger-UI-component";
import { ComponentTestPage } from "./console/pages/component/component-test-page";
import { HTTPMethod } from "./console/pages/enum/http-method-enum";

Cypress.Commands.add("verifyTest", (operationTarget: string) => {
  APITest.testAPI();
  ComponentTestPage.getTestKey();
  SwaggerUI.SelectResource(operationTarget);
  SwaggerUI.TryoutAPI();
  SwaggerUI.ExecuteResourceFunction();
  SwaggerUI.GetResponse();
  SwaggerUI.getResponseCode().should("eq", "200");
});

declare global {
  namespace Cypress {
    interface Chainable {
      verifyTest(operationTarget: string): Chainable<Element>;
    }
  }
}
