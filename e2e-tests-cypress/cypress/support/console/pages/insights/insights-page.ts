import { Environment } from "../enum/environment";

export class InsightsPage {
  static selectEnvironment(env: Environment) {
    cy.contains("Environment").should("be.visible");
    cy.contains("Environment").next().click();
    cy.contains(`${env}-Choreo`).click();
  }

  static selectTimePeriod(timePeriod: string = "Past 15 minutes") {
    cy.contains("hours").click();
    cy.contains(timePeriod).click();
  }

  static getTotalTraffic() {
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
