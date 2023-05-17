export const cyGet = (selector: string) => {
    cy.get(selector).as('element')
    return cy.get('@element')
}