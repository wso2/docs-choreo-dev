import { TestIds } from "../../../constants/TestIds";
import { Proxy } from "./proxy-component";

export class ProxyUtils {
  static validateDeploymentTrack(component: Proxy) {
    cy.get(TestIds.backdropLoader).should("not.exist");

    const version = component.getLatestVersion();

    cy.get(TestIds.versionPicker).contains(`v${version}`);
  }
}
