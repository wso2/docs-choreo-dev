export class GreetingSample{

    static selectSample(){
        cy.get('.choreo-sample-list .MuiGrid-item h3').should('have.length.greaterThan',2).each($s=>{
            cy.log($s.text())
            if($s.text()==='Greetings'){
                $s.trigger('click')
            }
        })
    }
}