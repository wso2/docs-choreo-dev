import { Environment } from "../../enum/environment";

export class Curl {
  static selectMethod(httpMethod: string) {
    cy.get('[data-testid="curl-select-method"]').click();
    cy.get(`[data-testid="curl-${httpMethod.toLowerCase()}"]`).click();
  }

  static addQueryParameter(parameters: object[]) {
    cy.wait(1000);
    for (let i = 0; i < parameters.length; i++) {
      cy.get('[data-testid="add-btn"]').click();
      cy.wait(1000);
      cy.get('[id*="mui"]').eq(0).type(parameters[i].key);
      cy.get('[id*="mui"]').eq(1).type(parameters[i].value);
      cy.get('td button[class*="MuiIconButton-colorInherit"]')
        .eq(2 * i)
        .click();
    }
  }
  static selectEnvironment(env: Environment) {
    cy.get('[data-testid="add-btn"]').should("be.visible");
    cy.get('[aria-haspopup="listbox"]').eq(1).click();
    cy.get("ul>li").contains(env).click();
  }

  static getRequestComponents( env: string) {
    const curlData = Cypress.env(`${env}`);
    if (curlData) {
      return cy.wrap(curlData);
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
        const curl = { method, url, headers: { "api-key": apiKey } };
        Cypress.env(`${env}`, curl);
        return cy.wrap(curl);
      });

  }

  static enterPathParameter(pathParameter: string) {
    cy.get("#path-id").clear().type(pathParameter);
  }
}
