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
import { VERY_SHORT_TIME } from "../../../../commons/timeouts";
import { Utils } from "../../../../commons/utils";
import { GraphQL } from "../../../apis/graphql";
import { APITest } from "../../apis/api-test";
import { Curl } from "../UI-components/curl-component";
import { SwaggerUI } from "../UI-components/swagger-UI-component";
import { ComponentTestPage } from "../component-test-page";
import { TestIds } from "../../../constants/TestIds";

export class TestHelper {
  static testOnSwagger(
    env: Enums.Environment,
    resourcePath: string,
    key: string = "",
    value: string = ""
  ) {
    this.selectOpenApiConsole();
    cy.wait(5000); // Attempting to select the environment too quickly causes wrong environment to be selected
    cy.get(TestIds.progressBar).should("not.exist");
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.getTestKey();
    this.invokeSwaggerResource(env, resourcePath, key, value, "", "");
    return this.getSwaggerResponse(resourcePath, "");
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
    cy.get(TestIds.graphQLTestConsole)
      .should("be.visible")
      .find(".execute-button")
      .should("be.visible");

    APITest.selectEnvironment(env);
    if (endpoint) {
      ComponentTestPage.selectEndpoint(endpoint);
    }

    cy.getUnstable(TestIds.graphQLTestConsole)
      .should("be.visible")
      .find(".query-editor")
      .find("textarea")
      .then(($p) => {
        Utils.paste($p, code, false);
      });

    cy.get(TestIds.graphQLQueryPrettify).click();
    cy.get(TestIds.graphQLTestConsole).find(".execute-button").click();
  }

  static getGraphQLResult() {
    cy.getUnstable(TestIds.graphQLTestConsole)
      .find(".spinner")
      .should("not.exist");

    return cy
      .get('[class="result-window"]')
      .should("be.visible")
      .find('[class="CodeMirror-sizer"]')
      .should("be.visible")
      .find('[class="CodeMirror-code"]')
      .should("be.visible")
      .invoke("text")
      .then((result) => {
        return result;
      });
  }

  static verifyProjectLevelEndpoint() {
    if (Utils.isKubeConFeaturesEnabled()) {
      cy.get('[data-testid="notification-with-icon-and-button"]').should(
        "be.visible"
      );
    } else {
      cy.get('[data-testid="no-public-endpoints-notification"]').should(
        "be.visible"
      );
    }
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
    this.invokeSwaggerResource(
      env,
      resourcePath,
      key,
      value,
      method,
      parentComponentId
    );
    return this.getSwaggerResponse(resourcePath, parentComponentId);
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
        cy.wait(retryDelay * retryCount);
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
    });
  }

  private static getSwaggerResponse(
    resourcePath: string,
    parentComponentId: string
  ) {
    return SwaggerUI.GetResponse().then((r) => {
      return SwaggerUI.getResponseCode().then((res) => {
        SwaggerUI.closeResource(resourcePath, parentComponentId);
        return Promise.resolve({
          response: r,
          statusCode: res,
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

    this.expandSecondaryMenu(selector);
    cy.get(selector).click({ force: true });
  }

  private static selectCurl() {
    let selector = '[data-cyid="curl"]';

    this.expandSecondaryMenu(selector);
    cy.get(selector).click();
  }

  private static selectTestConsole() {
    let selector = '[data-cyid="testConsole"]';

    this.expandSecondaryMenu(selector);
    cy.get(selector).click();
  }
}
