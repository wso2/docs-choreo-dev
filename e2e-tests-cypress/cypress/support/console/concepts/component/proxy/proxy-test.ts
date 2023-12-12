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
