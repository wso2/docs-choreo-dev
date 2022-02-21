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

  static getRequestComponents(fileID, env: string) {
    return cy
      .task("readFile", `${Cypress.env("tempFile")}${fileID}.json`)
      .then((data) => {
        if (data[env.toLowerCase()]) {
          return cy.wrap(data[env.toLowerCase()]);
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
            cy.task("writeTestData", {
              fileName: fileID,
              key: env.toLowerCase(),
              value: curl,
            });
            return cy.wrap(curl);
          });
      });
  }

  static enterPathParameter(pathparmeter: string) {
    cy.get("#path-id").clear().type(pathparmeter.replace(/\//g, ""));
  }
  static sendCurlRequest() {
    cy.get("textarea")
      .invoke("text")
      .then((curl) => {
        const modifiedURL = curl.replace(/"/g, "").replace(/'/g, "");
        const arrayURL = modifiedURL.split(" ");
        const url = arrayURL[1];
        const apiKey = arrayURL[4];
        const method = arrayURL[6];

        const request = {
          method,
          url,
          headers: {
            "api-key": apiKey,
          },
        };

        cy.request(request).then((res) => {});
      });
  }
}
