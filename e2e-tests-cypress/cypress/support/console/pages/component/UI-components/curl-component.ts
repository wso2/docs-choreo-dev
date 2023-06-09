import { cyGet } from "../../../../commons/cy";
import { Enums } from "../../../../commons/enums";
import { CurlData } from "../../../../interfaces/curl-data";

export class Curl {
  static selectMethod(httpMethod: string) {
    cyGet('[data-testid="curl-select-method"]').click();
    cy.get(`[data-testid="curl-${httpMethod.toLowerCase()}"]`).click();
  }

  static addQueryParameter(parameters: { key: string; value: string }[]) {
    cy.wait(1000);
    for (let i = 0; i < parameters.length; i++) {
      cy.get('[data-testid="add-btn"]').click().wait(1000);
      cy.get('[id*="mui"]').eq(0).type(parameters[i].key);
      cy.get('[id*="mui"]').eq(1).type(parameters[i].value);
      cy.get('td button[class*="MuiIconButton-colorInherit"]')
        .eq(2 * i)
        .click();
    }
  }

  static selectCurlEnvironment(env: Enums.Environment) {
    cy.get('[data-testid="env"]>div[role="button"]').click();
    cy.get("ul>li").contains(env).click();
  }

  static selectEnvironment(env: Enums.Environment) {
    cy.get('[data-cyid="select-env"]').click();
    cy.get('[data-cyid="item-env-name"]').contains(env).click();
  }

  static getRequestComponents(env: string) {
    const curlData = Cypress.env(`${env}`);
    let curl: CurlData = {
      method: "",
      url: "",
      headers: { "api-key": "" },
    };
    if (curlData) {
      curl.headers = curlData["headers"];
      curl.method = curlData["method"];
      curl.url = curlData["url"];
      return cy.wrap(curl);
    }

    return cy
      .get("textarea")
      .invoke("text")
      .then((c) => {
        const modifiedURL = c.replace(/"/g, "").replace(/'/g, "");
        const arrayURL = modifiedURL.split(" ");
        const url = arrayURL[1];
        const apiKey = arrayURL[4];
        const method = arrayURL[6];

        curl.headers["api-key"] = apiKey;
        curl.url = url;
        curl.method = method;
        Cypress.env(`${env}`, curl);
        return cy.wrap(curl);
      });
  }

  static getRequestComponentsDiscardPrevious(env: string) {
    return cy
      .get("textarea")
      .invoke("text")
      .then((c) => {
        const modifiedURL = c.replace(/"/g, "").replace(/'/g, "");
        const arrayURL = modifiedURL.split(" ");
        const url = arrayURL[1];
        const apiKey = arrayURL[4];
        const method = arrayURL[6];
        const curl = { method, url, headers: { "api-key": apiKey } };
        return cy.wrap(curl);
      });
  }

  static enterPathParameter(pathParameter: string) {
    cy.get("#path-id").clear().type(pathParameter);
  }

  static getRequestComponentsForService(env: string) {
    const curlData = Cypress.env(`${env}`);
    let curl: CurlData = {
      method: "",
      url: "",
      headers: { "api-key": "" },
    };
    if (curlData) {
      curl.headers = curlData["headers"];
      curl.method = curlData["method"];
      curl.url = curlData["url"];
      return cy.wrap(curl);
    }

    return cy
      .get('[class="language-bash"]')
      .invoke("text")
      .then((c) => {
        const modifiedURL = c.replace(/"/g, "").replace(/'/g, "");
        const arrayURL = modifiedURL.split(" ");
        console.log("arrayURL", arrayURL);
        const url = arrayURL[5];
        const apiKey = arrayURL[13];
        const method = arrayURL[3];

        curl.headers["api-key"] = apiKey;
        curl.url = url;
        curl.method = method;
        Cypress.env(`${env}`, curl);
        return cy.wrap(curl);
      });
  }
}
