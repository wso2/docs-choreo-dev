import { Enums } from "../../../../commons/enums";
import { TestHelper } from "../../../pages/component/common/test-helper";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";

export interface InvokeInfo {
  env: Enums.Environment;
  endpoint: string;
  resourcePath: string;
  method?: string;
  parentComponentId: string;
  key?: string;
  value?: string;
}

export class _ServiceTest {
  private sideMenu = new ServiceLeftMenu();

  testConsole(component: Service, invokeInfo: InvokeInfo) {
    this.sideMenu.navigateToTest();

    const version = component.getLatestVersion();

    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    return TestHelper.testManagedEndpoint(
      invokeInfo.env,
      invokeInfo.endpoint,
      invokeInfo.resourcePath,
      invokeInfo.method,
      invokeInfo.parentComponentId,
      invokeInfo.key,
      invokeInfo.value
    );
  }
}
