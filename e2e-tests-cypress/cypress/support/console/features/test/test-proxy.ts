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

import { Enums } from "../../../commons/enums";
import { Utils } from "../../../commons/utils";
import { TestHelper } from "../../pages/component/common/test-helper";
import { ProxyLeftMenu } from "../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "../../concepts/component/proxy/proxy-component";
import { Types } from "../../../commons/types";
import { DeploymentTrack } from "../deployment-track/deployment-track";

export interface TestProxyFeature {
  _testSwaggerConsole(
    component: Proxy,
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  );

  _testCurl(
    component: Proxy,
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  );
}

export function mixinTestProxy<T extends Types.Constructor>(
  base: T
): Types.Constructor<TestProxyFeature> & T {
  return class extends base {
    private sideMenu = new ProxyLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    _testSwaggerConsole(
      component: Proxy,
      environment: Enums.Environment,
      resource: string,
      key?: string,
      value?: string
    ) {
      this.sideMenu.navigateToTest();

      this.deploymentTrack.validate(component);

      return TestHelper.testOnSwagger(environment, resource, key, value);
    }

    _testCurl(
      component: Proxy,
      environment: Enums.Environment,
      method: Enums.HTTPMethod,
      resource: string
    ) {
      this.sideMenu.navigateToTest();

      this.deploymentTrack.validate(component);

      return TestHelper.testOnCurl(environment, method, resource).then(
        (curl) => {
          return Utils.sendGetRequest(curl.url, curl.headers);
        }
      );
    }
  };
}
