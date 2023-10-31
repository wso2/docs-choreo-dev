import { Enums } from "../../../../commons/enums";
import { Utils } from "../../../../commons/utils";
import { TestHelper } from "../../../pages/component/common/test-helper";
import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "./proxy-component";
import { ProxyUtils } from "./proxy-utils";

export class _ProxyTest {
  private sideMenu = new ProxyLeftMenu();

  testSwaggerConsole(
    component: Proxy,
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  ) {
    this.sideMenu.navigateToTest();

    ProxyUtils.validateDeploymentTrack(component);

    return TestHelper.testOnSwagger(environment, resource, key, value);
  }

  testCurl(
    component: Proxy,
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  ) {
    this.sideMenu.navigateToTest();

    ProxyUtils.validateDeploymentTrack(component);

    return TestHelper.testOnCurl(environment, method, resource).then((curl) => {
      return Utils.sendGetRequest(curl.url, curl.headers);
    });
  }
}
