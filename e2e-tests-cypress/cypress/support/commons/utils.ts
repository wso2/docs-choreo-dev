import { MIN_RENDERING_WAIT_TIME } from "./constants";
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
  static automatedNamePrefix = "e2etest";
  static keyNamePrefix = "e2eOnPremkey";
  static APP_SVC_URL = Cypress.env("appSvcURL");
  static ORG_NAME = Cypress.env("choreoOrgHandle");
  static MAIL_READER_SVC_URL = Cypress.env("mailReaderSvcURL");
  static ASGARDEO_MAIL_SENDER = Cypress.env("asgardeoMailSender");
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

  static generateName(suffix: string = "", timestamp: number): string {
    return `${this.automatedNamePrefix}${timestamp}${suffix}`;
  }

  static generatePassword(): string {
    const length = Math.floor(Math.random() * (30 - 8 + 1)) + 8; // Random length between 8 and 30
    const characters = {
      uppercase: 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
      lowercase: 'abcdefghijklmnopqrstuvwxyz',
      number: '0123456789',
    };

    const generateRandomChar = (charSet: string) => charSet.charAt(Math.floor(Math.random() * charSet.length));

    const password = [
      generateRandomChar(characters.uppercase),
      generateRandomChar(characters.lowercase),
      generateRandomChar(characters.number),
      ...Array.from({ length: length - 3 }, () => generateRandomChar(characters.uppercase + characters.lowercase + characters.number))
    ];

    // Shuffle the password array to avoid predictable patterns and join to form the final password string
    return password.sort(() => Math.random() - 0.5).join('');
  }

  static generateUserDetails() {
    const timestamp = Date.now();
    const firstName = this.generateName("firstName", timestamp);
    const lastName = this.generateName("lastName", timestamp);
    const email = `${firstName.toLowerCase()}.${lastName.toLowerCase()}@choreo.e2e.com`;
    const password = this.generatePassword();

    return { firstName, lastName, email, password };
  }

  /**
    * See https://stackoverflow.com/questions/3115150/how-to-escape-regular-expression-special-characters-using-javascript
    */

  static escapeRegExp(text) {
    return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
  }

  static replaceLineBreaks(text: string) : string {
    return text.replace(/(\r\n|\n|\r)/gm, "");
  }
  
  static replaceTrailingSlash(text: string) : string {
    return text.replace(/\/$/, "");
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

  static acceptEmailInviteToOrg(timestamp: string, retryCount = 0) {
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
      this.sendGetRequest(
        Utils.MAIL_READER_SVC_URL +
          timestamp +
          "&senderEmail=" +
          Utils.ASGARDEO_MAIL_SENDER,
        {
          Authorization: `Bearer ${accessToken}`,
        }
      ).then((res) => {
        if (res.status == 200 && res.body != "") {
          const rawMailContent = res.body;
          const decodedMail = window.atob(rawMailContent);
          console.log(rawMailContent);

          const asgardeocUrlRegex =
            /https:\/\/[a-zA-Z0-9.-]+\/invite-user-register\?email=[^'"]+/im;
          const match = asgardeocUrlRegex.exec(decodedMail);
          const asgardeoAcceptUrl = match
            ? match[0].replace(/&amp;/g, "&")
            : null;

          if (asgardeoAcceptUrl) {
            cy.window().then((win) => {
              win.open(asgardeoAcceptUrl, "_blank");
              cy.wait(2000);
              cy.window().then((newWin) => {
                cy.wrap(newWin.document.body).should(
                  "not.contain",
                  "Registration failed"
                );
              });
            });
          } else {
            cy.log("Asgardeo redirect URL not found");
          }
        } else {
          cy.log(`Error while reading email: ${res.status}`);
          retryCount++;
          this.acceptEmailInviteToOrg(timestamp, retryCount);
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

  static isTestConsoleOnly() {
    const consoleOnlyMode = Cypress.env("consoleOnlyMode");

    let isConsoleOnlyMode = false;

    if (consoleOnlyMode != null) {
      isConsoleOnlyMode = consoleOnlyMode == true || consoleOnlyMode == "true";
    }

    return cy.wrap(isConsoleOnlyMode, { log: false });
  }

  static isApiConfigurationEnabled() {
    const enableApiConfiguration = Cypress.env("enableApiConfiguration");

    if (enableApiConfiguration != null) {
      return enableApiConfiguration == true || enableApiConfiguration == "true";
    }

    return false;
  }

  static isKubeConFeaturesEnabled(enableSpecificFeature: boolean = true) {
    let isNewFeaturesActivated = false;
    const enableKubeConFeatures = Cypress.env("enableKubeConFeatures");
    if (enableKubeConFeatures != null) {
      isNewFeaturesActivated =
        enableKubeConFeatures == true || enableKubeConFeatures == "true";
    }

    return isNewFeaturesActivated && enableSpecificFeature;
  }

  static isBuildDeployEnabled() {
    const enableBuildDeploy = Cypress.env("enableBuildDeploy");

    if (enableBuildDeploy != null) {
      return enableBuildDeploy == true || enableBuildDeploy == "true";
    }

    return false;
  }

  static isWebAppAuthenticationEnabled() {
    const enableWebAppAuthentication = Cypress.env(
      "enableWebAppAuthentication"
    );

    if (enableWebAppAuthentication != null) {
      return (
        enableWebAppAuthentication == true ||
        enableWebAppAuthentication == "true"
      );
    }

    return false;
  }

  static isNewUserManagementEnabled() {
    const enableNewUserManagement = Cypress.env("enableNewUserManagement");

    if (enableNewUserManagement != null) {
      return (
        enableNewUserManagement == true || enableNewUserManagement == "true"
      );
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

    cy.log(`Waiting ${waitTime}ms for element ${locator} to render`);
    cy.wait(waitTime, { log: false });

    return cy
      .get(locator)
      .should("be.visible")
      .get(locator, { log: false })
      .should("be.visible", { log: false })
      .get(locator, { log: false })
      .should("be.visible", { log: false })
      .get(locator, { log: false });
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

  static isChecked(locator: string) : Cypress.Chainable<boolean> {
    return cy.get(locator)
      .invoke("attr", "class")
      .then((clazz) => {
        if (clazz && clazz.includes("Mui-checked")) {
          return cy.wrap(true);
        } else {
          return cy.wrap(false);
        }
      });
  }

  static unCheckIfChecked(locator: string) {
    this.isChecked(locator).then((isChecked) => {
      if (isChecked) {
        cy.get(locator).click();
      }
    });
  }

  static checkIfUnchecked(locator: string) {   
    this.isChecked(locator).then((isChecked) => {
      if (!isChecked) {
        cy.get(locator).click();
      }
    });
  }
}
