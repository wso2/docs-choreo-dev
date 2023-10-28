import { MIN_RENDERING_WAIT_TIME } from "./constants";
import { cyLog } from "./cy";
import { Enums } from "./enums";
import { VERY_SHORT_TIME } from "./timeouts";

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

interface MatchResponse {
  expectedCode: number;
  expectedHeaders?: Record<string, string>;
  expectedBody?: string;
}

export class Utils {
  static oldProjectNamePrefix = "automationtestproject";
  static projectNamePrefix = "autotest";
  static componentNamePrefix = "autotest";
  static keyNamePrefix = "e2eOnPremkey";
  static APP_SVC_URL = Cypress.env("appSvcURL");
  static ORG_NAME = Cypress.env("choreoOrgHandle");
  static MAIL_READER_SVC_URL = Cypress.env("mailReaderSvcURL");
  static MAIL_READER_CLIENT_ID = Cypress.env("mailReaderClientId");
  static MAIL_READER_CLIENT_SECRET = Cypress.env("mailReaderClientSecret");
  static MAIL_READER_TOKEN_URL = Cypress.env("mailReaderTokenURL");
  static NEW_APP_SVC_URL = Cypress.env("newAppSvcURL");

  static TRY_COUNT = 5;

  /**
   * Create name for app.
   *
   * @returns true name for a new app
   */
  static generateProjectName() {
    return `${this.projectNamePrefix}${Date.now()}`;
  }

  static generateComponentName(name: string = "") {
    return this.componentNamePrefix + Date.now() + name;
  }

  static generateBasePath() {
    return Date.now().toString();
  }

  /**
   * Create name for on-prem key.
   *
   * @returns true name for a new on-prem key
   */
  static generateKeyName(name: string) {
    return (
      this.keyNamePrefix +
      Math.random()
        .toString(36)
        .replace(/[^a-z]+/g, "")
        .substring(0, 5) +
      name
    );
  }

  static acceptEmailInviteToOrg(
    token: string,
    timestamp: string,
    retryCount = 0
  ) {
    const headerString = btoa(
      `${Utils.MAIL_READER_CLIENT_ID}:${Utils.MAIL_READER_CLIENT_SECRET}`
    );
    cy.wait(5000);
    if (retryCount > 5) {
      return;
    }
    this.sendPostRequest(
      Utils.MAIL_READER_TOKEN_URL,
      { Authorization: `Basic ${headerString}` },
      { grant_type: "client_credentials" }
    ).then((res) => {
      const accessToken = res.body.access_token;
      this.sendGetRequest(Utils.MAIL_READER_SVC_URL + timestamp, {
        Authorization: `Bearer ${accessToken}`,
      }).then((res) => {
        if (res.status == 200 && res.body != "") {
          const rawMailContent = res.body;
          //const decodedMail = atob(rawMailContent);
          const decodedMail = window.atob(rawMailContent);
          console.log(rawMailContent);

          const socRegEx = /^<!DOCTYPE html PUBLIC /im;
          const bodyPos = decodedMail.indexOf(
            socRegEx.exec(decodedMail) as unknown as string
          );
          let bodyLines = decodedMail.substring(bodyPos);
          bodyLines = bodyLines.replace(/\r?\n?[^\r\n]*$/, "");
          bodyLines = bodyLines.replace(/\r?\n?[^\r\n]*$/, "");
          const invitationId =
            /[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}/.exec(
              bodyLines
            )[0];

          const header = {
            Authorization: `Bearer ${token}`,
            "content-type": "application/json",
          };
          this.sendPostRequest(
            `${Utils.NEW_APP_SVC_URL}/user-mgt/1.0.0/orgs/${Utils.ORG_NAME}/invitations/${invitationId}`,
            header,
            {}
          ).then((resp) => {
            cy.log(`Org invite accept response status: ${resp.status}`);
            cy.log(
              `Org invite accept response body: ${JSON.stringify(resp.body)}`
            );
          });
        } else {
          cy.log(`Error while reading email: ${res.status}`);
          retryCount++;
          this.acceptEmailInviteToOrg(token, timestamp, retryCount);
        }
      });
    });
  }

  static saveComponentURL() {
    cy.url().then((url) => {
      Cypress.env(`componentURL`, url);
    });
  }

  static isHostResolvable(url: string) {
    const urlWithoutProtocol = url.replace("https://", "");
    const slashIndex = urlWithoutProtocol.indexOf("/");
    const domain = urlWithoutProtocol.substring(0, slashIndex);
    const resource = urlWithoutProtocol.substring(
      slashIndex,
      urlWithoutProtocol.length
    );

    var options = {
      host: domain,
      port: 443,
      path: resource,
    };

    const http = require("http");

    http
      .get(options, function (res) {
        if (res.statusCode == 200) {
          return true;
        }
      })
      .on("error", function (e) {
        return false;
      });

    return false;
  }

  private static sendRequest(request: any, retryCount: number) {
    return this.retryRequest(request).then((res) => {
      if (res.retry && retryCount < this.TRY_COUNT) {
        cy.wait(VERY_SHORT_TIME.timeout);
        retryCount++;
        this.sendRequest(request, retryCount);
      }
    });
  }

  private static retryRequest(request: any) {
    return cy.request(request).then((res) => {
      let isRetry = false;
      if (res.status > 205) {
        isRetry = true;
      }

      return Promise.resolve({
        body: res.body,
        status: res.status,
        retry: isRetry,
        headers: res.headers,
      });
    });
  }

  private static sendRequestAndMatch(
    request: any,
    match: MatchResponse,
    retryCount: number = 0
  ) {
    cy.request(request).then((res) => {
      if (!this.isMatched(res, match)) {
        if (retryCount < this.TRY_COUNT) {
          cy.wait(VERY_SHORT_TIME.timeout);
          retryCount++;
          this.sendRequestAndMatch(request, match, retryCount);
        } else {
          expect(res.status).equal(match.expectedCode);

          if (typeof match.expectedHeaders !== "undefined") {
            for (const [key, value] of Object.entries(match.expectedHeaders)) {
              expect(res.headers[key]).equal(value);
            }
          }

          if (typeof match.expectedBody !== "undefined") {
            expect(res.body).equal(match.expectedBody);
          }
        }
      } else {
        expect(res.status).equal(match.expectedCode);

        if (typeof match.expectedHeaders !== "undefined") {
          for (const [key, value] of Object.entries(match.expectedHeaders)) {
            expect(res.headers[key]).equal(value);
          }
        }

        if (typeof match.expectedBody !== "undefined") {
          expect(res.body).equal(match.expectedBody);
        }
      }
    });
  }

  private static isMatched(res: any, match: MatchResponse) {
    if (res.status !== match.expectedCode) {
      return false;
    }

    if (
      typeof match.expectedBody !== "undefined" &&
      match.expectedBody !== res.body
    ) {
      return false;
    }

    if (typeof match.expectedHeaders !== "undefined") {
      for (const [key, value] of Object.entries(match.expectedHeaders)) {
        if (res.headers[key] !== value) {
          return false;
        }
      }
    }

    return true;
  }

  static sendPostRequest(url: string, headers, body) {
    const request = {
      method: "POST",
      url,
      headers,
      body,
      failOnStatusCode: false,
    };
    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status }, { log: false });
    });
  }

  static sendPutRequest(url: string, headers, body) {
    const request = {
      method: "PUT",
      url,
      headers,
      body,
      failOnStatusCode: false,
    };
    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status }, { log: false });
    });
  }

  static sendGetRequest(url: string, headers: any = {}) {
    const request = {
      method: "GET",
      url,
      headers,
      failOnStatusCode: false,
    };
    let retryCount = 0;
    return this.sendRequest(request, retryCount);
  }

  static sendGetRequestAndMatch(
    url: string,
    headers: any,
    match: MatchResponse
  ) {
    const request = {
      method: "GET",
      url,
      headers,
      failOnStatusCode: false,
    };

    this.sendRequestAndMatch(request, match);
  }

  static sendDeleteRequest(url: string, headers: any = {}, body?: any) {
    const request = {
      method: "DELETE",
      url,
      body,
      headers,
      failOnStatusCode: false,
    };
    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status }, { log: false });
    });
  }

  static setBrowserCookie() {
    const dateString = new Date().toISOString();
    document.cookie = `OptanonAlertBoxClosed=${dateString};SameSite=Lax;Secure`;
    cy.setCookie("OptanonAlertBoxClosed", dateString);
  }

  static paste(obj, code, enter) {
    const pasteEvent = Object.assign(
      new Event("paste", { bubbles: true, cancelable: true }),
      {
        clipboardData: { getData: (type = "text") => code },
      }
    );
    obj[0].dispatchEvent(pasteEvent);
    if (enter) {
      cy.wait(3000);
      cy.wrap(obj).type("{enter}");
    }
  }

  static isKubeConFeaturesEnabled() {
    const enableKubeConFeatures = Cypress.env("enableKubeConFeatures");
    if (enableKubeConFeatures != null) {
      return enableKubeConFeatures == true || enableKubeConFeatures == "true";
    }

    return false;
  }

  static moveMouseAwayFromLeftMenu() {
    cy.get("body").realMouseMove(250, 250);
  }

  static interceptConfig() {
    cy.intercept(`${Cypress.env("apimSvcURL")}/api/am/publisher/v2/apis/**`).as(
      "config"
    );
  }

  // Ensure that element remains visible multiple times before returning to handle rerendering scenarios
  static getRenderedElement(
    locator: string,
    waitTime: number = MIN_RENDERING_WAIT_TIME
  ) {
    // Setting a wait time lower than MIN_RENDERING_WAIT_TIME can cause flaky tests
    if (waitTime < MIN_RENDERING_WAIT_TIME) {
      waitTime = MIN_RENDERING_WAIT_TIME;
    }
    cy.wait(waitTime);

    return cy
      .get(locator)
      .should("be.visible")
      .get(locator)
      .should("be.visible")
      .get(locator)
      .should("be.visible")
      .get(locator);
  }

  static clickOnOptionalElement(
    locator: string,
    timeout: number,
    elementIndex: number = 0
  ) {
    let isElementPresent = false;
    let elementCount = 0;
    cy.get("body", { log: false }).then((body) => {
      elementCount = body.find(locator).eq(elementIndex).length;
      if (elementCount > 0) {
        isElementPresent = true;
        cy.get(locator).eq(elementIndex).click();
      }
    });

    cy.wait(2000, { log: false }); // Wait for the element to be removed from DOM
    cy.get("body", { log: false }).then((body) => {
      let updatedElementCount = body.find(locator).eq(elementIndex).length;
      if (updatedElementCount < elementCount) {
        cy.wait(timeout, { log: false });
      }
    });

    return isElementPresent;
  }

  static waitIfOptionalElementPresent(
    locator: string,
    timeout: number,
    elementCount: number = 1
  ) {
    let isElementPresent = false;
    cy.get("body", { log: false }).then((body) => {
      if (body.find(locator).length == elementCount) {
        isElementPresent = true;
        cy.wait(timeout, { log: false });
      }
    });

    return isElementPresent;
  }

  static isError(responseStatus: string, errorMessage: string) {
    if (
      ["failed", "failure", "error", "Error", "ERROR"].includes(responseStatus)
    ) {
      throw Error(errorMessage);
    }
  }
}
