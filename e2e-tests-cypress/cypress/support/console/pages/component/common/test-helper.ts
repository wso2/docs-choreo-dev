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

import { Utils } from "../../../utils";
import { APITest } from "../../apis/api-test";
import { Enums } from "../../../enums";
import { ComponentOverviewPage } from "../component-overview-page";
import { ComponentTestPage } from "../component-test-page";
import { Curl } from "../UI-components/curl-component";
import { SwaggerUI } from "../UI-components/swagger-UI-component";

export class TestHelper {
  static testOnSwagger(env: Enums.Environment, resourcePath: string, key: string = "", value: string = "") {
    APITest.testAPI();
    cy.get('[data-cyid="OpenAPI Console"]').click()
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

  static testOnCurl(env: Enums.Environment, httpMethod: Enums.HTTPMethod, pathParm: string, queryParameters1 = []) {
    ComponentTestPage.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    Curl.enterPathParameter(pathParm);
    Curl.addQueryParameter(queryParameters1);
    cy.get("textarea").invoke("text").then(curl => {
      Cypress.env(`int_curl_${env}`, curl)
    })
    return Curl.getRequestComponents(`${env}${pathParm}`);
  }

  static testOnCurlDiscardPrevious(env: Enums.Environment, httpMethod: Enums.HTTPMethod, pathParm: string, queryParameters1 = []) {
    ComponentTestPage.selectCurl();
    Curl.selectCurlEnvironment(env);
    Curl.selectMethod(httpMethod);
    Curl.enterPathParameter(pathParm);
    Curl.addQueryParameter(queryParameters1);
    cy.get("textarea").invoke("text").then(curl => {
      Cypress.env(`int_curl_${env}`, curl)
    })
    return Curl.getRequestComponentsDiscardPrevious(`${env}${pathParm}`);
  }

  static testDevOnGraphQL(code: string) {
    cy.get('div[class="execute-button-wrap"]>button').should("be.visible");
    APITest.selectDevEnvironment();
    cy.wait(5000);
    cy.get('section> div>div>div>div>div[class="CodeMirror-lines"]>div').eq(0).click()
    cy.get('section>div>div>div[class="CodeMirror-sizer"]>div>div>div>div>div>pre>span>span[cm-text]')
      .then($p => {
        Utils.paste($p, code, false)
        cy.wait(2000)
      })
    cy.get('div[class="toolbar"]>button').eq(0).click()
    cy.get('div[class="execute-button-wrap"]>button').click()
  }

  static testProdOnGraphQL(code: string) {
    cy.get('div[class="execute-button-wrap"]>button').should("be.visible");
    APITest.selectProdEnvironment();
    cy.wait(5000);
    cy.get('section> div>div>div>div>div[class="CodeMirror-lines"]>div').eq(0).click()
    cy.get('section>div>div>div[class="CodeMirror-sizer"]>div>div>div>div>div>pre>span>span[cm-text]')
      .then($p => {
        Utils.paste($p, code, false)
        cy.wait(2000)
      })
    cy.get('div[class="toolbar"]>button').eq(0).click()
    cy.get('div[class="execute-button-wrap"]>button').click()
  }

  static getGqlResult(expectedResponse: string = "") {
    cy.wait(6000)
    cy.get('.CodeMirror-sizer>div>div>div').eq(3).invoke('text').then(r => {
      const response = r.replace('x', '').trim()
      expect(response).to.be.contains(expectedResponse)
    })
    cy.wait(6000)
    this.clearGQL()
  }


  private static clearGQL() {
    ComponentOverviewPage.navigateToDeploy()
    cy.wait(6000)
    ComponentOverviewPage.navigateToTest()
  }

}
