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

import { fromCallback } from "cypress/types/bluebird";
import { cyGet, cyLog } from "../../../../commons/cy";
import { Enums } from "../../../../commons/enums";
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../../commons/timeouts";
import { Utils } from "../../../../commons/utils";
import { GraphQL } from "../../../apis/graphql";
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
    this.selectOpenApiConsole();
    cy.wait(5000); // Attempting to select the environment too quickly causes wrong environment to be selected
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.getTestKey();
    return this.invokeSwaggerResource(env, resourcePath, key, value, "", "");
  }

  static invokeAPI(
    projectName: string,
    componentName: string,
    environment: Enums.Environment,
    httpMethod: Enums.HTTPMethod,
    pathParm: string,
    queryParameters1?: { key: string; value: string }[],
    enableHeaders: boolean = true
  ) {
    return GraphQL._getAuthHeaderKey(
      projectName,
      componentName,
      environment
    ).then((res) => {
      const { invokeUrl, apikey } = res;

      let query = "";
      let url = "";

      if (queryParameters1) {
        for (let index = 0; index < queryParameters1.length; index++) {
          const element = queryParameters1[index];
          query = query + `${element.key}=${element.value}&`;
        }
        url = `${invokeUrl}/${pathParm}?${query.trim()}`;
      } else {
        url = `${invokeUrl}/${pathParm}`;
      }

      if (enableHeaders) {
        return Utils.sendGetRequest(url, { "api-key": apikey }).then((res) => {
          const { body, status, headers } = res;
          return Promise.resolve({ invokeUrl, apikey, body, status, headers });
        });
      }

      return Utils.sendGetRequest(url).then((res) => {
        const { body, status, headers } = res;
        return Promise.resolve({ invokeUrl, apikey, body, status, headers });
      });
    });
  }

  static testOnCurl(
    env: Enums.Environment,
    httpMethod: Enums.HTTPMethod,
    pathParm: string,
    queryParameters1 = [],
    apiName?: string
  ) {
    this.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    if (pathParm != "") {
      Curl.enterPathParameter(pathParm);
    }
    Curl.addQueryParameter(queryParameters1);

    return Curl.getRequestComponents();
  }

  static testOnCurlDiscardPrevious(
    env: Enums.Environment,
    httpMethod: Enums.HTTPMethod,
    pathParm: string,
    queryParameters1 = []
  ) {
    this.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    Curl.enterPathParameter(pathParm);
    Curl.addQueryParameter(queryParameters1);
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
          cyGet("span[cm-text]")
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
    parentComponentId,
    key: string = "",
    value: string = ""
  ) {
    this.selectTestConsole();
    cy.wait(5000);
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.selectEndpoint(endpoint);
    ComponentTestPage.getTestKey();
    return this.invokeSwaggerResource(env, resourcePath, key, value, method, parentComponentId);
  }

  private static invokeSwaggerResource(
    env: string,
    resourcePath: string,
    key: string,
    value: string,
    method: string,
    parentComponentId: string,
    retryCount: number = 0,
    retryDelay: number = VERY_SHORT_TIME.timeout
  ) {
    cy.get('[id="circular-loader"]').should("not.exist");
    SwaggerUI.invokeResource(resourcePath, key, value, method);

    // This call is required to actually store the curl command in the env variable for later use
    Curl.getRequestComponentsForService(`${env}${resourcePath}`);

    return SwaggerUI.getResponseCode().then((res) => {
      cy.log("Response code: " + res);
      cy.log("Retry count: " + retryCount);
      if (res != "200" && retryCount < 4) {
        cy.log("Code is not 200, Retrying...");
        retryCount++;
        cy.wait(retryDelay);
        this.invokeSwaggerResource(
          env,
          resourcePath,
          key,
          value,
          method,
          parentComponentId,
          retryCount,
          retryDelay
        );
      }

      cy.log("Finished retrying");
      return SwaggerUI.GetResponse().then((r) => {
        return SwaggerUI.getResponseCode().then((res) => {
          SwaggerUI.closeResource(resourcePath, parentComponentId);
          return Promise.resolve({
            response: r,
            statusCode: res,
          });
        });
      });
    });
  }

  private static expandSecondaryMenu(selector: string) {
    cy.get("body").then((bdy) => {
      // Secondary menu is collapsed
      if (bdy.find(selector).length == 0) {
        // Expand secondary menu
        APITest.testAPI();
      }
    });
  }

  private static selectOpenApiConsole() {
    let selector = '[data-cyid="openapi"]';
    if (Utils.isUnifiedMenuEnabled()) {
      this.expandSecondaryMenu(selector);
    } else {
      selector = '[data-cyid="OpenAPI Console"]';
      APITest.testAPI();
    }
    cy.get(selector).click({ force: true });
  }

  private static selectCurl() {
    let selector = '[data-cyid="curl"]';
    if (Utils.isUnifiedMenuEnabled()) {
      this.expandSecondaryMenu(selector);
    } else {
      APITest.testAPI();
      selector = '[data-testid="cURL"]';
    }
    cy.get(selector).click();
  }

  private static selectTestConsole() {
    let selector = '[data-cyid="testConsole"]';
    if (Utils.isUnifiedMenuEnabled()) {
      this.expandSecondaryMenu(selector);
    } else {
      selector = '[data-cyid="Console"]';
      APITest.testAPI();
    }
    cy.get(selector).click();
  }
}
