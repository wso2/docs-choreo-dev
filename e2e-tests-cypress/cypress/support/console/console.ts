/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

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
