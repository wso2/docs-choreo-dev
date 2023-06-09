import { cyGet } from "../../../../commons/cy";

export class UnifiedMenu {


    static hoverToMenu() {
       cy.xpath('//div[@id="root"]/div/div/div/div[2]/div[1]').realHover().wait(2000)
    }



    static selectLifecycle() {
        this.hoverToMenu()
        cyGet('[data-cyid="link-manage"]').click()
    }


    static selectSettings() {
        this.hoverToMenu()
        cyGet('[data-cyid="manage-settings"]').click()
    }


    static selectDevelop(){
        this.hoverToMenu()
        cyGet('[data-cyid="link-deploy"]').click()
    }

    static selectResources(){
        this.hoverToMenu()
        cyGet('[data-cyid="link-develop"]').click()
        cyGet('[data-cyid="develop-resources"]').click()
    }


    static selectTestView(){
        this.hoverToMenu()
        cy.get('[data-cyid="link-test"]').should("be.visible").click();
        cy.get('[id="backdrop-loader"]').should("not.exist");
    }
}