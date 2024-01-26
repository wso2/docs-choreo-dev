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

export namespace Types {
  export interface ResourcePath {
    path: string;
    verbs: string[];
  }

  export type Constructor<T = any> = new (...args: any[]) => T;
}

export class ConfigEntryStep {
  configEntryFunction: ((args?: string[]) => void) | undefined;
  args: string[] | undefined;

  constructor(config?: (args?: string[]) => void, args?: string[]) {
    if (config !== undefined) {
      this.configEntryFunction = config;
    }

    if (args !== undefined) {
      this.args = args;
    }
  }
}

export function createDefaultSteps(numberOfSteps: number) {
  const configSteps: ConfigEntryStep[] = [];
  for (let i = 0; i < numberOfSteps; i++) {
    configSteps.push(new ConfigEntryStep());
  }
  return configSteps;
}
