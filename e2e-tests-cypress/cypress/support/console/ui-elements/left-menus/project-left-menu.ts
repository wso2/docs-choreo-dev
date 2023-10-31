import { LeftMenu } from "./left-menu";

export class ProjectLeftMenu extends LeftMenu {
  navigateToHome() {
    this.navigateToMenuItem("[data-cyid=home]");
  }

  navigateToComponents() {
    this.navigateToMenuItem("[data-cyid=listing]");
  }
}
