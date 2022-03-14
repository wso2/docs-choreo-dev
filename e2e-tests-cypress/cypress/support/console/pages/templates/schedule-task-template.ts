import { Utils } from "../../utils";

export class ScheduleTask {
  static selectTask() {
    cy.get('[data-testid="project-template-list-scheduleTask"]').click();
  }

  static createTask(name: string, description: string) {
    cy.get('[name="name"]').clear().type(name);
    cy.get('[data-cyid="btn-create-"]').click();
    Utils.saveProjectData();
    cy.get('[data-testid="component-develop-edit-code"]', { timeout: 120000 });
    Utils.saveComponentURL();
  }
}
