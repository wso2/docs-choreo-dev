export class TestView {

    static navigatTestView() {
        cy.get('#test').click()
        cy.get('[data-testid="backdrop-loader"]').should('not.exist');
    }


static getTestURL(){
return  cy.get('[data-testid="product-tour-log-panel"] input').eq(0).invoke('attr', 'value')
}
    static clickTestRunButton() {
        cy.get('#test-run-btn').click()
    }
    static getTestKey() {
        cy.get('[data-testid="get-test-key-btn"]').click()
    }

}