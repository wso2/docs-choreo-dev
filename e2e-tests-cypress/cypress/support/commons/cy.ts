export const cyGet = (selector: string, config?: any) => {
  cy.get(selector, config).as("element");
  return cy.get("@element");
};

export const cyLog = (obj: any) => {
  cy.log(JSON.stringify(obj));
};

export const type = (selector: string, value: string) => {
  cyGet(selector).clear().type(value);
};
