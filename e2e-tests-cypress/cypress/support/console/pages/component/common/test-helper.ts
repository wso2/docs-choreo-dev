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

import { Enums } from "../../../../commons/enums";
import { Utils } from "../../../../commons/utils";
import { APITest } from "../../apis/api-test";
import { Curl } from "../UI-components/curl-component";
import { SwaggerUI } from "../UI-components/swagger-UI-component";
import { ComponentOverviewPage } from "../component-overview-page";
import { ComponentTestPage } from "../component-test-page";

export class TestHelper {
  static testOnSwagger(
    env: Enums.Environment,
    resourcePath: string,
    key: string = "",
    value: string = ""
  ) {
    APITest.testAPI();
    cy.get('[data-cyid="OpenAPI Console"]').click();
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(resourcePath, key, value);

    return SwaggerUI.getResponseCode().then((res) => {
      return SwaggerUI.GetResponse().then((r) => {
        return cy.wrap({
          response: r,
          statusCode: res,
        });
      });
    });
  }

  static testOnCurl(
    env: Enums.Environment,
    httpMethod: Enums.HTTPMethod,
    pathParm: string,
    queryParameters1 = []
  ) {
    ComponentTestPage.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    if (pathParm != "") {
      Curl.enterPathParameter(pathParm);
    }
    Curl.addQueryParameter(queryParameters1);
    cy.get("textarea")
      .invoke("text")
      .then((curl) => {
        Cypress.env(`int_curl_${env}`, curl);
      });
    return Curl.getRequestComponents(`${env}${pathParm}`);
  }

  static testOnCurlDiscardPrevious(
    env: Enums.Environment,
    httpMethod: Enums.HTTPMethod,
    pathParm: string,
    queryParameters1 = []
  ) {
    ComponentTestPage.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    Curl.enterPathParameter(pathParm);
    Curl.addQueryParameter(queryParameters1);
    cy.get("textarea")
      .invoke("text")
      .then((curl) => {
        Cypress.env(`int_curl_${env}`, curl);
      });
    return Curl.getRequestComponentsDiscardPrevious(`${env}${pathParm}`);
  }

  static testGraphQL(env: Enums.Environment, code: string, endpoint?: string) {
    cy.get('div[class="execute-button-wrap"]>button').should("be.visible");
    APITest.selectEnvironment(env);
    if (endpoint) {
      ComponentTestPage.selectEndpoint(endpoint);
    }
    cy.wait(5000);
    Utils.getRenderedElement('[data-testid="graphiql-container"]').within(
      () => {
        cy.get('[class="query-editor"]').within(() => {
          cy.get("span[cm-text]")
            .eq(1)
            .then(($p) => {
              Utils.paste($p, code, false);
              cy.wait(2000);
            });
        });
      }
    );
    cy.get('div[class="toolbar"]>button').eq(0).click();
    cy.get('div[class="execute-button-wrap"]>button').click();
  }

  static getGqlResult(expectedResponse: string = "") {
    cy.wait(6000);
    cy.get('[class="result-window"]').within(() => {
      cy.get('[class="CodeMirror-sizer"]').within(() => {
        cy.get('[class="CodeMirror-code"]')
          .invoke("text")
          .then((r) => {
            const response = r.replace("x", "").trim();
            expect(response).to.be.contains(expectedResponse);
          });
      });
    });
    cy.wait(6000);
    this.clearGQL();
  }

  private static clearGQL() {
    ComponentOverviewPage.navigateToDeploy();
    cy.wait(6000);
    ComponentOverviewPage.navigateToTest();
  }

  static testProjectLevelEndpoint() {
    cy.get('[data-testid="no-public-endpoints-notification"]').should(
      "be.visible"
    );
  }

  static testManagedEndpoint(
    env: Enums.Environment,
    endpoint: string,
    resourcePath: string,
    method = "",
    key: string = "",
    value: string = ""
  ) {
    cy.get('[data-cyid="Console"]').click();
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.selectEndpoint(endpoint);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(resourcePath, key, value, method);
    Curl.getRequestComponentsForService(`${env}${resourcePath}`);

    return SwaggerUI.getResponseCode().then((res) => {
      return SwaggerUI.GetResponse().then((r) => {
        return cy.wrap({
          response: r,
          statusCode: res,
        });
      });
    });
  }
}
