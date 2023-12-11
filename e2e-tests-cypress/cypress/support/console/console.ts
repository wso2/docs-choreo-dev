import { Project } from "./concepts/project/project";
import { login } from "./concepts/login/login";

/**
 * Represents the Choreo Console, the entry point for all tests.
 */
class Console {
  static projectNamePrefix = "autotest";

  login() {
    login.login();
  }

  logout() {
    cy.request(login.getSignOutUrl()).then(() => {
      cy.clearAllSessionStorage();
      cy.clearLocalStorage();
      cy.clearAllCookies();
      cy.clearAllLocalStorage();
    });
  }

  navigateToHome() {
    cy.get('[data-cyid="organization-home"]').click();
  }

  createNewProject(description: string): Project {
    return new Project(this.generateProjectName(), description);
  }

  private generateProjectName() {
    return `${Console.projectNamePrefix}${Date.now()}`;
  }
}

// Singleton instance of Choreo Console
export const console = new Console();
