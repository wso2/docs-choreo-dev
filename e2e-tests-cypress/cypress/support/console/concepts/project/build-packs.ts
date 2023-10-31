import { TestIds } from "../../constants/TestIds";

export class _BuildPacks {
  createProxy() {
    cy.get(TestIds.proxyBuildPack).should("be.visible").click();
  }
}
