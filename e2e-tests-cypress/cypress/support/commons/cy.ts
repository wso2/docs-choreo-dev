export const cyGet = (selector: string,config?:any) => {
    cy.get(selector,config).as('element')
    return cy.get('@element')
}