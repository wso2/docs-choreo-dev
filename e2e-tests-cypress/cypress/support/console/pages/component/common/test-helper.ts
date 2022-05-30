/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Utils } from "../../../utils";
import { APITest } from "../../apis/api-test";
import { Environment } from "../../enum/environment";
import { HTTPMethod } from "../../enum/http-method-enum";
import { ComponentTestPage } from "../component-test-page";
import { Curl } from "../UI-components/curl-component";
import { SwaggerUI } from "../UI-components/swagger-UI-component";

export class TestHelper {
  public static testOnSwagger(
    env: Environment,
    resourcePath: string,
    key: string = "",
    value: string = ""
  ) {
    APITest.testAPI();
    ComponentTestPage.selectEnvironment(env);
    ComponentTestPage.getTestKey();
    SwaggerUI.invokeResource(resourcePath,key,value);

    return SwaggerUI.getResponseCode().then((res) => {
      return SwaggerUI.GetResponse().then((r) => {
        return cy.wrap({
          response: r,
          statusCode: res,
        });
      });
    });
  }

  public static testOnCurl(env: Environment, httpMethod: HTTPMethod,pathParm:string,queryParameters1=[]) {
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(env);
    Curl.selectMethod(httpMethod);
    Curl.enterPathParameter(pathParm);
    Curl.addQueryParameter(queryParameters1);
    return Curl.getRequestComponents(`${env}${pathParm}`);
  }
}

// static invokeResource(
//   resource: string,
//   key: string = "",
//   value: string = ""
// ) {
//   this.SelectResource(resource);
//   this.TryoutAPI();
//   if (key) {
//     this.enterValue(key, value);
//   }
//   this.ExecuteResourceFunction(resource);
// }
// }
// ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
// ComponentTestPage.getTestKey();
// SwaggerUI.SelectResource("root");
// SwaggerUI.TryoutAPI();
// SwaggerUI.enterValue("number", "2");
// SwaggerUI.ExecuteResourceFunction("root");
// SwaggerUI.GetResponse().should("eq", "4");
// SwaggerUI.getResponseCode().should("eq", "200");
