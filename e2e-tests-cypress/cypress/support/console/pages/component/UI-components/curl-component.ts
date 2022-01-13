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

  static sendCurlRequest() {
    cy.get('textarea')
      .invoke('text')
      .then((curl) => {
        const modifiedURL = curl.replace(/"/g, '').replace(/'/g, '');
        const arrayURL = modifiedURL.split(' ');
        const url = arrayURL[1];
        const apiKey = arrayURL[4];
        const method = arrayURL[6];

        const request = {
          method,
          url,
          headers: {
            'api-key': apiKey,
          },
        };

        cy.request(request).then((res) => {
          cy.log(res.body);
        });
      });
  }
}
