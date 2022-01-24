import { Environment } from "../enum/environment";

export class InsightsPage {
  static selectEnvironment(env: Environment) {
    cy.contains("Environment").should("be.visible");
    cy.contains("Environment").next().click();
    cy.contains(`${env}-Choreo`).click();

  }

  static selectTimePeriod(timePeriod: string = "Past 15 minutes") {
    cy.get('div[class*="analytics"]>button').eq(0).click();
    cy.wait(3000)
    cy.get('ul>div').contains(timePeriod).click();
    cy.wait(80000)
  }

  static getTotalTraffic() {
    cy.get('.recharts-area').should('be.visible')
    cy.contains("Total Traffic").should("be.visible");
    return cy.get("main").find("span>span").eq(0).invoke("text");
  }

  static getTotalErrorRequestCount() {
    return cy.get("main").find("span>span").eq(1).invoke("text");
  }

  static getAverageErrorRate() {
    return cy.get("main").find("span>span").eq(2).invoke("text");
  }
}
